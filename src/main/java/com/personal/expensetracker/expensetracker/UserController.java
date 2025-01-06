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


    private final UserService userService;
    private final JWTUtil jwtUtil;

    public UserController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/validate")
    public ResponseEntity<APIResponse<String>> validateUser(@RequestParam String email, @RequestParam String password) {
        if(email == null || password == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Invalid credentials", "Please enter email and password"));
        try {
            User user = userService.getUser(email)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + email));
            if(Objects.equals(password, user.getPassword())) {
                String token = jwtUtil.generateToken(email);
                return ResponseEntity.ok(APIResponse.success("Logged in successfully", token));
            }
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
            String token = jwtUtil.generateToken(createdUser.getEmail());
            return ResponseEntity.ok(APIResponse.success("User created successfully with name " + createdUser.getName(), token));
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

    @DeleteMapping("/delete{email}")
    public ResponseEntity<APIResponse<Void>> deleteUser(@PathVariable String email){
        if(email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Invalid credentials", "Please enter email"));
        if(!userService.deleteUser(email)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to delete user", "User not found with ID: " + email));
        }
        return ResponseEntity.ok(APIResponse.success("User Deleted Successfully", null));
    }
}
