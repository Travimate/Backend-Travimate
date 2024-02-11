package id.synrgy.travimate.service;

import id.synrgy.travimate.model.Users;

public interface UserService {

    Users findByUsername(String username);
}
