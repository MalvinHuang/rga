package com.rga;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.rga.model.User;
import com.rga.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class UserControllerTest {

	private MockMvc _mockMvc;
	@Autowired
	private WebApplicationContext _webContext;
	@Autowired
	private UserService _userService;
	private MediaType _contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8"));
	private HttpMessageConverter _mappingJackson2HttpMessageConverter;
	private AtomicLong _counter = new AtomicLong();

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		_mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

		assertNotNull("the JSON message converter must not be null", _mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setUp() throws Exception {
		_mockMvc = MockMvcBuilders.webAppContextSetup(_webContext).build();
		_userService.reset();
	}

	@Test
	public void testGetAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUser() throws Exception {
		User u = generateMockUser();
		_mockMvc.perform(get("/users/{userId}", u.getId())).andExpect(status().isNotFound());
		_mockMvc.perform(put("/users/{userId}", u.getId()).contentType(_contentType).content(json(u))).andExpect(status().is2xxSuccessful());
		_mockMvc.perform(get("/users/{userId}", u.getId())).andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.id", is(u.getId())));
	}

	@Test
	public void testAddUser() throws Exception {
		User u = generateMockUser();
		_mockMvc.perform(put("/users/{userId}", u.getId()).contentType(_contentType).content(json(u))).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void testAddSameUserTwice() throws Exception {
		User u = generateMockUser();
		_mockMvc.perform(put("/users/{userId}", u.getId()).contentType(_contentType).content(json(u))).andExpect(status().is2xxSuccessful());
		_mockMvc.perform(put("/users/{userId}", u.getId()).contentType(_contentType).content(json(u))).andExpect(status().is4xxClientError());
	}

	@Test
	public void testDeleteUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNextId() {
		fail("Not yet implemented");
	}

	private User generateMockUser() {
		return generateMockUser(_counter.incrementAndGet());
	}

	private User generateMockUser(long id) {
		User result = new User();
		result.setId(id);
		return result;
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		_mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}

}
