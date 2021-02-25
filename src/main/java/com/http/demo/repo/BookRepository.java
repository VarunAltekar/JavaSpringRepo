package com.http.demo.repo;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.http.demo.model.Book;

public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
	/**
	 * 
	 * @param isbn
	 * @return Optional to indicate that you dont necessarily have a book.
	 */
	Optional<Book> findByIsbn(String isbn);
	

}
