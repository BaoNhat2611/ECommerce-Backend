package com.nhat.service;

import com.nhat.exception.UserException;
import com.nhat.model.User;

public interface UserService {
    public User findUserById(Long userId) throws UserException;

    public User findUserProfileByJwt(String jwt) throws UserException;

}
