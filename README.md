![](https://raw.githubusercontent.com/Kaopiz/KProgressHUD/master/demo/screenshots/screencast.gif)

<!---
[![](https://raw.githubusercontent.com/Kaopiz/KProgressHUD/master/demo/screenshoots/thumb01.png)](https://github.com/Kaopiz/KProgressHUD/blob/master/demo/screenshoots/image01.png?raw=true)
[![](https://raw.githubusercontent.com/Kaopiz/KProgressHUD/master/demo/screenshoots/thumb02.png)](https://github.com/Kaopiz/KProgressHUD/blob/master/demo/screenshoots/image02.png?raw=true)
[![](https://raw.githubusercontent.com/Kaopiz/KProgressHUD/master/demo/screenshoots/thumb03.png)](https://github.com/Kaopiz/KProgressHUD/blob/master/demo/screenshoots/image03.png?raw=true)
[![](https://raw.githubusercontent.com/Kaopiz/KProgressHUD/master/demo/screenshoots/thumb04.png)](https://github.com/Kaopiz/KProgressHUD/blob/master/demo/screenshoots/image04.png?raw=true)
[![](https://raw.githubusercontent.com/Kaopiz/KProgressHUD/master/demo/screenshoots/thumb05.png)](https://github.com/Kaopiz/KProgressHUD/blob/master/demo/screenshoots/image05.png?raw=true)
[![](https://raw.githubusercontent.com/Kaopiz/KProgressHUD/master/demo/screenshoots/thumb06.png)](https://github.com/Kaopiz/KProgressHUD/blob/master/demo/screenshoots/image06.png?raw=true)
[![](https://raw.githubusercontent.com/Kaopiz/KProgressHUD/master/demo/screenshoots/thumb07.png)](https://github.com/Kaopiz/KProgressHUD/blob/master/demo/screenshoots/image07.png?raw=true)
[![](https://raw.githubusercontent.com/Kaopiz/KProgressHUD/master/demo/screenshoots/thumb08.png)](https://github.com/Kaopiz/KProgressHUD/blob/master/demo/screenshoots/image08.png?raw=true)
-->

## Compatibility

Android 2.3 and later

## Adding KProgressHUD to your project

### Gradle
Include this in your root `build.gradle`

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Include this in your app `build.gradle`

```
dependencies {
    // Other dependencies
    implementation 'com.github.ntduc-let:KProgressHUD:1.0.0'
}
```

### Source code
If you want more control over the implementation, download and import the `kprogresshud` folder as a module to your project and modify according to your need.

## Usage

The usage of KProgressHUD is pretty straight forward. 
- Create the HUD, customize its style and show on the UI thread. 
- Fire a background worker to handle long-running tasks. 
- When done, call `dismiss()` to close (or if you use a determinate style, the HUD will automatically dismiss when progress reaches its max).

Indeterminate HUD
```java
KProgressHUD.create(MainActivity.this)
	.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
	.setLabel("Please wait")
	.setDetailsLabel("Downloading data")
	.setCancellable(true)
	.setAnimationSpeed(2)
	.setDimAmount(0.5f)
	.show();
```

Determinate HUD
```java
KProgressHUD hud = KProgressHUD.create(MainActivity.this)
					.setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
					.setLabel("Please wait")
					.setMaxProgress(100)
					.show();
hud.setProgress(90);
```

You can also create a custom view to be displayed.
```java
ImageView imageView = new ImageView(this);
imageView.setBackgroundResource(R.drawable.spin_animation);
AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
drawable.start();
KProgressHUD.create(MainActivity.this)
   .setCustomView(imageView)
   .setLabel("This is a custom view")
   .show();
```
Optionally, the custom view can implement `Determinate` or `Indeterminate` interface, which make the HUD treats this view like the default determinate or indeterminate one.

See [**Javadocs**](http://kaopiz.github.io/KProgressHUD/) or [**sample**](https://github.com/Kaopiz/KProgressHUD/tree/master/demo/src/main) for more information.
