package org.wjanaszek.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/*
 * Klasa reprezentujaca model z architektury MVC. Przechowuje definicje zmiennych powiazane z wartosciami w HashMapie, a oprocz tego formuly obliczania zmiennych zaleznych
 * od siebie w liscie.
 * 
 * @author Wojciech Janaszek
 * @category Model
 */

public class Model {
	private static HashMap<Character, ComplexNumber> variables;
	private Stack<Character> stack;
	private LinkedList<String> queue;
	private LinkedList<String> variableFormules;
	
	public Model(){
		variables = new HashMap<Character, ComplexNumber>();
		stack = new Stack<Character>();
		queue = new LinkedList<String>();
		variableFormules = new LinkedList<String>();
	}

	public static HashMap<Character, ComplexNumber> getVariablesHashMap() {
		return variables;
	}

	public static void setVariablesHashMap(HashMap<Character, ComplexNumber> variables) {
		Model.variables = variables;
	}

	public Stack getStack() {
		return stack;
	}

	public void setStack(Stack stack) {
		this.stack = stack;
	}

	public LinkedList getQueue() {
		return queue;
	}
	
	public void setQueue(LinkedList queue) {
		this.queue = queue;
	}
	
	public LinkedList<String> getVariableFormules() {
		return variableFormules;
	}

	public void setVariableFormules(LinkedList<String> variableFormules) {
		this.variableFormules = variableFormules;
	}
}
