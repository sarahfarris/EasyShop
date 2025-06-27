![Easy Shop Banner small](https://github.com/user-attachments/assets/58c496b9-f881-4e00-9ddd-9e136cb92367)


# Table of Contents
1. How to Run
2. Squashed Bugs
3. New Features
4. Goals

## How to Run
1. Download all files: database, src, mvn files, and pom.xml
2. Extract all files
3. Open src folder in IntelliJ through New Project from Existing Sources
4. Navigate to the Database folder where you will find create_database.sql
5. Copy entire file, paste into a new query in MySQL Workbench and run query to create database
6. Open a new project in IntelliJ through existing sources and open capstone-client-web-application
7. Now when you run the EasyShop application from the first project created, you will be able to navigate to the EasyShop website


## Squashed Bugs 🪲
* Update products would create a new product
  * Fixed in Products Controller -> changed .create() to .update()

  ![image](https://github.com/user-attachments/assets/29dc0aa8-1047-4fd7-9293-c2f40323167d)

*  Deleted duplicate products directly from SQL database

     ![image](https://github.com/user-attachments/assets/c14f0dde-8914-4abf-9454-931344ea974a)

   
* Search functions working as intended
    * Switched less than/equal to -> greater than/equal to

  ![image](https://github.com/user-attachments/assets/ca014669-376d-43ac-9c50-284f3214ef08)

## New Features 🧩
* Allow users to add to cart

  ![Easy_Shop_Screenshot](https://github.com/user-attachments/assets/75bc5d73-290c-4914-ae31-1e9e1aeffdcd)


* Search products by category

  ![image](https://github.com/user-attachments/assets/fc36d51a-7e90-4db9-ac97-183252102d75)

* Create Profile

    ![image](https://github.com/user-attachments/assets/743d6e34-89b0-46c7-9ec5-2858a29ab317)

## Goals
* Ability to purchase/pay with gift card
* Save and choose payment method
* Save multiple addresses
* Recommended products
