package com.motive.rest.Auth;

import org.springframework.stereotype.Service;

import com.motive.rest.Util.ServiceInterface;
import com.motive.rest.dto.RegisterUser;
import com.motive.rest.user.User;
import com.motive.rest.user.UserRepo;

@Service
public class AuthService implements ServiceInterface<User>{
    
    UserRepo repo;
    
    public boolean createUser(RegisterUser user){
        return true;
    }

    @Override
    public User save(User object) {
        return null;
    }

    @Override
    public boolean delete(User object) {
        return false;
    }

    @Override
    public boolean findById(User id) {
        return false;
    }

    
}
