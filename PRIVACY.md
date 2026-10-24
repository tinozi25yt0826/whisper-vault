<p align="center"><b>Deutsch</b> · <a href="#english">English below</a></p>

# Datenschutzerklärung für Whisper Vault

Whisper Vault ist eine Diktier-App für Android. Sie nimmt Sprache auf, wandelt sie in Text um, fügt ihn in Textfelder anderer Apps ein und bewahrt jedes Diktat als Datei auf. Sie ist ein Fork von Phone Whisper von Pol Alvarez.

## Umgang mit Daten

Whisper Vault bietet zwei Arten der Spracherkennung.

### Lokal

Im lokalen Modus wird die Aufnahme auf dem Gerät mit lokalen Spracherkennungsmodellen verarbeitet. Die Aufnahme wird an keinen Transkriptionsdienst geschickt.

### Cloud

Im Cloud-Modus geht die Aufnahme direkt vom Gerät an die Transkriptions-API von OpenAI, die daraus Text erzeugt.

Ist die optionale Nachbearbeitung eingeschaltet, geht auch der erkannte Text direkt vom Gerät an die Chat-API von OpenAI, um Satzzeichen, Groß- und Kleinschreibung und Lesbarkeit zu verbessern.

## Diktat-Archiv

Jedes Diktat wird auf dem Gerät als Audiodatei (WAV) und als Textdatei mit dem Rohtext im gemeinsamen Ordner `Documents/Sportchronik` gespeichert.

Die Dateien bleiben auf dem Gerät, bis du sie löschst. Weil der Ordner im gemeinsamen Speicher liegt, können Dateimanager, ein angeschlossener Computer und Apps mit Dateizugriff sie lesen.

## Übertragung an deinen PC

Die Übertragung ist aus, bis du in der App eine PC-Adresse und einen Übertragungsschlüssel einträgst.

Ist sie eingerichtet, gehen die Dateien nur an die eingetragene Adresse: automatisch einmal pro Stunde, wenn das Handy lädt und in einem Netz ohne Volumenbegrenzung ist, oder wenn du **Transfer now** antippst. Vor dem Senden muss der PC beweisen, dass er den Übertragungsschlüssel kennt. So landen keine Dateien bei einem fremden Gerät, das zufällig dieselbe Adresse hat.

Die Übertragung nutzt einfaches HTTP im lokalen Netz. Geschützt ist sie nur durch die WLAN-Verschlüsselung, nutze sie also nur in einem Netz, dem du vertraust.

Die App merkt sich in ihrem eigenen Speicher, welche Dateien schon gesendet wurden.

## API-Schlüssel und Übertragungsschlüssel

Dein OpenAI-API-Schlüssel und der Übertragungsschlüssel werden nur auf deinem Gerät im App-Speicher abgelegt. Der OpenAI-Schlüssel dient nur zur Anmeldung bei Anfragen, die direkt an OpenAI gehen, der Übertragungsschlüssel nur für deinen eigenen PC.

Ich betreibe keinen Server, über den diese Anfragen laufen.

## Bedienungshilfe-Dienst

Whisper Vault nutzt den Android-Bedienungshilfe-Dienst nur, um das gerade aktive Textfeld zu finden und dort den diktierten Text einzufügen, nachdem du den schwebenden Knopf bewusst benutzt hast.

Whisper Vault ist nicht dafür gebaut, dein Surfen zu beobachten, Bildschirminhalte für Auswertungen zu sammeln oder im Hintergrund Abläufe zu automatisieren.

## Diagnose

Die App liest die Gründe, die Android für ihr letztes Beenden angibt, und ihren letzten Absturz, und zeigt sie in der App an. Dieser Bericht wird nur in die Zwischenablage kopiert, wenn du ihn antippst. Die App schickt ihn nirgendwohin.

## Datenerhebung

Ich betreibe keinen Server für Whisper Vault und erhebe keine Benutzerkonten, Nutzungsstatistiken oder Aufnahmen.

Dienste anderer Anbieter, die du selbst nutzt, etwa OpenAI, verarbeiten Daten nach ihren eigenen Bedingungen und Datenschutzerklärungen.

## Kontakt

Fragen zum Datenschutz an: sport@tinozi.de

---

<a id="english"></a>

# Privacy Policy for Whisper Vault (English)

Whisper Vault is an Android dictation app that records speech, transcribes it, inserts the result into text fields across apps, and keeps every dictation as a file. It is a fork of Phone Whisper by Pol Alvarez.

## Data handling

Whisper Vault supports two transcription modes.

### Local mode

In local mode, audio is processed on-device using local speech recognition models. Audio is not sent to any transcription service.

### Cloud mode

In cloud mode, recorded audio is sent directly from the device to OpenAI's transcription API to generate text.

If optional cleanup is enabled, the transcribed text is also sent directly from the device to OpenAI's chat API to improve punctuation, capitalization, and clarity.

## Dictation archive

Every dictation is saved on the device as an audio file (WAV) and a text file with the raw transcript in the shared folder `Documents/Sportchronik`.

These files stay on the device until you delete them. Because the folder is in shared storage, file managers, a connected computer, and apps you have given file access can read them.

## Transfer to your PC

The transfer is off until you enter a PC address and a transfer key in the app.

When it is set up, archived files are sent only to the address you entered: automatically once an hour while the phone is charging on an unmetered network, or when you tap **Transfer now**. Before sending, the app asks the PC to prove that it knows the transfer key, so files are not sent to an unknown device that happens to use the same address.

The transfer uses plain HTTP inside your local network. It is protected only by your Wi-Fi encryption, so use it only in a network you trust.

The app remembers the names of files already sent in its own storage.

## API keys and transfer key

Your OpenAI API key and the transfer key are stored locally on your device in app storage. The OpenAI key is only used to authenticate requests sent directly to OpenAI, the transfer key only for your own PC.

I do not operate a relay server for these requests.

## Accessibility Service

Whisper Vault uses Android Accessibility Service only to identify the currently focused text field and insert dictated text after you explicitly interact with the floating overlay button.

Whisper Vault is not designed to monitor browsing, collect screen content for analytics, or perform background automation.

## Diagnostics

The app reads the reasons Android gives for its own last stops and its last crash, and shows them in the app. This report is only copied to the clipboard when you tap it. It is never sent anywhere by the app.

## Data collection

I do not run a backend for Whisper Vault and do not collect user accounts, analytics, or recordings.

Third-party services you choose to use, such as OpenAI, may process data according to their own terms and privacy policies.

## Contact

For questions about privacy, contact: sport@tinozi.de
