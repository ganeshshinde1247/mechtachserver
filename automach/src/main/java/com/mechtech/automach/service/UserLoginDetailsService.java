package com.mechtech.automach.service;

import java.util.Map;

public interface UserLoginDetailsService {
    Map<String, Object> validateUserDetails(Map<String, Object> loginDetails);
}
