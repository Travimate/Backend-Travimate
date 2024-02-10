package id.synrgy.travimate.security.service;

import id.synrgy.travimate.exception.ResourceNotFoundException;
import id.synrgy.travimate.model.User;
import id.synrgy.travimate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(Integer.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException(userId));

        return UserDetailsImpl.build(user);
    }

}
