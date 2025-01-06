package com.personal.expensetracker.expensetracker;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/validate")
    public ResponseEntity<APIResponse<Void>> validateUser(@RequestParam String email, @RequestParam String password) {
        if(email == null || password == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Invalid credentials", "Please enter email and password"));
        try {
            User user = userService.getUser(email)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + email));
            if(Objects.equals(password, user.getPassword()))
             return ResponseEntity.ok(APIResponse.success("Logged in successfully", null));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Invalid credentials", "Incorrect password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Invalid credentials", e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<String>> createUser(@Valid @RequestBody User user){
        if(userService.userExists(user.getEmail())) return ResponseEntity.status(HttpStatus.CONFLICT).body(APIResponse.error("Failed to create user", "User with this email already exits"));
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(APIResponse.success("User created successfully with name " + createdUser.getName(), createdUser.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to create user", e.getMessage()));
        }
    }

    @PutMapping("/changepassword")
    public ResponseEntity<APIResponse<Void>> changePassword(@RequestParam String email, @RequestParam String password){
        if(email == null || password == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Invalid credentials", "Please enter email and password"));
        try {
            User existingUser = userService.getUser(email)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + email));
            User updatedUser = userService.changePassword(existingUser, password);
            return ResponseEntity.ok(APIResponse.success("Password updated successfully for user " + updatedUser.getName(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to fetch user", e.getMessage()));
        }
    }

    @DeleteMapping("/user{email}")
    public ResponseEntity<APIResponse<Void>> deleteUser(@PathVariable String email){
        if(email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Invalid credentials", "Please enter email"));
        if(!userService.deleteUser(email)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to delete user", "User not found with ID: " + email));
        }
        return ResponseEntity.ok(APIResponse.success("User Deleted Successfully", null));
    }
}
