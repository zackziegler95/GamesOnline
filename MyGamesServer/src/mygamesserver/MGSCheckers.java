package mygamesserver;

public class MGSCheckers extends MGSGame {
    private int[][] board = null;
    private String turn = "";
    
    // Inherits String[] players from MGSGame
    
    public MGSCheckers(String[] p) {
        super("Checkers", p);
        
        board = new int[][]{
            {3, 0, 3, 0, 3, 0, 3, 0},
            {0, 3, 0, 3, 0, 3, 0, 3},
            {3, 0, 3, 0, 3, 0, 3, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 1}};
        turn = p[0];
    }
    
    public String getBoard() {
        return intBoardToString(board);
    }
    
    private String intBoardToString(int[][] b) {
        String res = "[";
        for (int[] y : b) {
            res += "[";
            for (int x: y) {
                res += x+",";
            }
            res = res.substring(0, res.length()-1);
            res += "],";
        }
        res = res.substring(0, res.length()-1);
        res += "]";
        return res;
    }
    
    public int[][] stringBoardToInt(String b) {
        int[][] res = new int[8][8];
        String[] rows = b.substring(1, b.length()-1).split("\\],\\[");
        rows[0] = rows[0].substring(1, rows[0].length());
        rows[7] = rows[7].substring(0, rows[7].length()-1);

        for (int y = 0; y < 8; y++) {
            String chars[] = rows[y].split(",");
            for (int x = 0; x < 8; x++) {
                res[y][x] = Integer.parseInt(chars[x]);
            }
        }
        return res;
    }
    
    public String getRotatedBoard() {
        return getRotatedBoard(intBoardToString(board));
    }
    
    public String getRotatedBoard(String currentBoard) {
        String res = currentBoard;
        res = res.replace("1", "-1");
        res = res.replace("3", "1");
        res = res.replace("-1", "3");
        res = res.replace("2", "-2");
        res = res.replace("4", "2");
        res = res.replace("-2", "4");
        res = new StringBuffer(res).reverse().toString();
        res = res.replace("[", "*");
        res = res.replace("]", "[");
        res = res.replace("*", "]");
        return res;
    }
    
    public void setBoard(int[][] newBoard) {
        if (turn.equals(players[0])) {
            board = newBoard;
        } else {
            String newBoardStr = intBoardToString(newBoard);
            newBoardStr = getRotatedBoard(newBoardStr);
            
            int[][] realBoard = new int[8][8];
            String res = newBoardStr.substring(1, newBoardStr.length()-1);
            String[] rows = res.split("\\],\\[");
            rows[0] = rows[0].substring(1, rows[0].length());
            rows[7] = rows[7].substring(0, rows[7].length()-1);

            for (int y = 0; y < 8; y++) {
                String chars[] = rows[y].split(",");
                for (int x = 0; x < 8; x++) {
                    realBoard[y][x] = Integer.parseInt(chars[x]);
                }
            }
            board = realBoard;
        }
    }
    
    /**
     * Checks a move submitted by either player.
     * 
     * @param player name of the player
     * @param from (x, y) coordinate of the source
     * @param to (x, y) coordinate of the destination
     * @return 0 means not ok, 1 means ok, 2 means ok because there is an enemy
     * to jump over
     */
    public int checkMove(String player, int[] from, int[] to) {
        int[][] b = null;
        if (player.equals(players[0])) {
            b = board;
        } else if (player.equals(players[1])) {
            b = new int[8][8];
            String[] rows = getRotatedBoard().substring(1, getRotatedBoard().length()-1).split("\\],\\[");
            rows[0] = rows[0].substring(1, rows[0].length());
            rows[7] = rows[7].substring(0, rows[7].length()-1);

            for (int y = 0; y < 8; y++) {
                String chars[] = rows[y].split(",");
                for (int x = 0; x < 8; x++) {
                    b[y][x] = Integer.parseInt(chars[x]);
                }
            }
        } else {
            System.err.println("Error: player is not either of the recorded players");
        }
        
        if (b[to[1]][to[0]] == 0) {
            // If the new position is above the old position (only for non-kings)
            if (b[from[1]][from[0]] == 1 && to[1] > from[1]) {
                return 0;
            }
            
            // If the new position is directly diagonal to the piece
            if (Math.abs(to[0]-from[0]) == 1 && Math.abs(to[1]-from[1]) == 1) {
                return 1;
            }
            
            // If the new position is two spaces away diagonal, and there is an enemy in the way
            if (Math.abs(to[0]-from[0]) == 2 && Math.abs(to[1]-from[1]) == 2) {
                int dir_x = (int) ((to[0]-from[0])/2);
                int dir_y = (int) ((to[1]-from[1])/2);
                int enemy_num = b[from[1]+dir_y][from[0]+dir_x];
                if (enemy_num == 3 || enemy_num == 4) {
                    return 2;
                }
            }
        }
        return 0;
    }
    
    /**
     * Checks if no more moves can be made for the player.
     * 
     * @return true if game is over, false otherwise
     */
    public boolean checkEnd(String player) {
        int[][] b;
        if (player.equals(players[0])) {
            b = board;
        } else {
            b = stringBoardToInt(getRotatedBoard());
        }
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (b[y][x] == 1 || b[y][x] == 2) {
                    for (int j = 0; j < 8; j++) {
                        for (int k = 0; k < 8; k++) {
                            int[] from = {x, y};
                            int[] to = {k, j}; 
                            if (checkMove(player, from, to) != 0) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public String getTurn() {
        return turn;
    }
    
    public String getNotTurn() {
        if (turn.equals(players[0])) {
            return players[1];
        } else {
            return players[0];
        }
    }
    
    public void changeTurns() {
        if (turn.equals(players[0])) {
            turn = players[1];
        } else if (turn.equals(players[1])) {
            turn = players[0];
        } else {
            System.err.println("Error: turn is messed up");
        }
    }
}
