package com.mycompany.sudokusolver;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Richard
 */
public class SudokuSolver extends Application{
    static int [][] board = new int [9][9];
    /**
     * Check if the current square can be a certain value
     * @param r is the current row
     * @param c is the current column
     * @param v is the current number
     * @return whether the current cell does not collide with the other cells
     */
    static boolean canPlace (int r, int c, int v) {
            boolean contains [] = new boolean [10]; //this array checks if a number is contained in the current section
            //check the column
            for(int i = 0; i < 9; i++) {
                contains [board [i][c]] = true;
            }
            //check the row
            for(int j = 0; j < 9; j++) {
                contains [board [r][j]] = true;
            }
            //check the current 3x3 section
            int x = r-r%3, y = c-c%3;
            for(int i = x; i < x+3; i++) {
                for(int j = y; j < y+3; j++) {
                    contains [board [i][j]] = true;
                }
            }
            //return if the number is not found
            return !contains [v];
    }
    /**
     * Check if the current Sudoku board is valid
     * @return if the current board is valid
     */
    static boolean check() {
        //check all the columns
        for(int i =  0; i < 9; i++) {
            boolean contains [] = new boolean [10];
            //return if any of the columns are invalid
            for(int j = 0; j < 9; j++) {
                if(contains [board [i][j]] && board [i][j] != 0) return false;
                contains [board [i][j]] = true;
            }
        }
        //check all the rows
        for(int j = 0; j < 9; j++) {
            boolean contains [] = new boolean [10];
            //return if the rows are invalid
            for(int i = 0; i < 9; i++) {
                if(contains [board [i][j]] && board [i][j] != 0) return false;
                contains [board [i][j]] = true;
            }
        }
        //check all the 3x3 sections
        for(int i = 0; i < 9; i += 3) {
            for(int j = 0; j < 9; j += 3) {
                boolean contains [] = new boolean [10];
                //return if any of them are invalid
                for(int r = i; r < i+3; r++) {
                    for(int c = j; c < j+3; c++) {
                        if(contains [board [r][c]] && board [r][c] != 0) return false;
                        contains [board [r][c]] = true;
                    }
                }
            }
        }
        return true;
    }
    /**
     * Recursively solve the sudoku board with back tracking
     * @param r is the current row
     * @param c is the current column
     * @return if the current solution is valid
     */
    static boolean solve(int r, int c) {
        //if the end of the column is reached, go onto the next row
        if(c == 9) {
            r++;
            c = 0;
        }
        //if the last column is completed, check the board
        if(r == 9) return check();
        //if the board has a preinputed value, continue to the next cell
        if(board [r][c] != 0) return solve(r, c+1);
        //check all possible values for the current cell
        for(int k = 1; k <= 9; k++) {
            if(canPlace(r, c, k)) {
                    board [r][c] = k;
                    if(solve(r, c+1)) return true;
                    board [r][c] = 0;
            }
        }
        return false;
    }
    /**
     * Run the setup
     */
    static void run() {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                fields[i][j].setDisable(true);
                fields[i][j].setStyle("-fx-control-inner-background: white; -fx-opacity: 1;");
            }
        }
        long start = System.currentTimeMillis();
        if(check() && solve(0, 0)) {
            long end = System.currentTimeMillis();
            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 9; j++){
                    fields[i][j].setText("" + board[i][j]);
                    if(placed[i][j]){
                        fields[i][j].setStyle("-fx-control-inner-background: lightblue; -fx-opacity: 1;");
                    }
                }
            }
            output.setTextFill(Color.DARKGREEN);
            output.setText("It took " + (end-start) + " Milliseconds to solve!");
        }
        else {
            output.setTextFill(Color.RED);
            output.setText("NO SOLUTION");
        }
    }
    static Label output;
    static Button solve, reset, edit;
    static boolean placed [][] = new boolean [9][9];
    static TextField fields [][] = new TextField [9][9];
    public static void main(String[] args) {
        launch(args);
    }
    static int x = 0, y = 0;
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Sudoku Solver");
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10,10,10,10));
        
        Scene scene = new Scene(pane, 500, 500);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event->{
            event.consume();
            switch(event.getCode()){
                case TAB:
                case RIGHT:
                    x++;
                    if(x == 9){
                        y = (y+1)%9;
                        x = 0;
                    }
                    break;
                case LEFT:
                    x--;
                    if(x == -1){
                        y = ((y-1)%9+9)%9;
                        x = 8;
                    }
                    break;
                case DOWN:
                    y = (y+1)%9;
                    break;
                case UP:
                    y = ((y-1)%9+9)%9;
                    break;
            }
            fields[x][y].requestFocus();
        });
        createGrid(pane);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
    static void reset(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                fields[i][j].setDisable(false);
                fields[i][j].setText("");
                fields[i][j].setStyle("-fx-display-caret: false; -fx-focus-color: blue; -fx-faint-focus-color: blue; -fx-control-inner-background: white");
                board[i][j] = 0;
                placed[i][j] = false;
            }
        }
        output.setText("");
        solve.setDisable(false);
        solve.setBackground(Background.fill(Color.LIGHTGREEN));
        edit.setDisable(true);
        edit.setBackground(Background.fill(Color.GRAY));
        x = 0; y = 0;
        fields[x][y].requestFocus();
    }
    static void edit(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(!placed[i][j]){
                    fields[i][j].setText("");
                    board[i][j] = 0;
                }
                fields[i][j].setStyle("-fx-display-caret: false; -fx-focus-color: blue; -fx-faint-focus-color: blue; -fx-control-inner-background: white");
                fields[i][j].setDisable(false);
            }
        }
        x = 0; y = 0;
        fields[0][0].requestFocus();
    }
    public void createGrid(GridPane pane){
        createBoxes(pane);
        createOutlines(pane);
        output = new Label();
        output.setText("");
        output.setAlignment(Pos.CENTER);
        output.setPrefSize(1000, 1000);
        GridPane.setConstraints(output, 1, 10, 9, 1, HPos.CENTER, VPos.CENTER);
        pane.getChildren().add(output);
        
        solve = new Button();
        solve.setText("Solve");
        solve.setBackground(Background.fill(Color.LIGHTGREEN));
        solve.setTextFill(Color.WHITE);
        solve.setPrefSize(1000, 1000);  
        solve.setOnAction((event) ->{
            solve.setDisable(true);
            solve.setBackground(Background.fill(Color.GRAY));
            run();
            edit.setDisable(false);
            edit.setBackground(Background.fill(Color.ORANGE));
        });
        GridPane.setConstraints(solve, 4, 11);
        pane.getChildren().add(solve);
        
        reset = new Button();
        reset.setText("Reset");
        reset.setBackground(Background.fill(Color.LIGHTBLUE));
        reset.setTextFill(Color.WHITE);
        reset.setPrefSize(1000, 1000);
        reset.setOnAction((event) ->{
            reset();
        });
        GridPane.setConstraints(reset, 6, 11);
        pane.getChildren().add(reset);
        
        edit = new Button();
        edit.setText("Edit");
        edit.setBackground(Background.fill(Color.GRAY));
        edit.setTextFill(Color.WHITE);
        edit.setPrefSize(1000, 1000);
        edit.setDisable(true);
        edit.setOnAction((event) ->{
            edit.setDisable(true);
            edit.setBackground(Background.fill(Color.GRAY));
            edit();
            solve.setDisable(false);
            solve.setBackground(Background.fill(Color.LIGHTGREEN));
            output.setText("");
        });
        GridPane.setConstraints(edit, 5, 11);
        pane.getChildren().add(edit);
    }
    public void createBoxes(GridPane pane){
        for(int j = 1; j <= 9; j++){
            for(int i = 1; i <= 9; i++){
                TextField text = new TextField();
                text.setStyle("-fx-display-caret: false; -fx-focus-color: blue; -fx-faint-focus-color: blue; -fx-control-inner-background: white");
                text.setAlignment(Pos.CENTER);
                fields[i-1][j-1] = text;
                int r = i-1, c = j-1;
                text.setOnKeyTyped((event) ->{
                    String key = event.getCharacter();
                    if(key.charAt(0) == 8){
                        text.setText("");
                        board[r][c] = 0;
                        placed[r][c] = false;
                    }
                    else if(!Character.isDigit(key.charAt(0)) || key.equals("0")){
                        text.setText(text.getText().replace(key, ""));
                    }
                    else{
                        text.setText(key);
                        board[r][c] = key.charAt(0)-'0';
                        placed[r][c] = true;
                    }
                });
                text.setOnMouseClicked((event)->{
                    x = r; y = c;
                    text.requestFocus();
                });
                text.setPrefSize(1000, 1000);
                GridPane.setConstraints(text, i, j);        
                pane.getChildren().add(text);
            }    
        }
    }
    static void createOutlines(GridPane pane){
        for(int i = 1; i <= 9; i+=3){
            for(int j = 1; j <= 9; j+=3){
                Label l = new Label();
                l.setBorder(Border.stroke(Color.BLACK));
                l.setPrefSize(1000, 1000);
                l.setDisable(true);
                GridPane.setConstraints(l, i, j, 3 , 3, HPos.LEFT, VPos.TOP);
                pane.getChildren().add(l);
            }
        }
    }
}