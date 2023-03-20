package com.http.demo;

import static org.hamcrest.CoreMatchers.containsString;
// import these static classes
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.http.demo.model.Author;
import com.http.demo.model.Book;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=HttpStatusCodeApplication.class)
@AutoConfigureMockMvc
@Transactional
public class HttpStatusCodeApplicationTests {
	
	/**
	 * Transactional annotation to isolate each test, 
	 * otherwise you will have failed tests by running them all together
	 */

	/**
	 * 3. Spring provides us an easy way to test our controller through MockMvc.
	 * 
	 * We just need to create a controller test class specifying AutoConfigureMockMvc 
	 * annotation to auto-configure it
	 */
	@Autowired
	MockMvc mockMvc;
	
	@SuppressWarnings("rawtypes")
	private HttpMessageConverter mappingJackson2HttpMessageConverter;
	
	@Test
	public void contextLoads() {
	}
	
	/**
	 * 
	 * helper method to marshal Java object into JSON.
	 * 
	 * @param o
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, 
        		mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
	
	@Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();
        Assert.assertNotNull("the JSON message converter must not be null", 
        		this.mappingJackson2HttpMessageConverter);
    }
	
	/**
	 * first test a valid book creation and verify the presence of the 
	 * header location and the correct returned status code: 
	 */
	@Test
	public void should_create_valid_book_And_return_valid_header_and_statusCode() throws Exception{
		Book book = new Book("123-1234567890","My new book","Publisher");
	    book.addAuthor(new Author("John","Doe"));
	    
	    RequestBuilder requestBuilder = post("/api/books").contentType(MediaType.APPLICATION_JSON).
	    		content(json(book));
	    
	    mockMvc.perform(requestBuilder)
	    .andExpect(status().isCreated())
	    .andExpect(header().string("Location", is("http://localhost/api/books/123-1234567890")))
	    .andExpect(content().string(""))
	    .andDo(MockMvcResultHandlers.print());
	    
	}
	
	@Test
	public void bad_request_should_return_valid_header_and_statusCode() throws Exception{
		Book book = new Book("","My new book","Publisher");
		
		RequestBuilder requestBuilder = post("/api/books").contentType(MediaType.APPLICATION_JSON).
				content(json(book));
		
		mockMvc.perform(requestBuilder)
		.andExpect(status().isBadRequest())
		.andExpect(content().string(""))
		.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void already_existing_isbn_should_return_conflict() throws Exception{
		Book book = new Book("978-0321356680","My new book","Publisher");
	    book.addAuthor(new Author("John","Doe"));
	    
	    RequestBuilder requestBuilder = post("/api/books").contentType(MediaType.APPLICATION_JSON).
	    		content(json(book));
	    
	    mockMvc.perform(requestBuilder)
	    .andExpect(status().isConflict())
	    .andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void valid_get_book_should_return_status_ok() throws Exception{
		
		RequestBuilder requestBuilder = get("/api/books/978-0321356680").contentType(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(requestBuilder)
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.title", is("Effective Java")))
        .andExpect(jsonPath("$.publisher", is("Addison Wesley")))
		.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void should_not_get_unknown_book_with_not_found_status() throws Exception {
	    mockMvc.perform(get("/api/books/000-1234567890").contentType(MediaType.APPLICATION_JSON))
	        .andExpect(status().isNotFound())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
	        .andExpect(jsonPath("$[0].logref", is("error")))
	        .andExpect(jsonPath("$[0].message", containsString("could not find book with ISBN: '000-1234567890'")))
	        .andDo(MockMvcResultHandlers.print());
	}

}

