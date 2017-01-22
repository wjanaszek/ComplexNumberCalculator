package org.wjanaszek.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Stack;

import org.wjanaszek.view.*;

import com.sun.prism.paint.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.wjanaszek.exceptions.EmptyInputException;
import org.wjanaszek.exceptions.OperationException;
import org.wjanaszek.exceptions.SpaceException;
import org.wjanaszek.exceptions.VariableNotFoundException;
import org.wjanaszek.model.*;

/*
 * Klasa reprezentujaca kontroler aplikacji. Obsluguje wejscie/wyjscie programu, odpowiednio zlecajac odpowiednie zadania modelowi lub widokowi.
 * Implementuje algorytm Odwroconej Notacji Polskiej, sluzacy do przeksztalcania wejscia w odpowiednie struktury danych umozliwiajace obliczenie wyrazenia
 * zgodnie z kolejnoscia. 
 * 
 * @author Wojciech Janaszek
 * @see org.wjanaszek.controller.ReversePolishNotation
 */
public class Controller implements ReversePolishNotation {
	private View view;
	private Model model;
	private boolean variableDefinition = false;
	private boolean variableValueChanged = false;
	private char changedVariableName;
	private char variableName;
	private String input;
	
	/*
	 * Konstruktor. Przyjmuje i ustawia odniesienia do widoku i modelu, aby moc sie z nimi komunikowac.
	 * @param view widok, ktorym bedzie zarzadzac kontroler
	 * @param model model, w ktorym kontroler bedzie przechowywal dane
	 */
	public Controller(View view, Model model){
		this.view = view;
		this.model = model;
	}
	
	@Override
	public void parse(String input) throws VariableNotFoundException {
		char[] inpt = input.toCharArray();
		StringBuilder sb = new StringBuilder();
		char c;
		int i = 0;
		boolean imaginary = false;
		
		while(i < inpt.length){
			c = inpt[i];
			// Znaleziono liczbe
			if(Character.isDigit(c)){
				sb.append(c);
				i++;
				while(i < inpt.length){
					c = inpt[i];
					if(Character.isDigit(c)){
						sb.append(c);
					}
					else {
						if(c == '.'){
							sb.append(c);
						}
						else if(c == 'i'){
							sb.append(c);
							imaginary = true;
							i++;
							continue;
						}
						else {
							break;
						}
					}
					i++;
				}
				model.getQueue().add(sb.toString());
				sb = new StringBuilder();
			}
			// Znaleziono zmienna
			if(Character.isAlphabetic(c)){
				if(inpt.length == 1){
					if(model.getVariablesHashMap().containsKey(c)){
						model.getQueue().add(model.getVariablesHashMap().get(c).toString());
						return;
					}
					else {
						throw new VariableNotFoundException();
					}
				}
				else {
					if(i == 0 && model.getVariablesHashMap().containsKey(c)){
						if(i + 1 < inpt.length && inpt[i + 1] == '='){
							model.getVariablesHashMap().remove(c);
							variableValueChanged = true;
							changedVariableName = inpt[0];
							// usun formule definiujaca zmienna
						}
					}
				}
				i++;
				foundVariable(c, i);
				if(i < inpt.length){
					c = inpt[i];
				}
				if(c == '='){
					i++;
				}
			}
			// Znaleziono dodawanie lub usuwanie
			if(c == '+' || c == '-'){
				i++;
				foundPlusOrMinus(c);
			}
			// Znaleziono mnożenie lub dzielenie
			if(c == '*' || c == '/'){
				i++;
				foundMultiplyOrDivide(c);
			}
			// Znaleziono prawy nawias
			if(c == ')'){
				i++;
				foundRightBracket(c);
			}
			// Znaleziono lewy nawias
			if(c == '('){
				i++;
				foundLeftBracket(c);
			}
		}
		
		while(!model.getStack().isEmpty()){
			model.getQueue().add(model.getStack().pop().toString());
		}
	}
	
	private void checkInput(String input) throws IOException, SpaceException, EmptyInputException {
		char[] inpt = input.toCharArray();
		int leftBrackets = 0;
		int rightBrackets = 0;
		int i = 0;
		
		if(input.length() == 0){
			throw new EmptyInputException();
		}
		
		for(char c: inpt){
			if(c == '('){
				leftBrackets++;
			}
			if(c == ')'){
				rightBrackets++;
			}
			if(c == ' '){
				throw new SpaceException();
			}
			if(i == 0 && Character.isAlphabetic(inpt[0]) && c != '=' && i == 1){
				throw new IOException("Blad - dzialanie musi byc na 2 elementach!");
			}
			i++;
		}
		if(leftBrackets != rightBrackets){
			throw new IOException("Brak poprawnie zapisanych nawiasów");
		}
	}
	
	@Override
	public ComplexNumber calculate() throws OperationException {
		Stack<ComplexNumber> stack = new Stack<ComplexNumber>();
		ComplexNumber a, b;
		String tmp;
		while(!model.getQueue().isEmpty()){
			tmp = (String) model.getQueue().poll();
			char[] element = tmp.toCharArray();
			if(Character.isDigit(element[0])){
				stack.push(ComplexNumber.valueOf(tmp));
			}
			else if(stack.size() >= 2){
				Operation operation = setOperation(element[0]);
				a = stack.pop();
				b = stack.pop();
				switch(operation){
				case ADD:	
					b.add(a);
					stack.push(b);
					break;
					
				case SUBSTRACT:
					b.substract(a);
					stack.push(b);
					break;
					
				case MULTIPLY:
					b.multiply(a);
					stack.push(b);
					break;
					
				case DIVIDE:
					b.divide(a);
					stack.push(b);
					break;
					
				case NOP:
				default:
					throw new OperationException();
				}
			}
		}
		ComplexNumber result;
		result = stack.pop();
		if(variableValueChanged){
			//System.out.println("nazwa zmienionej zmiennej: " + changedVariableName);
			//model.getVariablesHashMap().remove(changedVariableName);
			model.getVariablesHashMap().put(changedVariableName, result);
			//variableValueChanged = false;
		}
		if(variableDefinition){			// jesli byla definicja zmiennej, to dodaj ja do hash mapy zmiennych w modelu
			model.getVariablesHashMap().put(variableName, result);
			//variableDefinition = false;
		}
		return result;			// zwroc wynik
	}
	
	private Operation setOperation(char input){
		if(input == '+'){
			Operation operation = Operation.ADD;
			return operation;
		}
		else if(input == '-'){
			Operation operation = Operation.SUBSTRACT;
			return operation;
		}
		else if(input == '*'){
			Operation operation = Operation.MULTIPLY;
			return operation;
		}
		else if(input == '/'){
			Operation operation = Operation.DIVIDE;
			return operation;
		}
		else {
			Operation operation = Operation.NOP;
			return operation;
		}
	}
	
	private boolean isNumber(char[] input){
		for(char c: input){
			if(!Character.isDigit(c) && c != '\n' && c != '.' && c != 'i'){
				return false;
			}
		}
		return true;
	}
	
	/* 
	 * Jesli zmienna znajduje sie w modelu, to dodaj jej wartosc do kolejki, w przeciwnym razie zakomunikuj definicje nowej zmiennej 
	*/
	private void foundVariable(char c, int i){
		if(model.getVariablesHashMap().containsKey(c)){			// zmien wartosc zmiennej
			model.getQueue().add(model.getVariablesHashMap().get(c).toString());
			variableValueChanged = true;
			changedVariableName = c;
		}
		else {
			if(i - 1 == 0){
				variableDefinition = true;
				variableName = c;
			}
		}
	}
	
	private void foundPlusOrMinus(char c){
		if(!model.getStack().isEmpty()){
			while(!model.getStack().isEmpty() &&
					((char) model.getStack().peek() == '*' ||
					 (char) model.getStack().peek() == '/' ||
					 (char) model.getStack().peek() == '+' ||
					 (char) model.getStack().peek() == '-')){
				model.getQueue().add(model.getStack().pop().toString());
			}
		}
		model.getStack().push(c);
		return;
	}
	
	private void foundMultiplyOrDivide(char c){
		if(model.getStack().isEmpty()){
			model.getStack().push(c);
		}
		else {
			if((char) model.getStack().peek() == '+' || 
				(char) model.getStack().peek() == '-' ||
				(char) model.getStack().peek() == ')' ||
				(char) model.getStack().peek() == '('){
				model.getStack().push(c);
			}
			else {
				while(!model.getStack().isEmpty()){
					model.getQueue().add(model.getStack().pop().toString());
				}
				model.getStack().push(c);
			}
		}
		return;
	}
	
	private void foundRightBracket(char c){
		while(!model.getStack().isEmpty()){
			if((char) model.getStack().peek() != '('){
				model.getQueue().add(model.getStack().pop().toString());
			}
			else {
				model.getStack().pop();
				break;
			}
		}
		return;
	}
	
	private void foundLeftBracket(char c){
		model.getStack().push(c);
		return;
	}
	
	private int countCharsInInput(String input){
		int res = 0;
		for(char c: input.toCharArray()){
			if(Character.isAlphabetic(c)){
				res++;
			}
		}
		return res;
	}
	
	private void easyRes(char c) throws VariableNotFoundException {
		if(!model.getVariablesHashMap().containsKey(c)){
			throw new VariableNotFoundException();
		}
	}
	
	/* 
	 * Metoda do obliczania wyrazenia w input i wyswietlania go w polu tekstowym okna
	 * @param input wejsciowe wyrazenie, ktore obliczamy
	 */
	private void doWork(String input){
		char[] entry;
		boolean easyResult = false;
		ComplexNumber result = new ComplexNumber();
		try {
			checkInput(input);
		}
		catch(IOException e0){
			System.out.println(e0.toString());
			return;
		}
		catch(SpaceException e1){
			System.out.println(e1.toString());
			return;
		}
		catch(EmptyInputException ei){
			System.out.println(ei.toString());
			return;
		}
		
		if(countCharsInInput(input) > 1){
			model.getVariableFormules().add(input);
		}
		
		entry = input.toCharArray();
		if(entry[0] == 'q'){
			return;
		}
		if(input.length() == 1){
			try {
				easyRes(entry[0]);
			}
			catch(VariableNotFoundException vnf){
				System.out.println(vnf.toString());
				return;
			}
			result = model.getVariablesHashMap().get(entry[0]);
			easyResult = true;
		}
		if (!easyResult) {
			try {
				parse(input);
			} catch (VariableNotFoundException ve) {
				System.out.println(ve.toString());
				return;
			}
			try {
				result = calculate();
			} catch (OperationException e2) {
				System.out.println(e2.toString());
				return;
			}
			boolean wasRemoved = false;
			if(variableValueChanged){
				for(String s: model.getVariableFormules()){
					if(!s.equals(input)){
						for(int i = 0; i < model.getVariableFormules().size(); i++){
							if(model.getVariableFormules().toArray()[i].toString().toCharArray()[0] == changedVariableName){
								model.getVariableFormules().remove(i);
								wasRemoved = true;
							}
						}
					}
					if(!wasRemoved) {
						try {
							parse(s);
						} catch (VariableNotFoundException ve) {
							System.out.println(ve.toString());
						}
						try {
							result = calculate();
							System.out.println("usuwam klucz " + s.toCharArray()[0]
									+ ", by wrzucic go z powrotem z wartoscia " + result.toString());
							model.getVariablesHashMap().remove(s.toCharArray()[0]);
							model.getVariablesHashMap().put(s.toCharArray()[0], result);
							//System.out.println("Wynik = " + result);
						} catch (OperationException e2) {
							System.out.println(e2.toString());
							return;
						} 
					}
					else {
						break;
					}
				}
				variableValueChanged = false;
			}
		}
		if (variableDefinition) {
			variableDefinition = false;
			getText().setText("Dodano zmienna " + variableName);
		} else {
			getText().setText(result.toString());
		} 
		easyResult = false;
		return;
	}
	
	/*
	 * Zapewnia dzialanie programu w konsoli. Dziala w petli konczacej sie wprowadzeniem klawisza 'q'. 
	 * Algorytm:
	 * 1. Wczytaj wejscie
	 * 2. Sprawdz, czy jest prawidlowe (tzn. czy nie zawiera bialych znakow itp.)
	 * 3. Sparsuj je do modelu
	 * 4. Oblicz wyrażenie wedlug danych z modelu
	 * 5. Jeśli byla definicja nowej zmiennej lub ktoras z zadeklarowanych zmiennych zostala zmieniona, to wykonaj potrzebne obliczenia, 
	 * 	  aby zaktualizowac jej wartosc w modelu.
	 * 6. Wroc do pkt 1
	 */
	public void start() {
		/* Stworz widok */
		createRequiredListeners();
		view.getShell().open();
		view.getShell().layout();
		while (!view.getShell().isDisposed()) {
			if (!view.getDisplay().readAndDispatch()) {
				view.getDisplay().sleep();
			}
		}

		/*Scanner sc = new Scanner(System.in);
		System.out.println("Witamy w kalkulatorze liczb zepsolonych. Milego uzytkowania!");
		String input = sc.nextLine();
		char[] entry;
		ComplexNumber result = new ComplexNumber(); 
		boolean easyResult = false;
		
		while(true){
			// Sprawdz wejscie
			try {
				checkInput(input);
			}
			catch(IOException e0){
				System.out.println(e0.toString());
				input = sc.nextLine();
				continue;
			}
			catch(SpaceException e1){
				System.out.println(e1.toString());
				input = sc.nextLine();
				continue;
			}
			catch(EmptyInputException ei){
				System.out.println(ei.toString());
				input = sc.nextLine();
				continue;
			}
			
			if(countCharsInInput(input) > 1){
				model.getVariableFormules().add(input);
			}
			
			entry = input.toCharArray();
			if(entry[0] == 'q'){
				break;
			}
			if(input.length() == 1){
				try {
					easyRes(entry[0]);
				}
				catch(VariableNotFoundException vnf){
					System.out.println(vnf.toString());
					input = sc.nextLine();
					continue;
				}
				result = model.getVariablesHashMap().get(entry[0]);
				easyResult = true;
			}
			if (!easyResult) {
				try {
					parse(input);
				} catch (VariableNotFoundException ve) {
					System.out.println(ve.toString());
					input = sc.nextLine();
					continue;
				}
				try {
					result = calculate();
				} catch (OperationException e2) {
					System.out.println(e2.toString());
					input = sc.nextLine();
					continue;
				}
				boolean wasRemoved = false;
				if(variableValueChanged){
					for(String s: model.getVariableFormules()){
						if(!s.equals(input)){
							for(int i = 0; i < model.getVariableFormules().size(); i++){
								if(model.getVariableFormules().toArray()[i].toString().toCharArray()[0] == changedVariableName){
									model.getVariableFormules().remove(i);
									wasRemoved = true;
								}
							}
						}
						if(!wasRemoved) {
							try {
								parse(s);
							} catch (VariableNotFoundException ve) {
								System.out.println(ve.toString());
							}
							try {
								result = calculate();
								System.out.println("usuwam klucz " + s.toCharArray()[0]
										+ ", by wrzucic go z powrotem z wartoscia " + result.toString());
								model.getVariablesHashMap().remove(s.toCharArray()[0]);
								model.getVariablesHashMap().put(s.toCharArray()[0], result);
								//System.out.println("Wynik = " + result);
							} catch (OperationException e2) {
								System.out.println(e2.toString());
								input = sc.nextLine();
								continue;
							} 
						}
						else {
							break;
						}
					}
					variableValueChanged = false;
				}
			}
			if (variableDefinition) {
				variableDefinition = false;
			} else {
				System.out.println("=" + result);
			} 
			
			/*System.out.println("Zawartosc formul:");
			for(int i = 0; i < model.getVariableFormules().size(); i++){
				System.out.println(model.getVariableFormules().toArray()[i]);
			}
			
			System.out.println("Zawartosc mapy:");
			for (Entry<Character, ComplexNumber> entry1 : model.getVariablesHashMap().entrySet()) {
			    System.out.println(entry1.getKey() + " : " + entry1.getValue());
			}
			System.out.println("------------------------------");*/
			
			/*easyResult = false;
			input = sc.nextLine();
		}
		/*LinkedList<String> queue = model.getQueue();
		while(!queue.isEmpty()){
			System.out.print(queue.poll() + " ");
		}
		model.getStack().clear();
		model.getQueue().clear();*/
		/*System.out.println(new ComplexNumber(2, -3).toString());
		System.out.println(ComplexNumber.valueOf("2+3i").toString());*/
		
		/*System.out.println("Koniec");*/
	}
	
	/* Delegaci: */
	private Text getText(){
		return view.getText();
	}
	
	private Button getButton1() {
		return view.getButton1();
	}

	private Button getButton2() {
		return view.getButton2();
	}

	private Button getButton3() {
		return view.getButton3();
	}

	private Button getButton4() {
		return view.getButton4();
	}

	private Button getButton5() {
		return view.getButton5();
	}

	private Button getButton6() {
		return view.getButton6();
	}

	private Button getButton7() {
		return view.getButton7();
	}

	private Button getButton8() {
		return view.getButton8();
	}

	private Button getButton9() {
		return view.getButton9();
	}

	private Button getButton0() {
		return view.getButton0();
	}

	private Button getButtonDot() {
		return view.getButtonDot();
	}

	private Button getButtonPls() {
		return view.getButtonPls();
	}

	private Button getButtonSub() {
		return view.getButtonSub();
	}

	private Button getButtonMul() {
		return view.getButtonMul();
	}

	private Button getButtonDiv() {
		return view.getButtonDiv();
	}

	private Button getButtonEqa() {
		return view.getButtonEqa();
	}

	private Button getButtonEnt() {
		return view.getButtonEnt();
	}

	private Button getButtonI() {
		return view.getButtonI();
	}
	
	private Button getButtonRes() {
		return view.getButtonRes();
	}
	
	/*
	 * Stworz potrzebnych "nasluchiwaczy" przyciskow 
	 */
	private void createRequiredListeners(){
		view.open();

		getButton1().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("1");
				}				
			}		
		});
		getButton2().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("2");
				}			
			}			
		});
		getButton3().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("3");
				}				
			}			
		});
		getButton4().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("4");
				}				
			}			
		});
		getButton5().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("5");
				}				
			}		
		});
		getButton6().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("6");
				}				
			}		
		});
		getButton7().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("7");
				}				
			}			
		});
		getButton8().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("8");
				}				
			}			
		});
		getButton9().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("9");
				}				
			}			
		});
		getButton0().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("0");
				}				
			}			
		});
		getButtonDot().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert(".");
				}				
			}			
		});
		getButtonPls().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("+");
				}				
			}		
		});
		getButtonSub().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("-");
				}			
			}			
		});
		getButtonMul().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("*");
				}				
			}		
		});
		getButtonDiv().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("/");
				}		
			}	
		});
		getButtonEqa().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("=");
				}
			}
		});
		getButtonI().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					getText().insert("i");
				}	
			}
		});
		getButtonEnt().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e) {
				switch(e.type){
				case SWT.Selection:
					//System.out.println("Licz!");
					doWork(getText().getText());
				}			
			}
		});
		getText().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e){
				switch(e.type){
				case SWT.Selection:
					System.out.println("ENTER");
				}
			}
		});
		getButtonRes().addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event e){
				switch(e.type){
				case SWT.Selection:
					getText().setText("");
				}
			}
		});
	}
}
