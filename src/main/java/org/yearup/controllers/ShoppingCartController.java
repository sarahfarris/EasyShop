package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

/**
 * Handles shopping cart privileges dependent on user authentication and returns as HTTP response
 */

@RestController
@RequestMapping("cart")
@CrossOrigin
public class ShoppingCartController
{
    // a shopping cart requires
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    /**
     * Get cart dependent on user authentication
     * @param principal identifies user authentication
     * @return cart using dao
     */

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            // use the shoppingcartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(getUserId(principal));
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /**
     * Adds product to shopping cart with user authentication
     * @param id identifies the product being added bd ID
     * @param principal verifies user authentication
     * @return cart object
     */

    // https://localhost:8080/cart/products/15 (15 is the productId to be added)
    @PostMapping("/products/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ShoppingCart addProduct(@PathVariable int id, Principal principal)
    {
        int userId = getUserId(principal);
        shoppingCartDao.addOrUpdate(userId, id);
        return getCart(principal);
    }



    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ShoppingCart updateQuantity(@PathVariable int id, Principal principal, @RequestBody ShoppingCartItem item)
    {
        int userId = getUserId(principal);
        shoppingCartDao.updateQuantity(userId, id, item.getQuantity());
        return getCart(principal);
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public void deleteCart(Principal principal)
    {
        shoppingCartDao.emptyCart(getUserId(principal));
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
