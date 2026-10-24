<p align="center">
  <img src="docs/logo.svg" width="128" height="128" alt="Whisper Vault Logo">
</p>

<p align="center"><b>Deutsch</b> · <a href="#english">English below</a></p>

# Whisper Vault

Diktieren per Knopfdruck für Android, und jedes Diktat bleibt erhalten.

Whisper Vault ist ein Fork von [Phone Whisper](https://github.com/kafkasl/phone-whisper) von Pol Alvarez. Die Grundidee bleibt: in fast jede App hineinsprechen, ohne die Tastatur zu wechseln. Neu ist, dass jedes Diktat zusätzlich als Datei gespeichert wird. So geht nichts verloren, und die Dateien können im heimischen WLAN an den eigenen PC gehen.

Was die App kann:

- **Lokale Spracherkennung** direkt auf dem Handy mit sherpa-onnx
- **Spracherkennung in der Cloud** mit OpenAI Whisper
- **Optionale Nachbearbeitung** mit OpenAI für Satzzeichen und Grammatik
- **Diktat-Archiv**: jede Aufnahme wird als WAV + TXT auf dem Handy gespeichert
- **Übertragung an den PC**: die Dateien gehen an einen kleinen Empfänger auf dem PC, während das Handy im WLAN lädt

## Was dieser Fork ändert

- Eigene App-ID `de.tinozi.whispervault`, dadurch lässt sich die App neben Phone Whisper installieren
- Jedes Diktat wird als `Documents/Sportchronik/<yyyy-MM-dd_HHmmss_xxxx>.wav` und `.txt` (Rohtext) gespeichert
- Die Aufnahme startet erst, wenn der Knopf eine halbe Sekunde gehalten wird, ein kurzer Tipp beendet sie
- Taschen-Schutz: bei ausgeschaltetem oder gesperrtem Bildschirm ist der Knopf aus, Aufnahmen enden nach 15 Minuten, Start und Stopp vibrieren
- Übertragung an einen PC-Empfänger im selben Netz (siehe unten)
- Diagnose: die App zeigt, warum Android sie zuletzt beendet hat, und kann diesen Bericht kopieren

## Installation

Ein fertiges Release gibt es noch nicht. Die App wird aus dem Quellcode gebaut.

Benötigt werden JDK 17 und das Android SDK.

```bash
git clone https://github.com/tinozi25yt0826/whisper-vault.git && cd whisper-vault
make build
```

Die fertige APK liegt hier:

```bash
app/build/outputs/apk/debug/whisper-vault-debug.apk
```

Mit ADB:

```bash
make adb-install
```

## So funktioniert es

1. Ein kleiner Knopf schwebt über dem Bildschirm
2. Knopf eine halbe Sekunde halten, die Aufnahme startet
3. Noch einmal tippen, die Aufnahme endet
4. Die Sprache wird lokal oder in der Cloud in Text umgewandelt
5. Der Text landet im gerade aktiven Textfeld
6. Klappt das Einfügen nicht, liegt der Text in der Zwischenablage
7. Audio und Rohtext werden in `Documents/Sportchronik` gespeichert

## Einrichtung

### Erster Start

1. **Whisper Vault** öffnen
2. Die Berechtigung für **Audioaufnahmen** erteilen
3. Den **Bedienungshilfe-Dienst** (Accessibility Service) einschalten
4. Art der Spracherkennung wählen:
   - **Lokal**: ein Modell in der App herunterladen
   - **Cloud**: den eigenen OpenAI-API-Schlüssel eintragen

Danach ist der schwebende Knopf einsatzbereit.

### Übertragung an den PC (optional)

1. Auf dem PC einen Empfänger starten (nicht Teil dieses Repos, Protokoll siehe unten)
2. In der App unter **PC transfer** die **PC address** (zum Beispiel `192.168.178.22:8770`) und den **Transfer key** eintragen, den der Empfänger anzeigt
3. Die App schickt neue Dateien einmal pro Stunde, wenn das Handy lädt und in einem WLAN ohne Volumenbegrenzung ist. **Transfer now** schickt sie, sobald WLAN da ist

Das Protokoll, falls du einen eigenen Empfänger schreiben willst:

- `GET /hallo?n=<nonce>` muss mit dem HMAC-SHA256 der Nonce (hex, mit dem Transfer key als Schlüssel) antworten. Stimmt die Antwort nicht, wird nichts gesendet
- `PUT /upload/<Dateiname>` mit dem Header `Authorization: Bearer <key>` und der Datei als Inhalt, Antwort `200` oder `201`
- Die App merkt sich, welche Dateien schon gesendet wurden, keine Datei geht doppelt raus

## Warum braucht die App den Bedienungshilfe-Dienst?

Whisper Vault nutzt den Android-Bedienungshilfe-Dienst aus genau einem Grund: um den diktierten Text in das gerade aktive Textfeld einer beliebigen App einzufügen.

Die App ersetzt **nicht** die Tastatur. Sie führt **keine** Automatisierungen im Hintergrund aus. Sie nimmt nur auf, wenn du den Knopf bewusst gedrückt hältst.

## Datenschutz

- **Lokal**: die Sprache wird auf dem Handy in Text umgewandelt
- **Cloud**: die Aufnahme geht direkt vom Handy an die Transkriptions-API von OpenAI
- **Nachbearbeitung**: der erkannte Text geht direkt vom Handy an die Chat-API von OpenAI
- **Archiv**: Audio und Text jedes Diktats bleiben in `Documents/Sportchronik`, bis du sie löschst
- **PC-Übertragung**: nur wenn du sie einrichtest, nur an die Adresse, die du einträgst, per einfachem HTTP im eigenen Netz

Es gibt keinen Server im Hintergrund. Nichts wird an den Entwickler geschickt.

Ausführlich: [PRIVACY.md](PRIVACY.md)

## Lokale Modelle

Die Modelle liegen im App-Speicher unter:

```bash
/data/data/de.tinozi.whispervault/files/models/
```

Aktueller Katalog:

| Modell | Größe | Hinweis |
|---|---:|---|
| Parakeet 110M | 100 MB | Gute Standardwahl |
| Whisper Base | 199 MB | Solide Grundlage |
| Parakeet 0.6B | 465 MB | Beste Qualität, in v3 auch gut für Deutsch |
| Moonshine Tiny | 103 MB | Am schnellsten |

Die App lädt die Modelle direkt aus den Release-Archiven von sherpa-onnx und entpackt sie.

## Entwicklung

```bash
make build       # Debug-APK bauen
make test        # Unit-Tests ausführen
make adb-install # bauen und per ADB installieren
make clean       # Build-Dateien löschen
```

## Kompatibilität mit anderen Apps

Am besten funktioniert Whisper Vault in Apps mit normalen Android-Textfeldern.
Manche Apps nutzen eigene Eingabeflächen oder Terminal-Ansichten, in die sich über die Bedienungshilfe nichts einfügen lässt.
Dann kopiert Whisper Vault den Text in die Zwischenablage.

### Termux

Die Terminal-Fläche von Termux ist kein normales Android-Textfeld, direktes Einfügen klappt dort oft nicht.

So geht es trotzdem:

1. Termux öffnen
2. Die Zusatztasten-Leiste (`ESC`, `CTRL`, `ALT`, Pfeile usw.) nach links oder rechts wischen
3. Zum eigenen Texteingabefeld von Termux wechseln
4. Dort diktieren

Termux schickt den Text aus diesem Feld ganz normal ans Terminal.

## Bekannte Einschränkungen

- Für das Einfügen in andere Apps ist der Bedienungshilfe-Dienst nötig
- Manche Apps blockieren Einfügen oder Texteingabe von außen
- Manche Apps nutzen eigene Eingabeflächen statt normaler Android-Textfelder
- Die lokalen Modelle sind groß
- Für die Cloud braucht es einen eigenen OpenAI-API-Schlüssel
- Der Archivordner ist fest auf `Documents/Sportchronik` eingestellt
- Die PC-Übertragung hat keine eigene Verschlüsselung, also nur in einem vertrauenswürdigen Netz nutzen
- Die Oberfläche der App ist noch auf Englisch

## Danksagung

Basiert auf [Phone Whisper](https://github.com/kafkasl/phone-whisper) von Pol Alvarez. Wenn dir das Original Zeit spart, kannst du ihn [auf GitHub unterstützen](https://github.com/sponsors/kafkasl).

## Lizenz

Apache License 2.0, siehe [LICENSE](LICENSE). Die oben genannten Änderungen stammen von Tino Zimmermann.

---

<a id="english"></a>

# English

Push-to-talk dictation for Android that keeps every dictation.

Whisper Vault is a fork of [Phone Whisper](https://github.com/kafkasl/phone-whisper) by Pol Alvarez. It keeps the original idea (speak into most apps without switching keyboards) and adds one thing: every dictation is also saved as a file, so nothing is lost, and the files can be sent to your own PC on your home Wi-Fi.

It supports:

- **Local on-device transcription** with sherpa-onnx
- **Cloud transcription** with OpenAI Whisper
- **Optional cleanup** with OpenAI to fix punctuation and grammar
- **A dictation archive**: every recording is saved as WAV + TXT on the phone
- **Transfer to your PC**: archived files go to a small receiver on your PC while the phone charges on Wi-Fi

## What this fork changes

- Own app id `de.tinozi.whispervault`, so it installs next to Phone Whisper
- Every dictation is saved as `Documents/Sportchronik/<yyyy-MM-dd_HHmmss_xxxx>.wav` and `.txt` (raw transcript)
- Recording starts only after holding the button for 0.5 s, a short tap stops it
- Pocket guard: the button is off while the screen is off or locked, recordings stop after 15 minutes, and start/stop vibrate
- Transfer to a PC receiver in the same network (see below)
- Diagnostics: the app shows why it was last stopped by Android and can copy that report

## Install

There is no release yet. Build from source.

Requires JDK 17 and Android SDK.

```bash
git clone https://github.com/tinozi25yt0826/whisper-vault.git && cd whisper-vault
make build
```

APK output:

```bash
app/build/outputs/apk/debug/whisper-vault-debug.apk
```

If you use ADB:

```bash
make adb-install
```

## How it works

1. A small overlay button floats on screen
2. Hold it for half a second to start recording
3. Tap again to stop
4. Audio is transcribed locally or in the cloud
5. The text is inserted into the focused text field
6. If insertion fails, the text is copied to the clipboard
7. Audio and raw transcript are saved in `Documents/Sportchronik`

## Setup

### First-time setup

1. Open **Whisper Vault**
2. Grant the **audio recording** permission
3. Enable the **Accessibility Service**
4. Choose your transcription mode:
   - **Local**: download a model in the app
   - **Cloud**: paste your OpenAI API key

Once setup is done, the floating button is ready.

### Transfer to a PC (optional)

1. Run a receiver on your PC (not part of this repo, see the protocol below)
2. In the app, set the **PC address** (for example `192.168.178.22:8770`) and the **Transfer key** shown by the receiver
3. The app sends new files once an hour while the phone is charging on an unmetered network. **Transfer now** sends as soon as Wi-Fi is available

Protocol, if you want to write your own receiver:

- `GET /hallo?n=<nonce>` must answer with the hex HMAC-SHA256 of the nonce, using the transfer key. If the answer is wrong, nothing is sent
- `PUT /upload/<file name>` with header `Authorization: Bearer <key>` and the file as body, answered with `200` or `201`
- Sent file names are remembered in the app, files are never sent twice

## Why does it need Accessibility?

Whisper Vault uses Android Accessibility Service for one narrow reason: to insert dictated text into the currently focused text field across apps.

It does **not** replace your keyboard. It does **not** run background automation. It only records after you explicitly hold the overlay button.

## Privacy

- **Local mode**: audio is transcribed on-device
- **Cloud mode**: audio is sent directly from your device to OpenAI's transcription API
- **Optional cleanup**: transcript text is sent directly from your device to OpenAI's chat API
- **Archive**: audio and transcript of every dictation stay in `Documents/Sportchronik` until you delete them
- **PC transfer**: only if you set it up, and only to the address you entered, over plain HTTP inside your network

There is no backend. Nothing is sent to the developer.

Full policy: [PRIVACY.md](PRIVACY.md#english)

## Local models

Models are stored in app storage under:

```bash
/data/data/de.tinozi.whispervault/files/models/
```

Current catalog:

| Model | Size | Notes |
|---|---:|---|
| Parakeet 110M | 100 MB | Best default |
| Whisper Base | 199 MB | Solid baseline |
| Parakeet 0.6B | 465 MB | Best quality, also good for German (v3) |
| Moonshine Tiny | 103 MB | Fastest |

The app downloads and extracts models directly from the sherpa-onnx release archives.

## Development

```bash
make build       # build debug APK
make test        # run unit tests
make adb-install # build + install via ADB
make clean       # clean build artifacts
```

## App compatibility

Whisper Vault works best in apps that use standard Android text fields.
Some apps use custom text surfaces or terminal-style views, which may not support direct accessibility paste.
When insertion is not possible, Whisper Vault falls back to copying the transcript to the clipboard.

### Termux

Termux's main terminal area is not a standard Android text field, so direct insertion may not work there.

To use Whisper Vault in Termux:

1. Focus Termux
2. Swipe the extra keys row (`ESC`, `CTRL`, `ALT`, arrows, etc.) left or right
3. Switch to Termux's native text input box
4. Dictate there

Once text is inserted into the native input box, Termux sends it to the terminal normally.

## Current limitations

- Accessibility permission is required for cross-app insertion
- Some apps may block paste or text injection
- Some apps use custom input surfaces instead of standard Android text fields
- Local models are large
- Cloud mode requires your own OpenAI API key
- The archive folder is fixed to `Documents/Sportchronik`
- The PC transfer has no encryption of its own, use it only in a network you trust

## Credits

Based on [Phone Whisper](https://github.com/kafkasl/phone-whisper) by Pol Alvarez. If the original saves you time, consider [sponsoring him](https://github.com/sponsors/kafkasl).

## License

Apache License 2.0, see [LICENSE](LICENSE). The changes listed above were made by Tino Zimmermann in this fork.
