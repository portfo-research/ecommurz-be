package com.github.portforesearch.ecommurzbe.service;

/**
 * User Role Service
 */
public interface UserRoleService {
    /**
     * Add other role to user
     * @param username
     * @param roleName
     */
    void addRoleToUser(String username,String roleName);}
