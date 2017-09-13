
# Conference - Android

## Introduction

Conference Application is a native mobile application designed and developed to provide Systers a method of collaborating, networking, and attending sessions within any big event (such as conferences, summits, codeathons or hackathons). Systers can register, login, and start planning weeks before the large event takes place.

The app supports devices running Android 4.0.3 (API level 15)+, and is optimized for phones of all shapes and sizes.

## Getting Started

### Prerequisites

This is a Gradle-based project that works best with
[Android Studio](http://developer.android.com/sdk/installing/studio.html).

To make sure you have necessary tools to run this application, follow these steps:

1.  Install the following software: <br>
          - [Android SDK](http://developer.android.com/sdk/index.html) <br>
          - [Gradle](http://www.gradle.org/downloads) <br>
          - [Android Studio](http://developer.android.com/sdk/installing/studio.html) <br>

2. Run the Android SDK Manager by pressing the SDK Manager toolbar button in Android Studio or by running the 'android' command in a terminal window.

3. In the Android SDK Manager, ensure that the following are installed, and are updated to the latest available version: <br>
         - Tools > Android SDK Platform-tools <br>
         - Tools > Android SDK Tools <br>
         - Tools > Android SDK Build-tools <br>
         - Android 7.1.1 > SDK Platform (API level 25) <br>
         - Extras > Android Support Repository <br>
         - Extras > Android Support Library <br>
         - Extras > Google Play services <br>
         - Extras > Google Repository <br>
         - Extras > Constraint Layout <br>

### Installing/Launching the application

1. Clone the repository or download the source code by following the instructions [here](https://help.github.com/articles/cloning-a-repository/).
2. Add firebase to your project by following the instructions [here](docs/Firebase-Setup.md).
3. All dependencies are defined in ```app/build.gradle```. Import the project in Android Studio and follow these [steps](https://developer.android.com/training/basics/firstapp/running-app.html) or use Gradle in command line:
   
   ```
   ./gradlew assembleDebug
   ```
   
   The result apk file will be placed in ```app/build/outputs/apk/```.


## Contributing

We welcome and encourage all pull requests. It usually will take 24-48 hours to respond to any issue or request. Here are some basic rules to follow to ensure timely addition of your request:

1. Match coding style (braces, spacing, etc.) This is best achieved using CMD+Option+L (Reformat code) on Mac and Ctrl+Alt+L for Windows with Android Studio defaults.
2. Also, please remove redundant imports before committing and pushing your code.
3. If its a feature, bugfix, or anything please only change code to what you specify.
4. Please keep PR titles easy to read and descriptive of changes, this will make them easier to merge.
5. Pull requests must be made against `develop` branch. Any other branch (unless specified by the maintainers) will get rejected.
6. Check for existing [issue](https://github.com/systers/conference-android/issues) first, before filing an issue.
7. Please tag a maintainer or contributor for code review.

### Known issues/features that require contribution:

1. Writing Local and Instrumented Unit Tests for the application. You can gain insight about how testing is done in Android by looking [here](https://developer.android.com/studio/test/index.html). 
   Also you can view [this](https://github.com/systers/conference-android/pull/58) PR to see how to write tests for a given scenario.

2. Also, to resolve known issues you can look at current [issues](https://github.com/systers/conference-android/issues) list.


## Getting Help

If you are facing any issues related to setting up the project, please make sure you first research and try to resolve the issue by your own.
If you cannot resolve the issue then you can reach out on the `#conference-app` channel on the [Systers Open Source Slack](http://systers.io/slack-systers-opensource/).

If you have suggestion regarding new features or optimization of any existing features please start the discussion on the slack channel instead of creating an issue.
**Create Issues only to report bugs**.

## Contributors

- [Aagam Shah](https://github.com/abs51295) (Slack: @abs51295)

## Maintainers 

- [Chhavi P. Gupta](https://github.com/chhavip) (Slack: @chhavip)
- [Riddhi Kakadia](https://github.com/riddhik84) (Slack: @riddhik84)


If you have any other questions or queries, that you think is not addressed here, you can contact 
[gsoc-admins@anitaborg.org](mailto:gsoc-admings@anitaborg.org) or [gsoc-mentors@anitaborg.org](mailto:gsoc-mentors@anitaborg.org)