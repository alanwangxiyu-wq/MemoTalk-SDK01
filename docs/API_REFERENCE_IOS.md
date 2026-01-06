# MemoTalk SDK for iOS - API Reference

---

## `MemoTalkSDK`

The main entry point for interacting with the SDK.

### `initialize(config: SDKConfig) -> Result<Void, PlaudError>`

Initializes the SDK. This must be called once, typically in your `AppDelegate`, before any other SDK methods are used.

-   **Parameters**:
    -   `config`: An `SDKConfig` object containing your app key and secret.
-   **Returns**: A `Result` object indicating success or failure.

### `getInstance() -> MemoTalkSDKImpl?`

Gets the singleton instance of the SDK implementation.

### `getDeviceManager() -> DeviceManager?`

Gets the `DeviceManager` instance for device scanning and connection.

### `getRecordingManager() -> RecordingManager?`

Gets the `RecordingManager` instance for controlling recordings.

### `getFileManager() -> FileManager?`

Gets the `FileManager` instance for file operations.

### `getOTAManager() -> OTAManager?`

Gets the `OTAManager` instance for firmware updates.

### `getTranscriptionManager() -> TranscriptionManager?`

Gets the `TranscriptionManager` instance for AI services.

### `release()`

Releases all resources used by the SDK. This should be called when your app is shutting down.

---

## `DeviceManager`

Manages device discovery and connection.

### `startScan() async -> Result<[Device], PlaudError>`

Starts scanning for nearby MemoTalk devices.

-   **Returns**: A `Result` containing an array of discovered `Device` objects.

### `stopScan()`

Stops the device scan.

### `connect(device: Device) async -> Result<Void, PlaudError>`

Connects to a specific device.

-   **Parameters**:
    -   `device`: The `Device` object to connect to.

### `disconnect(device: Device) async -> Result<Void, PlaudError>`

Disconnects from a device.

### `getDeviceStatus(device: Device) async -> Result<DeviceStatus, PlaudError>`

Gets the real-time status of a device (battery, storage, etc.).

---

## `RecordingManager`

Controls the recording functionality of the device.

### `startRecording(device: Device) async -> Result<Void, PlaudError>`

Starts a new recording on the device.

### `stopRecording(device: Device) async -> Result<Void, PlaudError>`

Stops the current recording.

### `pauseRecording(device: Device) async -> Result<Void, PlaudError>`

Pauses the current recording.

### `getRecordingList(device: Device) async -> Result<[Recording], PlaudError>`

Retrieves a list of all recordings stored on the device.

---

## `FileManager`

Handles file-related operations.

### `downloadRecording(device: Device, recording: Recording, progressCallback: @escaping (Int) -> Void) async -> Result<URL, PlaudError>`

Downloads a recording file from the device to the app's local cache.

-   **Parameters**:
    -   `recording`: The `Recording` to download.
    -   `progressCallback`: A closure to receive download progress updates (0-100).
-   **Returns**: A `Result` containing the URL of the downloaded file.

### `deleteRecording(device: Device, recording: Recording) async -> Result<Void, PlaudError>`

Deletes a recording from the device.

---

## `OTAManager`

Manages firmware updates.

### `checkFirmwareVersion(device: Device) async -> Result<String, PlaudError>`

Checks the current firmware version of the device.

### `startOTAUpdate(device: Device, firmwareFile: URL, progressCallback: @escaping (Int) -> Void) async -> Result<Void, PlaudError>`

Starts the Over-The-Air firmware update process.

---

## `TranscriptionManager`

Integrates with cloud-based AI services.

### `enableRealtimeTranscription(device: Device) async -> Result<Void, PlaudError>`

Enables the real-time transcription feature.

### `transcribeRecording(device: Device, recording: Recording) async -> Result<String, PlaudError>`

Submits a recording to the cloud for transcription.

-   **Returns**: A `Result` containing the transcription text.
