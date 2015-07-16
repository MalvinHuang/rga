package com.rga.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rga.exception.UserAlreadyExistException;
import com.rga.exception.UserNotFoundException;
import com.rga.exception.ValidationException;
import com.rga.model.User;

@Service
public class UserService {

	private static final Logger _log = LoggerFactory.getLogger(UserService.class);
	
	private static Map<Long, User> _userMap = new TreeMap<Long, User>();
	private static AtomicLong _idCounter = new AtomicLong();
	
	public long getNextId() {
		return _idCounter.incrementAndGet();
	}
	
	public void addUser(User user) {
		validateUser(user);
		if (_userMap.containsValue(user))
			throw new UserAlreadyExistException(user.getId());

		_userMap.put(user.getId(), user);
	}
	
	public void updateUser(User user) {
		validateUser(user);
		if (!_userMap.containsValue(user))
			throw new UserNotFoundException(user.getId());
		if (user.getId() == 0) {
			_log.error("updated user id 0");
		}
		_userMap.put(user.getId(), user);
	}
	
	public void deleteUser(long id) {
		if (!_userMap.containsKey(id))
			throw new UserNotFoundException(id);
		
		_userMap.remove(id);
	}
	
	public User findUserById(long id) {
		User result = _userMap.get(id);
		if (result == null)
			throw new UserNotFoundException(id);
		return result;
	}
	
	public List<User> getAllUsers() {
		for(User u : _userMap.values()) {
			if (u.getId() == 0)
				_log.error("ERROR! {}", u);
		}
		return Collections.list(Collections.enumeration(_userMap.values()));
	}
	
	private void validateUser(User user) {
		if (user == null || user.getId() == 0)
			throw new ValidationException("Invalid User, null or missing ID.");
	}
	
	public void reset() {
		_userMap = new TreeMap<Long, User>();
		_idCounter = new AtomicLong();
	}
}
