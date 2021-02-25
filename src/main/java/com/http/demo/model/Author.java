package com.http.demo.model;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter @Setter @ToString
public class Author {

	private String firstName;
    private String lastName;
    private Author() {
        // Default constructor for Jackson
    }
    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
   
   
}
