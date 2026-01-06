import Foundation

public enum ConnectionState {
    case disconnected
    case connecting
    case connected
    case disconnecting
}

public struct Device {
    public let name: String?
    public let address: UUID
    public let rssi: Int
    public var connectionState: ConnectionState = .disconnected
}

public struct Recording {
    public let fileId: String
    public let duration: TimeInterval
    public let timestamp: Date
    public let fileSize: Int64
    public var isSynced: Bool = false
}

public struct DeviceStatus {
    public let batteryLevel: Int
    public let storageFree: Int64
    public let storageTotal: Int64
    public let isRecording: Bool
}

public struct SDKConfig {
    public let appKey: String
    public let appSecret: String
    public let logLevel: LogLevel
    
    public init(appKey: String, appSecret: String, logLevel: LogLevel = .info) {
        self.appKey = appKey
        self.appSecret = appSecret
        self.logLevel = logLevel
    }
}

public enum LogLevel {
    case verbose, info, debug, warn, error
}

public struct PlaudError: Error {
    public let code: Int
    public let message: String
}
