package org.wjanaszek.exceptions;

public class EmptyInputException extends Exception {
	@Override
	public String toString(){
		return "Podano puste wejscie. Sprobuj ponownie";
	}
}
