# Troubleshooting

**Q:** When I tried to build the library, why did I get the following error? "SDK location not
found. Define location with an ANDROID_SDK_ROOT environment variable or by setting up the sdk.dir
path in your project's local properties file... "

**A:** This error occurs when the project does not know the location of your local Android SDK. It
should be located at `/Users/[YOUR_USER_NAME]/Library/Android/sdk`, which is where Android Studio
recommends you put it during initial setup/installation.

Opening this project in Android Studio will automatically create and configure a `local.properties`
file for you. If you want to do that yourself, create a file called `local.properties` in the root
directory of this project. Paste in the following line, replacing [YOUR_USER_NAME] with, you guessed
it, the username you're using on your local machine:

```
sdk.dir=/Users/[YOUR_USER_NAME]/Library/Android/sdk
```

> **Note**: The `local.properties` file should not be committed to version control, as the path will
> be different for anyone else working on the project.


**Q:** When I tried to run the project in Xcode, why did I get the following error? "Framework not
found shared_umbrella".

**A:** You probably opened the `.xcodeproj` file in Xcode instead of the `.xcworkspace`. Close out
the `.xcodeproj` and open the `.xcworkspace` and run again.

To learn more about Cocoapods and how to use them, check
out [their official guide](https://guides.cocoapods.org/using/index.html).

**Q:** The Xcode project won't compile. On the `import shared` line in Swift, I'm getting a
compilation error "no
such module: 'shared'".

**A:** Try closing Xcode and deleting the `Pods/` folder located in the root directory of the iOS
project. Then run the command `pod install` in that same iOS root directory (which
is `/KaMPKit/ios/` to be specific). This command will generate a new `Pods` folder. Reopen
the `.xcworkspace` file and try to build again.

> Note: We're still not quite sure as to the cause of this error. Possible factors include differing
> versions of Cocoapods or Xcode.

**Q:** My iOS framework binary size is bigger after adding Kotlin/Native code.

**A:** First confirm the actual impact on file size after uploading to the app store. Your library
framework can contain bitcode and debug symbols which won't make it to the user's device. There is
still a binary size overhead from adding Kotlin Native to a project, but there are things you can do
to reduce it. Primarily, be conscious of what is being exposed from the shared code. If unnecessary
code is public, it will add to the size of the generated ObjC headers. Ensure things in commonMain
are marked as private or internal unless they need to be exposed to iOS. Using
Kotlin's [explicit api mode](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)
can help enforce this.

## More to Come!

[Let us know](../CONTACT_US.md) what issues you run into.
