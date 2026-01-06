import UIKit

class ViewController: UIViewController {

    private var deviceManager: DeviceManager?
    private var recordingManager: RecordingManager?
    private var selectedDevice: Device?

    @IBOutlet weak var statusLabel: UILabel!
    @IBOutlet weak var deviceTableView: UITableView!
    @IBOutlet weak var scanButton: UIButton!
    @IBOutlet weak var startRecordingButton: UIButton!
    @IBOutlet weak var stopRecordingButton: UIButton!

    private var devices: [Device] = []
    private var isRecording = false

    override func viewDidLoad() {
        super.viewDidLoad()

        // Initialize SDK
        let config = SDKConfig(
            appKey: "your-app-key",
            appSecret: "your-app-secret",
            logLevel: .debug
        )

        let initResult = MemoTalkSDK.initialize(config: config)
        switch initResult {
        case .success:
            deviceManager = MemoTalkSDK.getDeviceManager()
            recordingManager = MemoTalkSDK.getRecordingManager()
        case .failure(let error):
            statusLabel.text = "SDK initialization failed: \(error.message)"
        }

        // Setup UI
        deviceTableView.delegate = self
        deviceTableView.dataSource = self

        scanButton.addTarget(self, action: #selector(scanDevices), for: .touchUpInside)
        startRecordingButton.addTarget(self, action: #selector(startRecording), for: .touchUpInside)
        stopRecordingButton.addTarget(self, action: #selector(stopRecording), for: .touchUpInside)

        stopRecordingButton.isEnabled = false
    }

    @objc private func scanDevices() {
        scanButton.isEnabled = false
        statusLabel.text = "Scanning..."

        Task {
            guard let deviceManager = deviceManager else { return }
            let result = await deviceManager.startScan()
            
            DispatchQueue.main.async {
                switch result {
                case .success(let scannedDevices):
                    self.devices = scannedDevices
                    self.statusLabel.text = "Found \(scannedDevices.count) devices"
                    self.deviceTableView.reloadData()
                case .failure(let error):
                    self.statusLabel.text = "Scan failed: \(error.message)"
                }
                self.scanButton.isEnabled = true
            }
        }
    }

    @objc private func startRecording() {
        guard let device = selectedDevice, let recordingManager = recordingManager else {
            statusLabel.text = "No device selected"
            return
        }

        Task {
            let result = await recordingManager.startRecording(device: device)
            
            DispatchQueue.main.async {
                switch result {
                case .success:
                    self.isRecording = true
                    self.statusLabel.text = "Recording started"
                    self.startRecordingButton.isEnabled = false
                    self.stopRecordingButton.isEnabled = true
                case .failure(let error):
                    self.statusLabel.text = "Start recording failed: \(error.message)"
                }
            }
        }
    }

    @objc private func stopRecording() {
        guard let device = selectedDevice, let recordingManager = recordingManager else {
            statusLabel.text = "No device selected"
            return
        }

        Task {
            let result = await recordingManager.stopRecording(device: device)
            
            DispatchQueue.main.async {
                switch result {
                case .success:
                    self.isRecording = false
                    self.statusLabel.text = "Recording stopped"
                    self.startRecordingButton.isEnabled = true
                    self.stopRecordingButton.isEnabled = false
                case .failure(let error):
                    self.statusLabel.text = "Stop recording failed: \(error.message)"
                }
            }
        }
    }

    deinit {
        MemoTalkSDK.release()
    }
}

extension ViewController: UITableViewDelegate, UITableViewDataSource {

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return devices.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DeviceCell", for: indexPath)
        let device = devices[indexPath.row]
        cell.textLabel?.text = device.name ?? "Unknown"
        cell.detailTextLabel?.text = device.address.uuidString
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let device = devices[indexPath.row]
        selectedDevice = device
        statusLabel.text = "Connecting to \(device.name ?? "Unknown")..."

        Task {
            guard let deviceManager = deviceManager else { return }
            let result = await deviceManager.connect(device: device)
            
            DispatchQueue.main.async {
                switch result {
                case .success:
                    self.statusLabel.text = "Connected to \(device.name ?? "Unknown")"
                    self.startRecordingButton.isEnabled = true
                case .failure(let error):
                    self.statusLabel.text = "Connection failed: \(error.message)"
                }
            }
        }
    }
}
