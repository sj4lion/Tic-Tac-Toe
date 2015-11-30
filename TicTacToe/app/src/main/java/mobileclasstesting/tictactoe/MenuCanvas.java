package mobileclasstesting.tictactoe;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class MenuCanvas extends View {

    final int SPEED = 10;

    private Handler handler;

    private Bitmap Ximage;
    private Bitmap Oimage;

    private int TOTAL_HEIGHT;
    private int TOTAL_WIDTH;

    private flyingObject flyingObjects[];
    private int createCounter;


    private Runnable animationThread = new Runnable() {
        @Override
        public void run() {
            // redraw
            invalidate();
        }
    };

    public MenuCanvas(Context context) {
        super(context);

        flyingObjects = new flyingObject[0];

        createCounter = 40;

        initializeXandO();
    }

    public MenuCanvas(Context context, AttributeSet attribs) {
        super(context, attribs);

        flyingObjects = new flyingObject[0];

        createCounter = 40;

        initializeXandO();
    }





    private void initializeXandO() {

        handler = new Handler();

        // create bitmap of X and O
        Ximage = BitmapFactory.decodeResource(getResources(), R.drawable.x);
        Oimage = BitmapFactory.decodeResource(getResources(), R.drawable.o);


        // scale bitmap of X and O
        Ximage = Bitmap.createScaledBitmap(Ximage, 100, 100, false);
        Oimage = Bitmap.createScaledBitmap(Oimage, 100, 100, false);

    }



    private void nextFrame() {

        for(int index = 0; index < flyingObjects.length; index++){

            flyingObjects[index].move();

            //detect if object moved out of bounds ... remove it if so
            if(flyingObjects[index].x < -250 || flyingObjects[index].x > TOTAL_WIDTH + 250
                    || flyingObjects[index].y < -250 || flyingObjects[index].y > TOTAL_HEIGHT + 250){

                flyingObjects = removeObject(flyingObjects, index);

            }

        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        TOTAL_HEIGHT = this.getHeight();
        TOTAL_WIDTH = this.getWidth();


        // fill background white
        canvas.drawRGB(255, 255, 255);

        createCounter++;

        //randomly create a new flying object
        if(createCounter == 50){

            int direction = (int)(Math.random()*3.99);
            if(direction == 0){ // moving right, randomY but set X

                int X = -200;
                int randY = (int)(Math.random()*(TOTAL_HEIGHT-100));

                int typeRandom = (int)(Math.random()*1.99);
                if(typeRandom == 0){

                    flyingObjects = addObject(flyingObjects, new flyingObject(X, randY, true, flyingObject.DIRECTION.RIGHT));

                }
                else{

                    flyingObjects = addObject(flyingObjects, new flyingObject(X, randY, false, flyingObject.DIRECTION.RIGHT));

                }

            }
            else if(direction == 1){ // moving left, randomY but set X

                int X = TOTAL_WIDTH + 200;
                int randY = (int)(Math.random()*(TOTAL_HEIGHT-100));

                int typeRandom = (int)(Math.random()*1.99);
                if(typeRandom == 0){

                    flyingObjects = addObject(flyingObjects, new flyingObject(X, randY, true, flyingObject.DIRECTION.LEFT));

                }
                else{

                    flyingObjects = addObject(flyingObjects, new flyingObject(X, randY, false, flyingObject.DIRECTION.LEFT));

                }

            }
            else if(direction == 2){ // moving up, randomx but set y

                int randX = (int)(Math.random()*(TOTAL_WIDTH-100));
                int Y = TOTAL_HEIGHT + 200;

                int typeRandom = (int)(Math.random()*1.99);
                if(typeRandom == 0){

                    flyingObjects = addObject(flyingObjects, new flyingObject(randX, Y, true, flyingObject.DIRECTION.UP));

                }
                else{

                    flyingObjects = addObject(flyingObjects, new flyingObject(randX, Y, false, flyingObject.DIRECTION.UP));

                }

            }
            else if(direction == 3){ // moving up, randomx but set y

                int randX = (int)(Math.random()*(TOTAL_WIDTH-100));
                int Y = -200;

                int typeRandom = (int)(Math.random()*1.99);
                if(typeRandom == 0){

                    flyingObjects = addObject(flyingObjects, new flyingObject(randX, Y, true, flyingObject.DIRECTION.DOWN));

                }
                else{

                    flyingObjects = addObject(flyingObjects, new flyingObject(randX, Y, false, flyingObject.DIRECTION.DOWN));

                }

            }


            createCounter = 0;

        }

        //render all flying objects
        for(int index = 0; index < flyingObjects.length; index++){

            if(flyingObjects[index].isX){

                canvas.drawBitmap(Ximage, flyingObjects[index].x, flyingObjects[index].y, null);

            }
            else{

                canvas.drawBitmap(Oimage, flyingObjects[index].x, flyingObjects[index].y, null);

            }

        }

        // delay before next frame
        handler.postDelayed(animationThread, SPEED);
        nextFrame();

    }




    // object to handle values for flying objects
    private static class flyingObject{

        public enum DIRECTION{

            RIGHT,
            LEFT,
            UP,
            DOWN

        }

        public int x;
        public int y;

        public boolean isX;

        public DIRECTION direction;

        public flyingObject(int X, int Y, boolean ISX, DIRECTION moveDirection){

            x = X;
            y = Y;

            isX = ISX;

            direction = moveDirection;

        }

        public void move(){

            if(direction == DIRECTION.RIGHT)
                x += 5;
            else if(direction == DIRECTION.LEFT)
                x -= 5;
            else if(direction == DIRECTION.UP)
                y -= 5;
            if(direction == DIRECTION.DOWN)
                y += 5;

        }


    }




    private flyingObject[] addObject(flyingObject[] old, flyingObject newObject){

        flyingObject result[] = new flyingObject[old.length+1];

        for(int index = 0; index < old.length; index++){

            result[index] = old[index];

        }

        result[old.length] = newObject;

        return result;

    }


    private flyingObject[] removeObject(flyingObject[] old, int location){

        flyingObject result[] = new flyingObject[old.length-1];

        for(int index = 0; index < result.length; index++){

            if(index < location)
                result[index] = old[index];
            else
                result[index] = old[index+1];

        }

        return result;

    }

}
