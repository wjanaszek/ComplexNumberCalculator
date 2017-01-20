package org.wjanaszek.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

import org.wjanaszek.view.*;
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
			//System.out.println("tmp = " + tmp);
			char[] element = tmp.toCharArray();
			if(Character.isDigit(element[0])){
				stack.push(ComplexNumber.valueOf(tmp));
				//System.out.println("Dodaje do stosu: (" + tmp + "), " + ComplexNumber.valueOf(tmp).toString());
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
		if(variableDefinition){			// jesli byla definicja zmiennej, to dodaj ja do hash mapy zmiennych w modelu
			model.getVariablesHashMap().put(variableName, result);
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
		//System.out.println("isnumber");
		for(char c: input){
			//System.out.println(c);
			if(!Character.isDigit(c) && c != '\n' && c != '.' && c != 'i'){
				//System.out.println("c: " + c + "false");
				return false;
			}
		}
		return true;
	}
	
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
	
	public void start() {
		//view.main(null);
		Scanner sc = new Scanner(System.in);
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
				/*if (Character.isAlphabetic(entry[0])) {
					model.getVariableFormules().add(input);
					//System.out.println(model.getVariableFormules().size());
				}*/
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
				char[] tmpChar;
				// wersja teoretycznie lepsza
				/*if (variableValueChanged) {
					for (String s : model.getVariableFormules()) {
						tmpChar = s.toCharArray();
						for (int i = 0; i < tmpChar.length; i++) {
							if (tmpChar[i] == changedVariableName) {
								//System.out.println(s + "jest " + changedVariableName);
							}
						}
					}
				}*/
				if(variableValueChanged){
					for(String s: model.getVariableFormules()){
						try {
							parse(s);
						} catch (VariableNotFoundException ve) {
							System.out.println(ve.toString());
						}
						try {
							calculate();
						} catch (OperationException e2) {
							System.out.println(e2.toString());
							input = sc.nextLine();
							continue;
						}
					}
				}
			}
			if (variableDefinition) {
				variableDefinition = false;
			} else {
				System.out.println("=" + result);
			} 
			easyResult = false;
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
		
		System.out.println("Koniec");
	}
	
}
