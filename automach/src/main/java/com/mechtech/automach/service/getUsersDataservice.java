package com.mechtech.automach.service;

import com.mechtech.automach.entity.User;

public interface getUsersDataservice {
    User getUserData(String username, String password);
}
