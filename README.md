<p align="center">
  <img src="docs/logo.svg" width="128" height="128" alt="Whisper Vault Logo">
</p>

# Whisper Vault

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

Full policy: [PRIVACY.md](PRIVACY.md)

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
