package com.rga;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rga.model.User;
import com.rga.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private static final Logger _log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService _service;

	@RequestMapping(method = RequestMethod.GET)
	public List<User> getAll() {
		return _service.getAllUsers();
	}
	
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public User getUser(@PathVariable long userId) {
		return _service.findUserById(userId);
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
	public ResponseEntity<?> addUser(@PathVariable long userId, @RequestBody User user) {
		_log.debug("adding new user: {}", user);
		user.setId(userId);
		_service.addUser(user);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable long userId) {
		_service.deleteUser(userId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.POST)
	public ResponseEntity<?>  updateUser(@PathVariable long userId, @RequestBody User user) {
		user.setId(userId);
		_service.updateUser(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping("/new-id")
	public long getNextId() {
		_log.debug("returning new id");
		return _service.getNextId();
	}
	
}
