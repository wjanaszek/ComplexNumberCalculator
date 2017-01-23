package org.wjanaszek.controller;

import org.wjanaszek.exceptions.OperationException;
import org.wjanaszek.exceptions.VariableNotFoundException;
import org.wjanaszek.model.ComplexNumber;

/*
 * Interfejs stanowiacy zarys dzialania Odwroconej Notacji Polskiej sluzacej do parsowania wejscia w struktury danych jak kolejka, stos i obliczania wartosci
 * wyrazenia na podstawie tych struktur danych.
 * 
 * @author Wojciech Janaszek
 * @category Controller
 */
public interface ReversePolishNotation {
	public void parse(String input) throws VariableNotFoundException;
	public ComplexNumber calculate() throws OperationException;
}
