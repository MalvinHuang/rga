package com.rga.service;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;

import com.rga.exception.UserAlreadyExistException;
import com.rga.exception.UserNotFoundException;
import com.rga.exception.ValidationException;
import com.rga.model.User;

public class UserServiceTest {
	
	private UserService _service;
	private AtomicLong _counter = new AtomicLong();
	
	@Before
	public void setup() {
		_service = new UserService();
		_service.addUser(generateMockUser());
		_service.addUser(generateMockUser());
	}

	@Test
	public void testAddUser() {
		User user = generateMockUser();
		_service.addUser(user);
		assertTrue(_service.findUserById(user.getId()) != null);
	}
	
	@Test(expected=UserAlreadyExistException.class)
	public void testAddSameUserTwice() {
		User user = generateMockUser();
		_service.addUser(user);
		_service.addUser(user);
	}
	
	@Test(expected=ValidationException.class)
	public void testAddInvalidUser() {
		User user = new User();
		_service.addUser(user);
	}

	@Test
	public void testUpdateUser() {
		User user = generateMockUser();
		int oriSize = _service.getAllUsers().size();
		_service.addUser(user);
		assertTrue((_service.getAllUsers().size() - oriSize) == 1);
		user.setAddr("Addr");
		_service.updateUser(user);
		assertTrue(_service.getAllUsers().size() == (oriSize + 1));
		User newUser = _service.findUserById(user.getId());
		assertTrue(newUser.getAddr() != null && "Addr".equals(newUser.getAddr()));
	}
	
	@Test(expected=UserNotFoundException.class)
	public void testUpdateNonexistUser() {
		User user = generateMockUser();
		assertTrue(_service.findUserById(user.getId()) == null);
		_service.updateUser(user);
	}
	
	@Test(expected=ValidationException.class)
	public void testUpdateInvalidUser() {
		User user = generateMockUser();
		_service.addUser(user);
		user.setId(0);
		_service.updateUser(user);
	}

	@Test
	public void testDeleteUser() {
		List<User> users = _service.getAllUsers();
		for (User user : users)
			_service.deleteUser(user.getId());
		assertTrue(_service.getAllUsers().isEmpty());
	}
	
	@Test(expected=UserNotFoundException.class)
	public void testDeleteNonexistUser() {
		_service.deleteUser(Long.MAX_VALUE);
	}

	@Test(expected=UserNotFoundException.class)
	public void testFindNonexistUser() {
		_service.findUserById(Long.MAX_VALUE);
	}
	
	private User generateMockUser() {
		return generateMockUser(_counter.incrementAndGet());
	}
	
	private User generateMockUser(long id) {
		User result = new User();
		result.setId(id);
		return result;
	}

}
