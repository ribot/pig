# Pig (in the middle)
**Pig is currently under heavy development and should not be used by anyone yet!**

Developing for multiple mobile platforms is both time-consuming and expensive. Writing completely separate versions in Java and Objective-C in parallel duplicates logic and wastes effort, especially when requirements change. Writing an application using PhoneGap generally leads to the UI to feeling non-standard and ultimately to a sub-par user experience.

Pig aims to solves these problems by moving the business logic into a shared Javascript codebase. On each platform (currently Android and iOS), the user interface is implemented in native code, leading the best possible user experience, while data manipulation and other logic is handled by the shared Javascript.

## What is Pig?
Your Pig enabled application will be split into at least two different parts:

- Javascript logic, which is shared between all platforms
- At least one native (Java, Objective-C) project which implements the user interface

Pig sits in the middle of these two pieces of your application ([hence the name](http://en.wikipedia.org/wiki/Keep_Away)) and facilitates the exchange of data between them.

![Pig Architecture](graphics/architecture.png)

Pig has two different models for passing data between native code and Javascript: *Request/response* and an *event bus*.

### Request/Response
Request and response is used when your native code wants to send some information to the Javascript logic and expects a response, possibly including some data it can display. For example, consider an application with a login form containing an email text field, password text field and a submit button. These elements would be implemented in native code, to give a truly native user experience.

When the submit button is pressed the native code could make a *request* through Pig, passing the email and password data, to a *handler* in Javascript. This Javascript would then make a HTTP request to the API and return the *response* to the callback that the native code passed in when it made the *request*. It could also include some data, such as the newly logged in user profile.

It is the responsibility of the project using Pig to define what the handlers for your project should look like, and ensure each platforms native code is using these handlers correctly. Think of this as a similar exercise to documenting a REST API. Include enough detail to you know what data should be passed in, what you expect to be returned and what errors might encounter.

### Event Bus
The event bus is used to broadcast small pieces of data to all parts of you application. It is possible for either your native or Javascript code to emit events and these events can be listened for on either side as well. With each event you can also send a piece of JSON data.

Following on from the login example above, the Javascript may wish to emit a `user-logged-in` event when a login request is successful. We could also send the new user's profile information as data with the event. This event would give other parts of the application a chance to learn about the change in state, without having to be called directly. For example, when a login is successful a piece of Javascript might want to run to associated some user data the application has with the newly logged in user. At the same time a piece of user interface code might want to listen for the `user-logged-in` event and change to display some user data.

## Using Pig
_Pig is still at a very early stage of development. We are currently experimenting with the best way of structuring a Pig project. This section describes how we use Pig at [ribot](http://ribot.co.uk), but it might not be the best method. Submit a pull request if you have any suggestions._

### Project structure
Each Pig application is made up of at least two parts: the Javascript logic and at least one platform specific user interface. We keep all these parts in the same source control repository. The reason for this is that it when you update the Javascript to native interface you are also going to  update the platform specific parts to match. This is much easier to keep in sync if all parts live in the same repository.

Each part of the application lives in its own folder in the project root (`android`, `ios` and `javascript` are our convention). The structure of each of these parts are described in detail below.

### Javascript
The Javascript is the core of your Pig application and all of the other parts depend on it. At [ribot](http://ribot.co.uk) we are using [browserify](http://browserify.org) to bundle our Javascript code and run it inside the hidden web views on each platform. We have found this to be the perfect tool for the job, allowing us to use the super simple CommonJS module definitions we know from Node.js, which also benefitting from being able to access npm modules!

Inside the Javascript folder we have a standard `package.json` file which, among other things, defines our dependency on the Javascript portion of the Pig library.

```json
{
  "name": "pig-app-js",
  "version": "0.0.1",
  "main": "app.js",
  "dependencies": {
    "pig": "~0.2.0",
  }
}
```

As the JSON file above states, our main file for the Javascript part is called `app.js`. The contents of it are something like this:

```javascript
var Pig = require( 'Pig' );

// Setup our pig instance
var pig = window.pig = new Pig();

// Define a handler
pig.register( 'user/get', function getUser( data, response ) {
	// Get the user
	// ...
	
	response.success( user );} );
```

**A very important part here is that you must assign your Pig instance to `window.pig`. This allows the native part of the Pig library to call your Pig instance's methods to pass requests to it.**

Because we are going to use browserify you are free to use any patterns you would normally use in a Node.js application here. For example we often have a module for each _group_ of handlers which greatly increases the readability of the code.

To turn our application into a bundle that the hidden web views of our native applications can run, we need to call browserify:

```
browserify app.'s -o bundle.js
```

In reality we never call this directly, but instead add this as a build step for each of our platforms. The process for this is described in the sections below.

### Android
The `Android` folder contains a normal gradle based project, which could easily be generated with Android Studio.

To get the Android part of the Pig library we need to add this to our `build.gradle` file:

```groovy
dependencies {
    compile 'uk.co.ribot:pig:0.2.0'
}
```

This will include the Pig jar in your project. If you are not using Gradle you can instead [download the latest Jar from Maven Central](http://search.maven.org/#browse%7C475045034) and put that in your `libs` folder along with the latest [Gson](https://code.google.com/p/google-gson/) Jar.

There must be a file called `index.html` included in a folder called `bridge` inside your `assets` folder. This is the file which is loaded into the hidden web view automatically by the Pig library. The contents of that file should be:

```html
<script src="bundle.js"></script>
```

In this case we are expecting the Browserify bundled Javascript to be in the same `bridge` folder and called `bundle.js`. Because the `bundle.js` file is generated by the browserify command we recommend you add it to your `.gitignore`. Obviously you are free to not use Browserify and instead use something like Require.js. In this case your structure may well be different.

Assuming you are using Browserify you will likely want to bundle you Javascript code automatically when you Android application builds, to avoid having to remember to first run the `browserify` command above. Add the following to you `build.gradle`:

```groovy
task browserifyPigJs(type: Exec) {
    def pigJsFile = "../../javascript/app.js"
    def browserifyPath = "/usr/local/bin/"
    commandLine "${browserifyPath}browserify", pigJsFile, '-o', 'src/main/assets/bridge/bundle.js'
}

// Add the browserifyPigJs tasks to the Android build lifecycle before merging assets
tasks.whenTaskAdded { task ->
    //Matches all the mergeAssets tasks, i.e. mergeDebugAssets or mergeReleaseAssets
    if (task.name.matches("merge([A-Za-z]+)Assets")) {
        task.dependsOn browserifyPigJs
    }
}
```

This will automatically bundle the Javascript every time you compile your Android app into the correct folder.

_TODO: Talk about using Pig from Android_

### iOS
_TODO: Talk about setting up Pig on iOS_
_TODO: Talk about using Pig from iOS_

## API Reference

## Licence
```
Copyright 2014 ribot (http://ribot.co.uk)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Contributing
