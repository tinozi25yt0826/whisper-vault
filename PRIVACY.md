# Privacy Policy for Whisper Vault

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
