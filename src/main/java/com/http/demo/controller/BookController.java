package com.http.demo.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.http.demo.exception.BookIsbnAlreadyExistException;
import com.http.demo.exception.BookNotFoundException;
import com.http.demo.model.Book;
import com.http.demo.repo.BookRepository;

@RestController
@RequestMapping(path="/api/books")
public class BookController {

	/**
	 * Logback is provided out of the box with Spring Boot when you use one of the 
	 * Spring Boot starter dependencies
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

	@Autowired
	private BookRepository bookRepository;

	@PostMapping
	public ResponseEntity<?> createBook(@Valid @RequestBody Book book, UriComponentsBuilder ucBuilder) {

		/**
		 * 1. Creating a book needs first to check a book is not already present in the database 
		 * with the same ISBN
		 * 
		 * If not, we can save the book and send an empty response with the 201 status, 
		 * else we need to inform the client a book is present:
		 */
		if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
			throw new BookIsbnAlreadyExistException(book.getIsbn()); 
		}
		LOGGER.info("saving book");
		bookRepository.save(book);

		HttpHeaders headers = new HttpHeaders();
		// uri templates & UCB to specify resource location
		/**
		 * 2. We need to send where the new book resource can be located, so the client is
		 *  able to parse the header response and make a new request to retrieve the book's data.
		 */
		headers.setLocation(ucBuilder.path("/api/books/{isbn}").buildAndExpand(book.getIsbn()).toUri());
		headers.setContentType(MediaType.APPLICATION_JSON);

		LOGGER.info("setting headers and sending response");

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	@GetMapping("/{isbn}")
	public ResponseEntity<Book> getBook(@PathVariable(name="isbn") String isbn){
		return bookRepository.findByIsbn(isbn).
				map(book -> new ResponseEntity<>(book, HttpStatus.OK)).
				orElseThrow(() -> new BookNotFoundException(isbn));

	}

	@PutMapping("/{isbn}")
	public ResponseEntity<Book> updateBook(@PathVariable(name="isbn") String isbn, @Valid @RequestBody Book book){
		return bookRepository.findByIsbn(isbn)
				.map(bookUpdated -> {
					bookUpdated.setIsbn(book.getIsbn());
					bookUpdated.setDescription(book.getDescription());
					bookUpdated.setPublisher(book.getPublisher());
					bookUpdated.setTitle(book.getTitle());

					bookUpdated.addAuthor(book.getAuthors().iterator().next());

					bookRepository.save(bookUpdated);

					return new ResponseEntity<Book>(bookUpdated, HttpStatus.OK);
				}).orElseThrow(() -> new BookNotFoundException(isbn));
	}

	/**
	 * 1. check if book exits for isbn, then delete
	 * 		else throw exception
	 * @param isbn
	 * @return
	 */
	@DeleteMapping("/{isbn}")
	public ResponseEntity<?> deleteBook(@PathVariable(name = "isbn") String isbn){
		return bookRepository.findByIsbn(isbn).
			map(bookDelete -> {
				bookRepository.delete(bookDelete);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			})
			.orElseThrow(() -> new BookNotFoundException("Wrong isbn"));
	}



}
