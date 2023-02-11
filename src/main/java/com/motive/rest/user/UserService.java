package com.motive.rest.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.motive.rest.Util.ServiceInterface;
import com.motive.rest.exceptions.EntityNotFound;

@Service
public class UserService implements ServiceInterface<User>{

    public enum REQUEST_RESPONSE {
        ACCEPT, REJECT, CANCEL, REMOVE_FRIEND
    }

    @Value("${JWT_SIGNATURE}")
    String JWTSignature;

    @Autowired
    UserRepo repo;

    public User findByUsername(String username) throws EntityNotFound{
        Optional<User> user = repo.findByUsername(username);

        if (user.isEmpty()) {
            throw new EntityNotFound("User not found");
        }

        return user.get();
    }

    public boolean usernameExists(String username) {
        return  repo.findByUsername(username).isPresent();
    }


    public  List<User>  findByUsernameContaining(String username) throws EntityNotFound{
        List<User> user = repo.findByUsernameContaining(username);

        if (user==null) {
            throw new EntityNotFound("User not found");
        }

        return user;
    }

    @Override
    public User save(User object) {
        return repo.save(object);
    }

    @Override
    public void delete(User object) {
       repo.delete(object);
        
    }

    @Override
    public User findById(UUID id) {
        return repo.findById(id).orElseThrow();
    }
   
}
