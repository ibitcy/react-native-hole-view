# Example app for new architecture

NOTE: install node modules using NodeJS v20.19.4 or later

## Getting Started

### Prerequisites

1. Install dependencies:
   ```sh
   npm install
   # or
   yarn install
   ```

2. For iOS, install CocoaPods dependencies:
   ```sh
   cd ios
   bundle install  # First time only
   bundle exec pod install
   cd ..
   ```

### Running the App

#### Android

1. Start Metro bundler (optional - it will start automatically when running the app):
   ```sh
   npm start
   # or
   yarn start
   ```

2. Run the app:
   ```sh
   npm run android
   # or
   yarn android
   ```

   Or open `android/` in Android Studio and run from there.

#### iOS

1. Start Metro bundler (optional - it will start automatically when building from Xcode):
   ```sh
   npm start
   # or
   yarn start
   ```

2. Run the app:
   ```sh
   npm run ios
   # or
   yarn ios
   ```

   Or open `ios/exampleNewArch.xcworkspace` in Xcode and run from there (⌘R).

**Note:** When running from Xcode, Metro bundler will start automatically before the app launches.

