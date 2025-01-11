package com.personal.expensetracker.users;
import com.personal.expensetracker.utilities.APIResponse;
import com.personal.expensetracker.utilities.JWTUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/validate")
    public ResponseEntity<APIResponse<String>> validateUser(@RequestParam String email, @RequestParam String password) {
        logger.info("Validating User with email {}", email);
        if(email == null || password == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Invalid credentials", "Please enter email and password"));
        try {
            logger.debug("Retrieving user information for email: {}", email);
            User user = userService.getUser(email)
                    .orElseThrow(() -> {
                        logger.warn("User not found with email: {}", email);
                        return new RuntimeException("User not found with ID: " + email);
                    });
            if(Objects.equals(password, user.getPassword())) {
                logger.debug("Password validation successful for user: {}", email);
                String token = jwtUtil.generateToken(email);
                logger.info("Login successful for user: {}", email);
                return ResponseEntity.ok(APIResponse.success("Logged in successfully", token));
            }
            logger.warn("Invalid password attempt for user: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Invalid credentials", "Incorrect password"));
        } catch (Exception e) {
            logger.error("Authentication error for user: {} - Error: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Invalid credentials", e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<String>> createUser(@Valid @RequestBody User user){
        logger.info("Attempting to create new user with email: {}", user.getEmail());
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

    @DeleteMapping("/delete")
    public ResponseEntity<APIResponse<Void>> deleteUser(@RequestHeader("Authorization") String authHeader){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            if(email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Invalid credentials", "Please enter email"));
            if(!userService.deleteUser(email)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to delete user", "User not found with ID: " + email));
            }
            return ResponseEntity.ok(APIResponse.success("User Deleted Successfully", null));
        } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to delete user", e.getMessage()));
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to delete user", e.getMessage()));
        }
    }
}
