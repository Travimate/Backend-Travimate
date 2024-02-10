package id.synrgy.travimate.service;

import id.synrgy.travimate.model.User;

public interface UserService {

    User findByUsername(String username);
}
