import java.util.*;
import java.io.*;


class Sudoku
{
    /* SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.  For 
     * a standard Sudoku puzzle, SIZE is 3 and N is 9. */
    int SIZE, N;

    /* The grid contains all the numbers in the Sudoku puzzle.  Numbers which have
     * not yet been revealed are stored as 0. */
    int Grid[][];


    /* The solve() method should remove all the unknown characters ('x') in the Grid
     * and replace them with the numbers from 1-9 that satisfy the Sudoku puzzle. */
    public void solve()
    {
        /* INSERT YOUR CODE HERE */
        
        /* since I intend to solve the sudoku using
            backtracking, I create three stacks that
            keep track of the coordinates of the last
            row changed, last column changed and the
            last value/number added to the sudoku */

        Stack<Integer> lastRowChanged = new Stack<Integer>();
        Stack<Integer> lastColumnChanged = new Stack<Integer>();
        Stack<Integer> lastValidVal = new Stack<Integer>();

        for(int i = 0; i < this.N; i++) {
            for(int j = 0; j < this.N; j++) {

                /* there is something to do only if
                    the cell is empty (that is, it
                    contains 0) */
                if(this.Grid[i][j] == 0) {
                    /* the boolean keeps track of whether it is
                        possible to insert a number at the given
                        cell or not */
                    boolean ableToInsert = false;
                    for(int k = 1; k < this.N + 1; k++) {
                        if(this.isValid(i,j,k)) {
                            /* (as soon as we reach the first valid number
                                we break and exit this for loop)
                                if it is valid to insert the number
                                k at the given cell, then we assign
                                the number k to the cell and update
                                the 3 stacks to account for this change */
                            this.Grid[i][j] = k;
                            lastRowChanged.push(i);
                            lastColumnChanged.push(j);
                            lastValidVal.push(k);
                            ableToInsert = true;
                            break;
                        }
                    }

                    /* if we are not able to insert a number at the
                        given cell, that means that we made a mistake
                        somewhere and so we must go back and change
                        our numbers. for this purpose we use the backTrack method */
                    if(!ableToInsert) {
                        /* to the method backTrack, we give as input the row
                            and column numbers where we last made changes. we
                            also pass the stacks so that they may be updated */
                        backTrack(lastRowChanged.peek(), lastColumnChanged.peek(), lastRowChanged, lastColumnChanged, lastValidVal);
                        /* while the method backTrack was running, it went back and
                            changed some cells to zero (since the previous numbers
                            didn't work). so the nested loop below finds the first
                            zero in the sudoku and makes the entire, bigger loop
                            run from there again */
                        search:
                        for(int p = 0; p < this.N; p++) {
                            for(int q = 0; q < this.N; q++) {
                                if(this.Grid[p][q] == 0) {
                                    if(j == this.N - 1 && p != 0) {
                                        i = p-1;
                                    }
                                    else {
                                        i = p;
                                    }
                                    j = q-1;
                                    break search;
                                }
                            }
                        }
                    }
                    
                    
                }
            }
        }
        System.out.println("\n\n");

    }

    /* the method backTrack takes as input the row
        and column number (therefore a specific cell)
        where we want to make a change and also the
        3 stacks so that we may update them */
    private void backTrack(int currentRow, int currentColumn, Stack<Integer> lastRowChanged, Stack<Integer> lastColumnChanged, Stack<Integer> lastValidVal) {
        /* we start trying values from the value
            that is one higher than what the cell
            was storing previously 

            for example, if the cell stored 5 previously,
            we will now start trying values from 6 onwards */
        for(int p = lastValidVal.peek()+1; p < this.N + 1; p++) {
            /* we check if its valid to enter the value 'p'
                at the given row and column (therefore the
                specific cell) */
            if(this.isValid(currentRow, currentColumn, p)) {
                this.Grid[currentRow][currentColumn] = p;
                /* we remove the previous (incorrect) value
                    stored in this cell from the stack and
                    update the stack with this new value */
                lastValidVal.pop();
                lastValidVal.push(p);
                return;
            }
        }

        /* if we reach this point, that means that 
            there was no value which could be inserted
            in the cell. this means that we must further
            backtrack and therefore we pop the row, column
            numbers and the last valid value from the stacks */
        lastRowChanged.pop(); lastColumnChanged.pop(); lastValidVal.pop();
        this.Grid[currentRow][currentColumn] = 0;
        /* here we recursively call backTrack with the last valid
            spot in the sudoku. we stop only when we are able to
            succesfully enter a value in the cell */
        this.backTrack(lastRowChanged.peek(), lastColumnChanged.peek(), lastRowChanged, lastColumnChanged, lastValidVal);
        return;
    }

    /* isValid takes as input a row number, a column number
        (therefore a specific cell) and a number that we
        want to try. it tells us if its okay or not for
        that number to be in that cell */
    private boolean isValid(int row, int column, int number) {
        /* checks if the number occurs
            only once in its row */
        for(int j = 0; j < this.N; j++) {
            /* we use the condition j != column
                so that the method doesn't check
                at the number's own place (otherwise
                the method would always return false)

                we have similar conditions for column
                checking and box checking as well */
            if (j != column) {
                if(number == this.Grid[row][j]) {
                    return false;
                }
            }
        }

        /* checks if the number occurs
            only once in its column */
        for(int i = 0; i < this.N; i++) {
            if (i != row) {
                if(number == this.Grid[i][column]) {
                    return false;
                }
            }
        }

        /* checks if the number occurs
            only once in its box */
        
        /* here we take advantage of integer
            division. consider the case of a
            3x3 sudoku. the 'boxes' are in a
            3x3 grid. 

            rows 0,1,2 belong to a box which starts with row 0
            rows 3,4,5 belong to a box which starts with row 3
            rows 6,7,8 belong to a box which starts with row 6
            similarly for columns as well

            for example, for row 4:
            boxRow = (4/3) * 3 = 1 * 3 = 3 */
        int boxRow = (row/this.SIZE) * this.SIZE;
        int boxColumn = (column/this.SIZE) * this.SIZE;

        for(int k = boxRow; k < boxRow + this.SIZE; k++) {
            for(int p = boxColumn; p < boxColumn + this.SIZE; p++) {
                if (k != row && p != column) {
                    if(number == this.Grid[k][p]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /*****************************************************************************/
    /* NOTE: YOU SHOULD NOT HAVE TO MODIFY ANY OF THE FUNCTIONS BELOW THIS LINE. */
    /*****************************************************************************/
 
    /* Default constructor.  This will initialize all positions to the default 0
     * value.  Use the read() function to load the Sudoku puzzle from a file or
     * the standard input. */
    public Sudoku( int size )
    {
        SIZE = size;
        N = size*size;

        Grid = new int[N][N];
        for( int i = 0; i < N; i++ ) 
            for( int j = 0; j < N; j++ ) 
                Grid[i][j] = 0;
    }


    /* readInteger is a helper function for the reading of the input file.  It reads
     * words until it finds one that represents an integer. For convenience, it will also
     * recognize the string "x" as equivalent to "0". */
    static int readInteger( InputStream in ) throws Exception
    {
        int result = 0;
        boolean success = false;

        while( !success ) {
            String word = readWord( in );

            try {
                result = Integer.parseInt( word );
                success = true;
            } catch( Exception e ) {
                // Convert 'x' words into 0's
                if( word.compareTo("x") == 0 ) {
                    result = 0;
                    success = true;
                }
                // Ignore all other words that are not integers
            }
        }

        return result;
    }


    /* readWord is a helper function that reads a word separated by white space. */
    static String readWord( InputStream in ) throws Exception
    {
        StringBuffer result = new StringBuffer();
        int currentChar = in.read();
	String whiteSpace = " \t\r\n";
        // Ignore any leading white space
        while( whiteSpace.indexOf(currentChar) > -1 ) {
            currentChar = in.read();
        }

        // Read all characters until you reach white space
        while( whiteSpace.indexOf(currentChar) == -1 ) {
            result.append( (char) currentChar );
            currentChar = in.read();
        }
        return result.toString();
    }


    /* This function reads a Sudoku puzzle from the input stream in.  The Sudoku
     * grid is filled in one row at at time, from left to right.  All non-valid
     * characters are ignored by this function and may be used in the Sudoku file
     * to increase its legibility. */
    public void read( InputStream in ) throws Exception
    {
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                Grid[i][j] = readInteger( in );
            }
        }
    }


    /* Helper function for the printing of Sudoku puzzle.  This function will print
     * out text, preceded by enough ' ' characters to make sure that the printint out
     * takes at least width characters.  */
    void printFixedWidth( String text, int width )
    {
        for( int i = 0; i < width - text.length(); i++ )
            System.out.print( " " );
        System.out.print( text );
    }


    /* The print() function outputs the Sudoku grid to the standard output, using
     * a bit of extra formatting to make the result clearly readable. */
    public void print()
    {
        // Compute the number of digits necessary to print out each number in the Sudoku puzzle
        int digits = (int) Math.floor(Math.log(N) / Math.log(10)) + 1;

        // Create a dashed line to separate the boxes 
        int lineLength = (digits + 1) * N + 2 * SIZE - 3;
        StringBuffer line = new StringBuffer();
        for( int lineInit = 0; lineInit < lineLength; lineInit++ )
            line.append('-');

        // Go through the Grid, printing out its values separated by spaces
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                printFixedWidth( String.valueOf( Grid[i][j] ), digits );
                // Print the vertical lines between boxes 
                if( (j < N-1) && ((j+1) % SIZE == 0) )
                    System.out.print( " |" );
                System.out.print( " " );
            }
            System.out.println();

            // Print the horizontal line between boxes
            if( (i < N-1) && ((i+1) % SIZE == 0) )
                System.out.println( line.toString() );
        }
    }


    /* The main function reads in a Sudoku puzzle from the standard input, 
     * unless a file name is provided as a run-time argument, in which case the
     * Sudoku puzzle is loaded from that file.  It then solves the puzzle, and
     * outputs the completed puzzle to the standard output. */
    public static void main( String args[] ) throws Exception
    {
        InputStream in;
        if( args.length > 0 ) 
            in = new FileInputStream( args[0] );
        else
            in = System.in;

        // The first number in all Sudoku files must represent the size of the puzzle.  See
        // the example files for the file format.
        int puzzleSize = readInteger( in );
        if( puzzleSize > 100 || puzzleSize < 1 ) {
            System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
            System.exit(-1);
        }

        Sudoku s = new Sudoku( puzzleSize );

        // read the rest of the Sudoku puzzle
        s.read( in );

        // Solve the puzzle.  We don't currently check to verify that the puzzle can be
        // successfully completed.  You may add that check if you want to, but it is not
        // necessary.
        //long startTime = System.nanoTime();
        s.solve();
        //long stopTime = System.nanoTime() - startTime;
        //System.out.println(stopTime/(Math.pow(10,6)) + " milliseconds\n");

        // Print out the (hopefully completed!) puzzle
        s.print();
    }
}
