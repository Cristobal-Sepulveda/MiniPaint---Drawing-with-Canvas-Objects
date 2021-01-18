First COMMIT: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>





In this exercise you are going to create a MiniPaint project.

Create a new Kotlin project called MiniPaint that uses the Empty Activity template.
Open the app/res/values/colors.xml file and add the following two colors. One for the background, and one for drawing.

//
<color name="colorBackground">#FFFF5500</color>
<color name="colorPaint">#FFFFEB3B</color>
//

Open styles.xml In the parent of the given AppTheme style, replace DarkActionBar with NoActionBar. This removes the action bar,
so that you can draw fullscreen.

// <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">






Second COMMIT: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>






Next, you going to create a custom view, MyCanvasView for drawing. In the app.java.com.example.android.minipaint package create a new Kotlin file called MyCanvasView. Make the canvas view class, extend the view class, and pass in the context and go with the suggested imports.

//
import android.content.Context
import android.view.View

class MyCanvasView(context: Context) : View(context) {
}
//

7.	Exercise: Set MyCanvasView as the Content View

	To display what you will draw in MyCanvasView,you have to set it as the ContentView of the MainActivity.

	Open strings.xml and define a string to use for the views contents description.
<string name="canvasContentDescription">Mini Paint is a simple line drawing app.
   Drag your fingers to draw. Rotate the phone to clear.</string>


	Next, open MainActivity.kt. In onCreate delete setContentView. Next, create an instance of MyCanvasView.

val myCanvasView = MyCanvasView(this)


	Below that, request the fullscreen for the layout of MyCanvasView.
Do this by setting the SYSTEM_UI_FLAG_FULLSCREEN flag on MyCanvasView.
In this way, the view completely fills the screen.
myCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN

	Next, add a content description.
myCanvasView.contentDescription = getString(R.string.canvasContentDescription)

Finally, below that, set the ContentView to MyCanvasView.
setContentView(myCanvasView)

	You're going to need to know the size of the view for drawing, but you can't get the size of the view in this onCreate method because the size has not been determined at this point.

	Now, it's time for you to run your app.
When you run your app, you will see a completely widescreen
because Canvas has no size and you have not drawn anything yet.

8.	Exercise: Override onSizeChanged()

	The onSizeChanged method is caused by the Android system whenever a view changes size. Because the view starts out with no size, the view's onSizeChanged method is also called after the activity first creates an inflates the view. This onSizeChanged method is therefore the ideal place to create and set up the Views Canvas.

	In MyCanvasView at the class level, defined member variables for a Canvas and a Bitmap. Column extraCanvas and extra Bitmap.

private lateinit var extraCanvas: Canvas
private lateinit var extraBitmap: Bitmap

 	These are your Bitmap and Canvas for caching what has been drawn before.

	Define a class level variable, background color for the background color of the Canvas and initialize it to the color background you defined earlier.

private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

	In MyCanvasView, override the onSizeChanged method. This callback method is called by the Android system with the changed screen dimensions. That is with a new width and height to change to and the old width and height to change from. 	

override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
   super.onSizeChanged(width, height, oldWidth, oldHeight)
}

	Inside onSizeChanged, create an instance of Bitmap with a new width and height, which are the screen size and assign it to extra Bitmap. The third argument is the Bitmap color configuration. ARGB_8888 stores each color in four bytes and is recommended.

extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)


 	Below, create a Canvas instance from extra Bitmap and assign it to extra Canvas. Specify the background color in which to fill extra Canvas.

extraCanvas = Canvas(extraBitmap)
extraCanvas.drawColor(backgroundColor)




	Looking at onSizeChanged, a new Bitmap and Canvas are created. every time the function executes. You do need a new Bitmap because the size has changed. However, this is a memory leak leaving the old Bitmaps around. To fix this, recycle extra Bitmap before creating the new one.

if (::extraBitmap.isInitialized) extraBitmap.recycle()








THIRD COMMIT: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>







9.	Exercise: Override onDraw()

	Our drawing work from MyCanvasView happens in onDraw. To start, display the canvas, fill in the screen with a background color that you set in onSizeChanged.

	In MyCanvasView override onDraw and draw the contents of the cached extraBitmap on the canvas associated with the view. That drawBitmap canvas method comes in several versions. In this code you provide the bitmap, the x and y coordinates and pixels for the top left corner, and null for to paint, so you can set that later.
override fun onDraw(canvas: Canvas) {
   super.onDraw(canvas)
canvas.drawBitmap(extraBitmap, 0f, 0f, null)
}

	Notice that the canvas that is passed to onDraw and used by the system to display the bitmap is different than the one you created in the onSizeChanged method and used to draw on the bitmap. Also note the 2D coordinate system used for drawing on a canvas is in pixels and the origin, 00, is at the top left corner of the canvas.

 Run your app and you should see the whole screen filled with a specified background color.







FOURTH COMMIT: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>







Exercise
In this exercise you are going to set up the Paint for drawing.

1. In MyCanvasView.kt, at the top file level, define a constant for the stroke
	 width.

private const val STROKE_WIDTH = 12f // has to be float

2. At the class level of MyCanvasView, define a variable drawColor for holding the
	 color to draw with and initialize it with the colorPaint resource you defined
	 earlier.

private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

3. At the class level, below, add a variable paint for a Paint object and initialize it as follows.

// Set up the paint with which to draw.
private val paint = Paint().apply {
   color = drawColor
   // Smooths out edges of what is drawn without affecting shape.
   isAntiAlias = true
   // Dithering affects how colors with higher-precision than the device are down-sampled.
   isDither = true
   style = Paint.Style.STROKE // default: FILL
   strokeJoin = Paint.Join.ROUND // default: MITER
   strokeCap = Paint.Cap.ROUND // default: BUTT
   strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
}




Exercise
In this exercise you are going to initialize a Path object.

1. In MyCanvasView, add a variable path and initialize it with a Path object to
	 store the path that is being drawn when following the user's touch on the
	 screen. Import android.graphics.Path for the Path.

private var path = Path()




Exercise
In this exercise you are going to use the onTouchEvent() method to respond to
motion on the display.

1. In MyCanvasView, override the onTouchEvent() method to cache the x and y coordinates
	 of the passed in event. Then use a when expression to handle motion events for
	 touching down on the screen, moving on the screen, and releasing touch on the
	 screen. These are the events of interest for drawing a line on the screen. For each
	 event type, call a utility method, as shown in the code below. See the
	 MotionEvent class documentation for a full list of touch events.

override fun onTouchEvent(event: MotionEvent): Boolean {
   motionTouchEventX = event.x
   motionTouchEventY = event.y

   when (event.action) {
       MotionEvent.ACTION_DOWN -> touchStart()
       MotionEvent.ACTION_MOVE -> touchMove()
       MotionEvent.ACTION_UP -> touchUp()
   }
   return true
}

2. At the class level, add the missing motionTouchEventX and motionTouchEventY
	 variables for caching the x and y coordinates of the current touch event
	 (the MotionEvent coordinates). Initialize them to 0f.

private var motionTouchEventX = 0f
private var motionTouchEventY = 0f

3. Create stubs for the three functions touchStart(), touchMove(), and touchUp().

private fun touchStart() {}

private fun touchMove() {}

private fun touchUp() {}

4. Your code should build and run, but you won't see any different from the colored background yet.




Exercise
In this exercise you are going to implement touchStart().

1. At the class level, add variables to cache the latest x and y values. After the
	 user stops moving and lifts their touch, these are the starting point for the
	 next path (the next segment of the line to draw).

private var currentX = 0f
private var currentY = 0f

2. Implement the touchStart() method as follows. Reset the path, move to the x-y
 	 coordinates of the touch event (motionTouchEventX and motionTouchEventY) and assign
 	 currentX and currentY to that value.

private fun touchStart() {
   path.reset()
   path.moveTo(motionTouchEventX, motionTouchEventY)
   currentX = motionTouchEventX
   currentY = motionTouchEventY
}




Exercise
In this exercise you are going to implement touchMove().

1) At the class level, add a touchTolerance variable and set it to
	 ViewConfiguration.get(context).scaledTouchSlop.

	 private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

2) Define the touchMove() method. Calculate the traveled distance (dx, dy), create
	 a curve between the two points and store it in path, update the running currentX
	 and currentY tally, and draw the path. Then call invalidate() to force redrawing
	 of the screen with the updated path.

private fun touchMove() {
   val dx = Math.abs(motionTouchEventX - currentX)
   val dy = Math.abs(motionTouchEventY - currentY)
   if (dx >= touchTolerance || dy >= touchTolerance) {
       // QuadTo() adds a quadratic bezier from the last point,
       // approaching control point (x1,y1), and ending at (x2,y2).
       path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2,
			  															 (motionTouchEventY + currentY) / 2)
       currentX = motionTouchEventX
       currentY = motionTouchEventY
       // Draw the path in the extra bitmap to cache it.
       extraCanvas.drawPath(path, paint)
   }
   invalidate()
}

In more detail, this is what you will be doing in code:

1) Calculate the distance that has been moved (dx, dy).
2) If the movement was further than the touch tolerance, add a segment to the path.
3) Set the starting point for the next segment to the endpoint of this segment.
4) Using quadTo() instead of lineTo() create a smoothly drawn line without corners.
	 See Bezier Curves.
5) Call invalidate() to (eventually call onDraw() and) redraw the view.




Exercise
In this exercise you are going to implement touchUp().

1. Implement the touchUp() method.

private fun touchUp() {
   // Reset the path so it doesn't get drawn again.
   path.reset()
}

2. Run your code and use your finger to draw on the screen. Notice that if you
 	 rotate the device, the screen is cleared, because the drawing state is not
   saved. For this sample app, this is by design, to give the user a simple way
	 to clear the screen.
