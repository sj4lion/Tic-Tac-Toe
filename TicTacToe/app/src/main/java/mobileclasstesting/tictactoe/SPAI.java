package mobileclasstesting.tictactoe;


// Single Player AI
// cant make it use decision trees or it will never let the user win
// to make game interesting, make ai simply make random moves
public class SPAI {

    // gets board, picks random logical move
    public int getMove(String Board){

        int move = 0;

        boolean moveFound = false;

        while(!moveFound){

            int spot = (int)(Math.random()*8.99);

            if(Board.charAt(spot) == 'E'){

                move = spot;
                moveFound = true;

            }


        }

        return move;

    }

}
