import Foundation

public class MemoTalkSDK {

    private static var instance: MemoTalkSDKImpl?

    public static func initialize(config: SDKConfig) -> Result<Void, PlaudError> {
        do {
            instance = MemoTalkSDKImpl(config: config)
            return .success(())
        } catch {
            return .failure(PlaudError(code: 0, message: "SDK initialization failed: \(error.localizedDescription)"))
        }
    }

    public static func getInstance() -> MemoTalkSDKImpl? {
        return instance
    }

    public static func getDeviceManager() -> DeviceManager? {
        return instance?.deviceManager
    }

    public static func getRecordingManager() -> RecordingManager? {
        return instance?.recordingManager
    }

    public static func getFileManager() -> FileManager? {
        return instance?.fileManager
    }

    public static func getOTAManager() -> OTAManager? {
        return instance?.otaManager
    }

    public static func getTranscriptionManager() -> TranscriptionManager? {
        return instance?.transcriptionManager
    }

    public static func release() {
        instance?.release()
        instance = nil
    }
}

public class MemoTalkSDKImpl {

    public let deviceManager: DeviceManager
    public let recordingManager: RecordingManager
    public let fileManager: FileManager
    public let otaManager: OTAManager
    public let transcriptionManager: TranscriptionManager

    private let bleClient: BleClient
    private let config: SDKConfig

    public init(config: SDKConfig) {
        self.config = config
        bleClient = BleClient()
        deviceManager = DeviceManager()
        recordingManager = RecordingManager(bleClient: bleClient)
        fileManager = FileManager(bleClient: bleClient)
        otaManager = OTAManager(bleClient: bleClient)
        transcriptionManager = TranscriptionManager(bleClient: bleClient)
    }

    public func release() {
        bleClient.disconnect()
    }
}
