package id.synrgy.travimate.service;

import id.synrgy.travimate.exception.ResourceNotFoundException;
import id.synrgy.travimate.model.User;
import id.synrgy.travimate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Autowired
    UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException(username));
    }
}
