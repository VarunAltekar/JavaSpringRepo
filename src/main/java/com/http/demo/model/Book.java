package com.http.demo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
@Table(uniqueConstraints = { @UniqueConstraint(name="uk_book_isbn",columnNames="isbn")})
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String isbn;
	@NotBlank
	private String title;
	private String description;
	@ElementCollection
	//@NotEmpty(message="Author cannot be empty")
	private Set<Author> authors;
	@NotBlank
	private String publisher;

	// ----------------
	// - CONSTRUCTORS -
	// ----------------
	private Book() {
		// Default constructor for Jackson
	}
	public Book(String isbn, String title, Set<Author> authors, String publisher) {
		this.isbn = isbn;
		this.title = title;
		this.authors = authors;
		this.publisher = publisher;
	}
	public Book(String isbn, String title, String publisher) {
		this(isbn, title, new HashSet<>(), publisher);
	}
	// -----------
	// - METHODS -
	// -----------
	public void addAuthor(Author author) {
		this.authors.add(author);
	}


}
