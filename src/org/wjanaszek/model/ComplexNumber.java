package org.wjanaszek.model;

import java.lang.Math.*;

public class ComplexNumber {
	private double re;		// real part
	private double im;		// imaginary part
	
	public ComplexNumber(double re, double im){
		this.re = re;
		this.im = im;
	}
	
	public ComplexNumber(){
		this.re = 0.0D;
		this.im = 0.0D;
	}

	public double getRe() {
		return re;
	}

	public void setRe(double re) {
		this.re = re;
	}

	public double getIm() {
		return im;
	}

	public void setIm(double im) {
		this.im = im;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String result = "";
		boolean wasRe = false;
		if(this.re > 0){
			sb.append(this.re);
			wasRe = true;
		}
		else if(this.re < 0){
			sb.append(this.re);
			wasRe = true;
		}
		
		if(this.im > 0 && wasRe){
			sb.append("+");
			sb.append(this.im);
			sb.append("i");
		}
		else if(this.im < 0){
			sb.append(this.im);
			sb.append("i");
		}
		else if(this.im > 0){
			sb.append(this.im);
			sb.append("i");
		}
		
		if(sb.length() == 0){
			result = "0";
		}
		else {
			result = sb.toString();
		}
		return result;
	}
	
	public void add(ComplexNumber w){
		this.re += w.getRe();
		this.im += w.getIm();
	}
	
	public void substract(ComplexNumber w){
		this.re -= w.getRe();
		this.im -= w.getIm();
	}
	
	public void multiply(ComplexNumber w){
		double re = this.re;
		double im = this.im;
		this.re = this.re * w.getRe() - this.im * w.getIm();
		this.im = re * w.getIm() + im * w.getRe();
	}
	
	public void divide(ComplexNumber w){
		double re = this.re;
		double im = this.im;
		this.re = (this.re * w.getRe() + this.im * w.getIm()) / (Math.pow(w.getRe(), 2.0D) + Math.pow(w.getIm(), 2));
		this.im = (im * w.getRe() - re * w.getIm()) / (Math.pow(w.getRe(), 2.0D) + Math.pow(w.getIm(), 2));
	}
	
	public double getAbsolute(){
		double res = this.re * this.re + this.im * this.im;
		res = Math.sqrt(res);
		return res;
	}
	
	public double getArgumnet() {
		double abs, res = 0, tmp;
		abs = this.getAbsolute();
		if(this.im >= 0 && abs != 0){
			tmp = this.re / abs;
			tmp = Math.toRadians(tmp);
			res = Math.acos(tmp);
		}
		if(this.im < 0){
			tmp = this.re / abs;
			tmp = Math.toRadians(tmp);
			res = Math.acos(tmp) * (-1);
		}
		return res;
	}
	
	public static ComplexNumber valueOf(String input){
		StringBuilder value1 = new StringBuilder();
		ComplexNumber result = new ComplexNumber();
		char[] inpt = input.toCharArray();
		boolean minus = false;
		String tmpVal;
		char c;
		if(inpt.length > 0){
			if(inpt[0] == '-'){
				minus = true;
			}
		}
		for(int i = 0; i < inpt.length; i++){
			c = inpt[i];
			if(Character.isDigit(c)){
				value1.append(c);
				if(i == inpt.length - 1){
					result.setRe(Double.valueOf(value1.toString()));
				}
			}
			if((c == '+' || c == '-') && i != 0){
				tmpVal = value1.toString();
				if(tmpVal.length() > 0){
					if(minus){
						result.setRe((-1.0D)*Double.valueOf(tmpVal));
					}
					else {
						result.setRe(Double.valueOf(tmpVal));
					}
				}
				if(c == '-'){
					minus = true;
				}
				value1 = new StringBuilder();
			}
			if(c == 'i'){
				tmpVal = value1.toString();
				if(tmpVal.length() > 0){
					if(minus){
						result.setIm((-1.0D)*Double.valueOf(tmpVal));
					}
					else {
						result.setIm(Double.valueOf(tmpVal));
					}
				}
			}
			if(c == '.'){
				value1.append(c);
			}
		}
		return result;
	}
}
