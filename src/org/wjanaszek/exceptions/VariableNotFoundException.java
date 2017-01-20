package org.wjanaszek.exceptions;

public class VariableNotFoundException extends Exception {
	@Override
	public String toString(){
		return "Nie znaleziono zmiennej - moze nie zostala zainicjalizowana?";
	}
}
