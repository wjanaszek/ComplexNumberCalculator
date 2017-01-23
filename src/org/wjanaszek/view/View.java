package org.wjanaszek.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class View {
	private static final String APP_NAME = "ComplexNumberCalc";
	private Text text;
	private Button button1,
					button2,
					button3,
					button4,
					button5,
					button6,
					button7,
					button8,
					button9,
					button0,
					buttonDot,
					buttonPls,
					buttonSub,
					buttonMul,
					buttonDiv,
					buttonArg,
					buttonAbs,
					buttonEqa,
					buttonEnt,
					buttonI,
					buttonRes;
	private Display display;
	private Shell shell;
	
	public Display getDisplay(){
		return display;
	}
	
	public Shell getShell(){
		return shell;
	}
	
	public Text getText() {
		return text;
	}


	public Button getButton1() {
		return button1;
	}


	public Button getButton2() {
		return button2;
	}


	public Button getButton3() {
		return button3;
	}


	public Button getButton4() {
		return button4;
	}


	public Button getButton5() {
		return button5;
	}


	public Button getButton6() {
		return button6;
	}


	public Button getButton7() {
		return button7;
	}


	public Button getButton8() {
		return button8;
	}


	public Button getButton9() {
		return button9;
	}


	public Button getButton0() {
		return button0;
	}


	public Button getButtonDot() {
		return buttonDot;
	}


	public Button getButtonPls() {
		return buttonPls;
	}


	public Button getButtonSub() {
		return buttonSub;
	}


	public Button getButtonMul() {
		return buttonMul;
	}


	public Button getButtonDiv() {
		return buttonDiv;
	}


	public Button getButtonArg() {
		return buttonArg;
	}


	public Button getButtonAbs() {
		return buttonAbs;
	}


	public Button getButtonEqa() {
		return buttonEqa;
	}


	public Button getButtonEnt() {
		return buttonEnt;
	}


	public Button getButtonI() {
		return buttonI;
	}

	public Button getButtonRes() {
		return buttonRes;
	}

	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display.getDefault();
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText(APP_NAME);
		shell.setLayout(null);
	
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(64, 10, 165, 22);
		
		button1 = new Button(shell, SWT.NONE);
		button1.setBounds(10, 51, 82, 22);
		button1.setText("1");
		
		button2 = new Button(shell, SWT.NONE);
		button2.setBounds(98, 51, 82, 22);
		button2.setText("2");
		
		button3 = new Button(shell, SWT.NONE);
		button3.setBounds(186, 51, 82, 22);
		button3.setText("3");
		
		button4 = new Button(shell, SWT.NONE);
		button4.setBounds(10, 79, 82, 22);
		button4.setText("4");
		
		button5 = new Button(shell, SWT.NONE);
		button5.setBounds(98, 79, 82, 22);
		button5.setText("5");
				
		button6 = new Button(shell, SWT.NONE);
		button6.setBounds(186, 79, 82, 22);
		button6.setText("6");
		
		button7 = new Button(shell, SWT.NONE);
		button7.setBounds(10, 107, 82, 22);
		button7.setText("7");
		
		button8 = new Button(shell, SWT.NONE);
		button8.setBounds(98, 107, 82, 22);
		button8.setText("8");
		
		button9 = new Button(shell, SWT.NONE);
		button9.setBounds(186, 107, 82, 22);
		button9.setText("9");
				
		button0 = new Button(shell, SWT.NONE);
		button0.setBounds(10, 135, 82, 22);
		button0.setText("0");
		
		buttonDot = new Button(shell, SWT.NONE);
		buttonDot.setBounds(98, 135, 82, 22);
		buttonDot.setText(".");
		
		buttonPls = new Button(shell, SWT.NONE);
		buttonPls.setBounds(186, 135, 82, 22);
		buttonPls.setText("+");
		
		buttonSub = new Button(shell, SWT.NONE);
		buttonSub.setBounds(274, 51, 82, 22);
		buttonSub.setText("-");
		
		buttonMul = new Button(shell, SWT.NONE);
		buttonMul.setBounds(274, 79, 82, 22);
		buttonMul.setText("*");
		
		buttonDiv = new Button(shell, SWT.NONE);
		buttonDiv.setBounds(274, 107, 82, 22);
		buttonDiv.setText("/");
		
		buttonEqa = new Button(shell, SWT.NONE);
		buttonEqa.setBounds(274, 135, 82, 22);
		buttonEqa.setText("=");
		
		buttonArg = new Button(shell, SWT.NONE);
		buttonArg.setBounds(10, 163, 82, 22);
		buttonArg.setText("arg");
		
		buttonAbs = new Button(shell, SWT.NONE);
		buttonAbs.setBounds(98, 163, 82, 22);
		buttonAbs.setText("abs");
		
		buttonI = new Button(shell, SWT.NONE);
		buttonI.setBounds(186, 163, 82, 22);
		buttonI.setText("i");
		
		buttonEnt = new Button(shell, SWT.NONE);
		buttonEnt.setBounds(274, 163, 82, 22);
		buttonEnt.setText("ENTER");
		
		buttonRes = new Button(shell, SWT.NONE);
		buttonRes.setBounds(262, 10, 82, 22);
		buttonRes.setText("RESET");
		
		Label lblWejcie = new Label(shell, SWT.NONE);
		lblWejcie.setBounds(10, 16, 48, 16);
		lblWejcie.setText("wejście:");
	}
}
