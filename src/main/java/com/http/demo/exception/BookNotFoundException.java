package com.http.demo.exception;

public class BookNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BookNotFoundException(String isbn){
		 super("could not find book with ISBN: '" + isbn + "'");
	}
}
