-- Debug script to check user table contents
USE techData;

-- Check table structure
DESCRIBE Users;

-- Check all users in table
SELECT owncode, username, password, mobileno, expirydate FROM Users;

-- Check for specific user (replace 'testuser' with actual username)
SELECT * FROM Users WHERE username = 'testuser';

-- Check for case variations
SELECT * FROM Users WHERE LOWER(username) = LOWER('testuser');

-- Check if passwords match exactly
SELECT username, password, LENGTH(password) as password_length FROM Users;

-- Check for any hidden characters or spaces
SELECT username, 
       CONCAT('[', username, ']') as username_with_brackets,
       CONCAT('[', password, ']') as password_with_brackets
FROM Users;
