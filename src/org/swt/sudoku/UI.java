package org.swt.sudoku;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class UI {

    private static final int BOX_SIZE = 55;
    private SudokuRunner3 sudokuRunner;
    private Shell shell;
    private int[][] board;
    Map<Text,UserInputPoint> inputBoxes = new HashMap<Text,UserInputPoint>();
    private Button chkBtn;
    private Color orgBackColor;
    private Color orgTextColor;
    private Font answerFont = null;
    private Font font = null;
    private Font inputTextFont = null;
    private boolean readOnlyMode = false;

    public UI(SudokuRunner3 sudoku) {
        sudokuRunner = sudoku;
        board = sudokuRunner.getBoard();
    }
    
    private void configureAnswerFont( Device device ) {
        FontData fontData = new FontData();
        fontData.setHeight(13);
        fontData.setStyle(SWT.BOLD);
        answerFont = new Font( device , fontData);
    }
    
    private void configureLabelFont( Device device ) {
        FontData fontData = new FontData();
        fontData.setHeight(13);
        font = new Font( device , fontData);
    }
    
    private void configureInputBoxFont( Device device ) {
        if ( inputTextFont == null ) {
            FontData fontData = new FontData();
            fontData.setHeight(13);
            fontData.setStyle(SWT.BOLD);
            inputTextFont = new Font( device , fontData);
        }
    }


    public void show() {
        Display display = new Display();
        shell = new Shell(display);
        
        shell.setText("Sudoku");
        GridLayout layout = new GridLayout(9, false);
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        shell.setLayout(layout);


        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.heightHint = BOX_SIZE;
        gridData.widthHint = BOX_SIZE;
        gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;

        Color white = display.getSystemColor(SWT.COLOR_WHITE);
        
        configureLabelFont( shell.getFont().getDevice() );
        configureAnswerFont( shell.getFont().getDevice() );
        
        orgBackColor = shell.getBackground();
        
        // code to display board
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Composite labelCell = new Composite(shell, SWT.BORDER);
                labelCell.setLayoutData(gridData);
                labelCell.setLayout( new GridLayout() );   
                
                
                if ( (j+1) % 3 == 0 && (j+1) != 9 ) {
                    labelCell.addPaintListener( new PaintListener() {
                        @Override
                        public void paintControl(PaintEvent e) {
                            e.gc.drawLine(BOX_SIZE - 1, BOX_SIZE - 1, BOX_SIZE - 1, 0);
                        }
                    });
                }
                
                if ( (i+1) % 3 == 0 && (i+1) != 9 ) {
                    labelCell.addPaintListener( new PaintListener() {
                        @Override
                        public void paintControl(PaintEvent e) {
                            e.gc.drawLine(BOX_SIZE - 1, BOX_SIZE - 1, 0, BOX_SIZE - 1);
                        }
                    });
                }
                
                if ( board[i][j] != 0 ) {
                    Label label = createLabel(labelCell);
                    label.setText(Integer.toString(board[i][j]));
                    
                } else {
                    labelCell.setBackground(white);
                    Text input = createText(labelCell);
                    inputBoxes.put(input, new UserInputPoint(i,j));
                    
                }
                
            }
        }
        
        Composite btnComposite = new Composite(shell, SWT.NONE);
        GridData gdComposite = new GridData();
        gdComposite.horizontalSpan = 9;
        btnComposite.setLayoutData( gdComposite );
        btnComposite.setLayout(new GridLayout(4, false));
        
        GridData btnGrid = getGridDataForButton();
        chkBtn = new Button(btnComposite, SWT.PUSH);
        chkBtn.setLayoutData(btnGrid);
        chkBtn.setText("Check");
        chkBtn.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event e) {
                boolean isSuccess = checkSolution();
                showResult(isSuccess);
            }
        });
        
        
        Button resetBtn = new Button(btnComposite, SWT.PUSH);
        GridData resetBtnGridData = getGridDataForButton();
        resetBtnGridData.horizontalIndent = 10;
        resetBtn.setLayoutData(resetBtnGridData);
        resetBtn.setText("Reset");
        resetBtn.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event e) {
                clearUserInput();
            }
        }); 
        
        
        Button ansBtn = new Button(btnComposite, SWT.PUSH);
        GridData ansBtnGridData = getGridDataForButton();
        ansBtnGridData.horizontalIndent = 10;
        ansBtn.setLayoutData(resetBtnGridData);
        ansBtn.setText("Answer");
        ansBtn.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event e) {
                showAnswer();
            }
        }); 
        
        Button createBtn = new Button(btnComposite, SWT.PUSH);
        GridData createBtnGridData = getGridDataForButton();
        createBtnGridData.horizontalIndent = 170;
        createBtn.setLayoutData(createBtnGridData);
        createBtn.setText("New Puzzle");
        createBtn.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event e) {
                boolean success = sudokuRunner.generateProblemGrid(GameDifficultyLevel.EASY);
                if ( success ) {
                    displayNewPuzzle();
                }
            }
        }); 
        
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    private Label createLabel( Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setAlignment(SWT.CENTER);
        assert font != null: "Font not configured for labels";
        label.setFont(font);
        label.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, true, true) );
        return label;
    }
    
    private Text createText( Composite parent ) {
        Text input = new Text(parent, SWT.NONE);
        input.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_GREEN));
        configureInputBoxFont(shell.getFont().getDevice());
        assert inputTextFont != null : "Error no font for user input";
        input.setFont(inputTextFont);
        input.setTextLimit(1);
        
        GridData inputGD = new GridData( SWT.CENTER, SWT.CENTER, true, true);
        inputGD.horizontalIndent = BOX_SIZE / 4;
        input.setLayoutData(inputGD);
        
        // verify that the input is a valid numeric char
        input.addListener(SWT.Verify, new Listener() {
            public void handleEvent(Event e) {
                String input = e.text;
                char[] chars = new char[input.length()];
                input.getChars(0, chars.length, chars, 0);
                for (int i = 0; i < chars.length; i++) {
                    if (!('1' <= chars[i] && chars[i] <= '9')) {
                        e.doit = false;
                        return;
                    }
                }
                updateBoardDataWithUserInput((Text) e.widget, input);
                
                
            }
        });
        return input;
        
    }
    
    
    private void displayNewPuzzle() {
        Control [] controls = shell.getChildren();
        int index = 0;
        for ( Control control : controls ) {
            if ( control instanceof Composite ) {
                
                Control [] widgets = ((Composite)control).getChildren();
                if ( widgets.length > 0 && ! ( widgets[0] instanceof Button) ) {
                    if ( widgets[0] instanceof Text ) {
                        inputBoxes.remove((Text) widgets[0]);
                    }
                    widgets[0].dispose();
                 
                    int xBox = index / 9;
                    int yBox = index % 9;
                    String val = Integer.toString( board[xBox][yBox] );
                    if ( board[xBox][yBox] != 0 ) {
                        ((Composite)control).setBackground( orgBackColor );
                        Label label= createLabel((Composite)control);
                        label.setText(val);
                    } else {
                        Text input = createText((Composite)control);
                        ((Composite)control).setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
                        inputBoxes.put(input, new UserInputPoint(xBox,yBox));
                    }
                    index++;
                }
                Composite comp = (Composite) control;
                // NEEDED to call layout so that composite can refresh/redraw with the new child control(text/label)
                comp.layout(true);
            }

        }
        chkBtn.setEnabled(true);
    }
    
    private void showAnswer() {
        int [][] solution = sudokuRunner.getSolution();
        for ( Entry<Text,UserInputPoint> entry : inputBoxes.entrySet() ) {
            UserInputPoint point = entry.getValue();
            Text box = entry.getKey();
            box.setEditable(false);
            orgTextColor = box.getForeground();
            assert answerFont != null : "Create font for displaying answer";
            box.setFont( answerFont );
            box.setForeground( shell.getDisplay().getSystemColor( SWT.COLOR_DARK_RED ) );
            box.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
            box.setText( Integer.toString( solution[point.x][point.y] ) );
            readOnlyMode = true;
        }
        chkBtn.setEnabled(false);
    }
    
    private boolean checkSolution() {
        int[][] solution = sudokuRunner.getSolution();
        for( Entry<Text,UserInputPoint> entry : inputBoxes.entrySet() ) {
            UserInputPoint point = entry.getValue();
            if ( board[point.x][point.y] != solution[point.x][point.y] ) {
                return false;
            }
        }
        return true;
    }
    
    private void clearUserInput() {
        for( Entry<Text,UserInputPoint> input : inputBoxes.entrySet() ) {
            input.getKey().setText("");
        }
        if ( readOnlyMode ) {
            readOnlyMode = false;
            for ( Entry<Text,UserInputPoint> entry : inputBoxes.entrySet() ) {
                Text box = entry.getKey();
                box.setEditable(true);
                assert font != null : "Create font for resetting input boxes";
                box.setFont( font );
                box.setForeground( orgTextColor );
            }
        }
        chkBtn.setEnabled(true);
    }
    private void updateBoardDataWithUserInput( Text inputBox, String inputStr ) {
        UserInputPoint point = inputBoxes.get(inputBox);
        int num = 0;
        if (inputStr.length() > 0) {
            num = Integer.parseInt(inputStr);
        }
        board[point.x][point.y] = num;
    }
    
    private void showResult( boolean isSuccess ) {
        MessageBox messageBox = null;
        if ( ! isSuccess ) {
            messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
            messageBox.setMessage("Wrong Answer. Try again!");
        } else {
            messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_WORKING);
            messageBox.setMessage("Thats right. Good job!");
        }
        messageBox.open();
    }
    
    private GridData getGridDataForButton() {
        GridData gd = new GridData();
        gd.verticalIndent = 10;
        gd.heightHint = 30;
        gd.widthHint = 80;
        gd.grabExcessVerticalSpace = true;
        gd.grabExcessHorizontalSpace = true;
        return gd;
    }
    
    private static class UserInputPoint {
        int x;
        int y;
        UserInputPoint( int x, int y ) {
            this.x = x;
            this.y = y;
        }
    }
    
    public static void main(String [] args) {
        SudokuRunner3 sudoku = new SudokuRunner3();
        boolean status = sudoku.generateProblemGrid( GameDifficultyLevel.HARD );
        assert status : "Invalid Problem generation -No Unique Solution";
       
        UI sudokuUI = new UI( sudoku );
        sudokuUI.show();
    }

}
