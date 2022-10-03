import java.util.*;

public class Solution{
    
    public static void main(String[] args){
        int lives = 200;
        String[] input = new String[20];
        input[ 0]="XXXXXXXXX XXXXXXXXXXX";
        input[ 1]="XXX XXXXXXX XXXXXX X";
        input[ 2]="XXX      XX XXXX   X";
        input[ 3]="XXXXXXX XXXXXXXXXXXX";
        input[ 4]="XXXXXXX XX      XXXX";
        input[ 5]="XXX  XXXXX XXXX XXXX";
        input[ 6]="XX  X XXXX   XX XXXX";
        input[ 7]="XXX XXXXXXXX XX XXXX";
        input[ 8]="XX  X  XXXXX XX XXXX";
        input[ 9]="XXXXXX       XX XXXX";
        input[10]="X XXXX XX  XXXX XXXX";
        input[11]="     XXXX  XXXX XXXX";
        input[12]="XXXXXXXXXXXXXXX XXXX";
        input[13]="XXXXXX  XXXX    XXXX";
        input[14]="XX XX XXXXXX XX XXXX";
        input[15]="X  XX XXXXXX XX XXXX";
        input[16]="XX XX X  X   XX XX  ";
        input[17]="X  XXXXXXX XXXX XX X";
        input[18]="XX XXXXXXX XXXXXXX X";
        input[19]="XX XXXXXXX XXXXXXX X";
        int posX=10;
        int posY=10;
        
        boolean[][] maze = new boolean[20][20];
        for(int i=0;i<20;i++){
            for(int j=0;j<20;j++){
                if(input[j].charAt(i)=='X'){
                    maze[i][j]=false;
                }else{
                    maze[i][j]=true;
                }
            }
        }
        System.out.println(posX+" "+posY);
        printboard(maze,posX,posY);
        Brain myBrain = new Brain();
        
        while(lives > 0){
            String move =myBrain.getMove(maze[posX][posY - 1],maze[posX][posY+1],maze[posX+1][posY],maze[posX-1][posY]);
            if(move=="north"&&maze[posX][posY-1]){
                posY--;
            }else if(move=="south"&&maze[posX][posY+1]){
                posY++;
            }else if(move=="east"&&maze[posX+1][posY]){
                posX++;
            }else if(move=="west"&&maze[posX-1][posY]){
                posX--;
            }
            System.out.println(posX+" "+posY+" "+lives);
            printboard(maze,posX,posY);
            lives--;
            if(posY%19==0||posX%19==0){
                System.out.println(posX+","+posY);
                System.exit(0);
            }
        }
        System.out.println("You died in the maze!");
    }

    
    public static void printboard(boolean[][] board, int posX, int posY){
        for(int y=0;y<20;y++){
            for(int x=0;x<20;x++){
                if(x==posX&&y==posY){
                    System.out.print(":)");
                }else{
                    if(board[x][y]==true){
                        System.out.print("  ");
                    }else{
                        System.out.print("■ ");
                    }
                }
            }
            System.out.println();
        }
        try{
            Thread.sleep(100);
        }catch(InterruptedException ex){
            Thread.currentThread().interrupt();
        }
    }
}

class Brain{
    static String prev = "";
    static LinkedList<String> path = new LinkedList<>(); //list containing directions used to date, directions that led to dead ends are removed
    static LinkedList<Coordinate> coordinates = new LinkedList<>(); //list containing Coordinate objects
    
    static int x = 0; //used to create Coordinate objects
    static int y = 0;

    static boolean first = true; //checks to see if this is the first move
    static boolean trapped = false; //used to flag trapped players

    public String getMove(boolean north, boolean south, boolean east, boolean west){
        
        String result = "";
        Coordinate newCoordinate;

        if(first){
            //initilaising new Coordinate object and adding to list
            newCoordinate = new Coordinate(x, y, north, south, east, west);
            coordinates.add(newCoordinate);
            first = false;
        }else if(!trapped){
            //adds new coordinate to list, based on previous move - not used if previous move was used to correct a trap
            switch(path.get(path.size() - 1)){
                case("north") : y++; break;
                case("south") : y--; break;
                case("east") : x++; break;
                case("west") : x--; break;
            }
            newCoordinate = new Coordinate(x, y, north, south, east, west);
            coordinates.add(newCoordinate);
        }
        Coordinate currentCoordinate = coordinates.get(coordinates.size() - 1);

        if(!first){
            x = currentCoordinate.returnX();
            y = currentCoordinate.returnY();
        }

        trapped = false;
        System.out.println("CURRENT SPACE COORDS " + currentCoordinate.returnX() + ", " + currentCoordinate.returnY());
        result = (currentCoordinate.returnValidDirection());

        if(result.equals("TRAPPED")){

            trapped = true;
            System.out.println("TRAPPED!!!");
            System.out.println();

            //switch statement used to correct trap situations.
            //looks at last move, reverses it, then deletes the previous move from the list,
            //leaving only moves that made progress
            switch(path.get(path.size() - 1)){
                case("north") : System.out.println("RETURNING SOUTH"); result = "south"; path.removeLast(); break;
                case("south") : System.out.println("RETURNING NORTH"); result = "north"; path.removeLast(); break;
                case("east") : System.out.println("RETURNING WEST"); result = "west"; path.removeLast(); break;
                case("west") : System.out.println("RETURNING EAST"); result = "east"; path.removeLast(); break;
            }
            //removing last coordinate
            coordinates.removeLast();
        }

        System.out.println("CURRENT COORDINATES: " + x + ", " + y);
        System.out.println("Valid Direction: " + result);
        System.out.print("Coordinate list: ");
        Coordinate.printCoords();
        System.out.println("Previous direction: " + ((path.size() > 0) ? (path.get(path.size() - 1)) : " "));

        System.out.print("directions so far: ");
        for(int i = 0; i < path.size(); i++)
            System.out.print(path.get(i) + " ");
        
        System.out.println("\nNo. of directions: " + path.size());

        
        if(trapped) return result; //does not add directions used to correct traps to path queue
        
        path.add(result);
        return result;
    }
}
class Coordinate{
    private int x, y;
    private boolean north, south, east, west;

    static LinkedList <String> visitedCoords = new LinkedList<>();

    Coordinate(int x, int y, boolean north, boolean south, boolean east, boolean west){
        String coord = Integer.toString(x) + Integer.toString(y);
        if(!visitedCoords.contains(coord))
            visitedCoords.add(coord);

        this.x = x;
        this.y = y;
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }

    int returnX(){
        return this.x;
    }
    int returnY(){
        return this.y;
    }

    String returnValidDirection(){

        for(int i = 4; i >= 0; i--){
            if(i == 0 && north && !(visitedCoords.contains(Integer.toString(x) + Integer.toString(y + 1)))){
                return "north";
            }if(i == 1 && south && !(visitedCoords.contains(Integer.toString(x) + Integer.toString(y - 1)))){
                return "south";
            }if(i == 2 && east && !(visitedCoords.contains(Integer.toString(x + 1) + Integer.toString(y)))){
                return "east";
            }if(i == 3 && west && !(visitedCoords.contains(Integer.toString(x - 1) + Integer.toString(y)))){
                return "west";
            }
        }
        return "TRAPPED";
    }
    static void printCoords(){
        for(int i = 0; i < visitedCoords.size(); i++)
            System.out.print("(" + visitedCoords.get(i) + ")");

        System.out.println();
    }
    static void addCoord(String input){
        visitedCoords.add(input);
    }
}