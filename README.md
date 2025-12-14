# BCarControl

![Android CI](https://github.com/futesat/bcarcontrol/actions/workflows/android_build.yml/badge.svg)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**BCarControl** is an Android application designed to remotely control a robotic car. It features a virtual joystick interface for directional control and provides additional toggles for vehicle systems like lights and fans.

This application serves as the client for the **BCarControl Server**. It is designed to work in tandem with the server implementation available at [https://github.com/your-username/bcarcontrol](https://github.com/your-username/bcarcontrol).

## Features

- **Virtual Joystick**: Intuitive touch interface for steering and speed control (Forward, Backward, Left, Right).
- **System Controls**:
  - **Lights**: Toggle car headlights.
  - **Fans**: Control motor cooling fans.
  - **Mobile Control**: Enable/Disable remote mobile control.
- **Status Monitoring**: Real-time connection status and feedback.
- **Power Management**: Options to reboot or shutdown the vehicle remotely.

## Tech Stack

- **Language**: Java
- **UI**: Android XML Layouts, Virtual Joystick Library
- **Architecture**: MVC
- **Networking**: Retrofit, OkHttp
- **Reactive Programming**: RxJava 2, RxAndroid
- **Build System**: Gradle 8.5
- **CI/CD**: GitHub Actions

## Prerequisites

- **JDK 21**: Ensure Java 21 is installed.
- **Android SDK**: API Level 34 (Android 14) is targeted.

## Installation

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/futesat/bcarcontrol.git
    cd bcarcontrol
    ```

2.  **Open in Android Studio**:
    - Select "Open an existing Android Studio project" and navigate to the cloned directory.

3.  **Sync Gradle**:
    - Allow Android Studio to download dependencies and sync the project.

## Building the App

To build the application from the command line:

```bash
# Build Debug APK
./gradlew assembleDebug
```

The output APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

## Running Tests

To run the unit tests:

```bash
./gradlew testDebugUnitTest
```

## Continuous Integration

This project uses **GitHub Actions** for CI. Every push and pull request to the `main` branch triggers a workflow that:
1.  Sets up the build environment (JDK 21).
2.  Runs all unit tests.
3.  Assembles the Debug APK.
4.  Uploads the APK as a build artifact.

Check the [Actions tab](https://github.com/futesat/bcarcontrol/actions) for build history.

## Project Structure

- `app/`: Main application module.
- `virtualjoystick/`: Local module for the joystick UI component.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
