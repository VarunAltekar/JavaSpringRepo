package com.http.demo.exception;

/**
 * map exception class to specific status code
 * @author Admin
 *
 */
public class BookIsbnAlreadyExistException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BookIsbnAlreadyExistException(String isbn){
		 super("book already exists for ISBN: '" + isbn + "'");
	}
}
