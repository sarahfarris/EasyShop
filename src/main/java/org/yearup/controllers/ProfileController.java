package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

/**
 * Handles user privileges to view and update profile and return as HTTP response
 */

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {

    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    /**
     * Retrieves profile using user authentication
     * @param principal verifies user authentication
     * @return profile using user ID
     */

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Profile getProfile(Principal principal) {
        try {
            return profileDao.getByUserId(getUserId(principal));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /**
     * Updates user profile with user privileges
     * @param principal takes authenticated user and returns HTTP response to update profile
     * @param profileToUpdate response to update profile
     * @return returns updated profile
     */

    @PutMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Profile updateProfile(Principal principal, @RequestBody Profile profileToUpdate) {
        try {
            Profile profile = profileDao.updateProfile(getUserId(principal), profileToUpdate);
            if (profile == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
            }

            return profile;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }


    /**
     * used as a helper method to verify authentication
     * @param principal object to verify authentication
     * @return user ID which is used for authentication
     */

    private int getUserId(Principal principal) {
        // get the currently logged in username
        String userName = principal.getName();
        // find database user by userId
        User user = userDao.getByUserName(userName);
        return user.getId();
    }
}
