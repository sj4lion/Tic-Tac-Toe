package mobileclasstesting.tictactoe;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GameBoardCanvas extends View {

    final int SPEED = 10;

    private Handler handler;

    private Bitmap Ximage;
    private Bitmap Oimage;

    private int TOTAL_HEIGHT;
    private int TOTAL_WIDTH;

    private boolean scaled;

    private String BOARD; //9 characters that represent the state of the board



    private Runnable animationThread = new Runnable() {
        @Override
        public void run() {
            // redraw
            invalidate();
        }
    };

    public GameBoardCanvas(Context context) {
        super(context);

        BOARD = "EEEEEEEEE"; //board is totally empty to start

        scaled = false;

        handler = new Handler();
        initializeXandO();

    }

    public GameBoardCanvas(Context context, AttributeSet attribs) {
        super(context, attribs);

        BOARD = "EEEEEEEEE"; //board is totally empty to start

        scaled = false;

        handler = new Handler();
        initializeXandO();

    }

    private void initializeXandO() {

        handler = new Handler();

        // create bitmap of X and O
        Ximage = BitmapFactory.decodeResource(getResources(), R.drawable.x);
        Oimage = BitmapFactory.decodeResource(getResources(), R.drawable.o);

    }


    public void setGameBoard(String newBoard){

        BOARD = newBoard;


    }


    private void nextFrame() {



    }

    @Override
    protected void onDraw(Canvas canvas) {

        TOTAL_HEIGHT = this.getHeight();
        TOTAL_WIDTH = this.getWidth();

        // have to scale X and O images in here since width and height of canvas are needed
        if(!scaled) {

            Ximage = Bitmap.createScaledBitmap(Ximage, (int) (TOTAL_WIDTH / 3.50), (int) (TOTAL_HEIGHT / 3.50), false);
            Oimage = Bitmap.createScaledBitmap(Oimage, (int) (TOTAL_WIDTH / 3.50), (int) (TOTAL_HEIGHT / 3.50), false);

            scaled = true; //makes sure it only scaled once for performance

        }


        // fill background white
        canvas.drawRGB(255, 255, 255);

        Paint boardColour = new Paint();
        boardColour.setColor(Color.rgb(0, 0, 0));

        //draw boards
        canvas.drawRect((float) TOTAL_WIDTH / 3, 0.0f, (float) (TOTAL_WIDTH / 3) + 25, (float) TOTAL_HEIGHT, boardColour);
        canvas.drawRect((float) (2*TOTAL_WIDTH) / 3, 0.0f, (float) (2*TOTAL_WIDTH / 3) + 25, (float) TOTAL_HEIGHT, boardColour);

        canvas.drawRect(0.0f, (float) TOTAL_HEIGHT / 3, TOTAL_WIDTH, (float) (TOTAL_HEIGHT / 3)+25, boardColour);
        canvas.drawRect(0.0f, (float) (2*TOTAL_HEIGHT / 3), TOTAL_WIDTH, (float) (2*TOTAL_HEIGHT / 3)+25, boardColour);

        //draws Xs and Os (or blank spaces)
        for(int index = 0; index < 9; index++){

            if(BOARD.charAt(index) == 'X'){

                //formulas for location on 3x3 board
                int picX = (index%3)*(TOTAL_WIDTH/3) +25; //offset 25 for width of board bars
                int picY = (index/3)*(TOTAL_HEIGHT/3) +25;

                canvas.drawBitmap(Ximage, picX, picY, null);

            }
            else if(BOARD.charAt(index) == 'O'){

                int picX = (index%3)*(TOTAL_WIDTH/3) +25; //offset 25 for width of board bars
                int picY = (index/3)*(TOTAL_HEIGHT/3) +25;

                canvas.drawBitmap(Oimage, picX, picY, null);

            }

        }

        // delay before next frame
        handler.postDelayed(animationThread, SPEED);
        nextFrame();

    }




}
