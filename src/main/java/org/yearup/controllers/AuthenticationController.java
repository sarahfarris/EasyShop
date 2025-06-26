package org.yearup.controllers;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.authentication.LoginDto;
import org.yearup.models.authentication.LoginResponseDto;
import org.yearup.models.authentication.RegisterUserDto;
import org.yearup.models.User;
import org.yearup.security.jwt.JWTFilter;
import org.yearup.security.jwt.TokenProvider;


/**
Handles authentication and registration for log in
 */
@RestController
@CrossOrigin
@PreAuthorize("permitAll()")
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;
    private ProfileDao profileDao;


    /**
     * Injects required dependencies
     * @param tokenProvider generates JWT's for authorization
     * @param authenticationManagerBuilder constructor for authentication builder
     * @param userDao manages user data access
     * @param profileDao manages user privileges
     */
    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao, ProfileDao profileDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    /**
     * Handles login and authenticates user/admin based on credentials
     * Generates JWT for user/admin depending on credentials
     * @param loginDto holds the user's credentials
     * @return access to website
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND) when user doesn't exist
     * @throws ResponseStatusException (HttpStatus.INTERNAL_SERVER_ERROR) for any unexpected errors
     */

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) {

        //creates authentication token that depends on username/password and user/admin
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        //authenticate the user, if fails then throw exception
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //Set authentication object, and allows user to navigate website with credentials (allowing user privileges)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Generates JWT for user
        String jwt = tokenProvider.createToken(authentication, false);

        try
        {
            User user = userDao.getByUserName(loginDto.getUsername());

            if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
            return new ResponseEntity<>(new LoginResponseDto(jwt, user), httpHeaders, HttpStatus.OK);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /**
     * Handles new user registration
     * @param newUser object containing user's credentials
     * @return newly created user object
     * @throws ResponseStatusException if user exists
     * @throws ResponseStatusException to handle all other errors
     */

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserDto newUser) {

        try
        {
            boolean exists = userDao.exists(newUser.getUsername());
            if (exists)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Exists.");
            }

            // create user
            User user = userDao.create(new User(0, newUser.getUsername(), newUser.getPassword(), newUser.getRole()));

            // create profile
            Profile profile = new Profile();
            profile.setUserId(user.getId());

            profileDao.create(profile);

            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

}

