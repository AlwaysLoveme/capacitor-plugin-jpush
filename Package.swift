// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorPluginJpush",
    platforms: [.iOS(.v15)],
    products: [
        .library(
            name: "CapacitorPluginJpush",
            targets: ["JPushPlugin"]
        )
    ],
    dependencies: [
        .package(
            url: "https://github.com/ionic-team/capacitor-swift-pm.git",
            from: "8.0.0"
        )
    ],
    targets: [
        .binaryTarget(
            name: "jcore",
            path: "ios/Sources/JPushPlugin/JPushCoreLib/jcore-ios-5.2.2.xcframework"
        ),
        .binaryTarget(
            name: "jpush",
            path: "ios/Sources/JPushPlugin/JPushCoreLib/jpush-ios-5.9.0.xcframework"
        ),
        .target(
            name: "JPushPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm"),
                "jcore",
                "jpush"
            ],
            path: "ios/Sources/JPushPlugin",
            sources: ["JPush.swift", "JPushPlugin.swift"],
            publicHeadersPath: "JPushCoreLib",
            cSettings: [
                .headerSearchPath("JPushCoreLib"),
            ],
            linkerSettings: [
                // JPush 必需的系统框架
                .linkedFramework("UserNotifications"),     // 推送通知
                .linkedFramework("CFNetwork"),             // 网络请求
                .linkedFramework("CoreTelephony"),         // 运营商信息
                .linkedFramework("SystemConfiguration"),   // 网络状态
                .linkedFramework("Security"),              // 加密
                // JPush 必需的系统库
                .linkedLibrary("z"),                       // 压缩
                .linkedLibrary("resolv"),                  // DNS解析
                // 以下是可选的，可根据需要删除：
                // .linkedFramework("CoreGraphics"),       // 图片处理（可选）
                // .linkedFramework("AdSupport"),          // 广告标识符（可选，用于统计）
            ]
        ),
        .testTarget(
            name: "JPushPluginTests",
            dependencies: ["JPushPlugin"],
            path: "ios/Tests/JPushPluginTests"
        ),
    ]
)
