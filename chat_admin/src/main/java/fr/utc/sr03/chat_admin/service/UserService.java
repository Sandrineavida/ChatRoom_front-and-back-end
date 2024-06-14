package fr.utc.sr03.chat_admin.service;

import fr.utc.sr03.chat_admin.dto.UserDTO;
import fr.utc.sr03.chat_admin.model.User;
import fr.utc.sr03.chat_admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private Map<String, Integer> loginAttempts = new HashMap<>();
    private static final int MAX_ATTEMPTS = 3;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public boolean isMailExists(String mail) {
        return userRepository.existsByMail(mail);
    }

    @Transactional
    public void deleteUser(String userMail) {
        if (userRepository.existsByMail(userMail)) {
            userRepository.deleteByMail(userMail);
        } else {
            throw new RuntimeException("User not found with email " + userMail);
        }
    }

    public User authenticate(String email, String password) {
        if (isLockedOut(email)) {
            return null;
        }

        User user = userRepository.findByMail(email);
        if (user != null && user.getPassword().equals(password)) {
            resetLoginAttempts(email);
            return user;
        } else {
            incrementLoginAttempts(email);
            return null;
        }
    }

    private void incrementLoginAttempts(String email) {
        int attempts = loginAttempts.getOrDefault(email, 0);
        attempts++;
        loginAttempts.put(email, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            lockUserAccount(email); // Lock the user account
        }
    }

    private void resetLoginAttempts(String email) {
        loginAttempts.remove(email);
    }

    private boolean isLockedOut(String email) {
        User user = userRepository.findByMail(email);
        return user != null && user.isLocked();
    }

    public int getRemainingAttempts(String email) {
        User user = userRepository.findByMail(email);
        if (user.isLocked()) return 0;
        return MAX_ATTEMPTS - loginAttempts.getOrDefault(email, 0);
    }

    public boolean validatePassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUpper = true;
            } else if (Character.isLowerCase(ch)) {
                hasLower = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(ch)) {
                hasSpecial = true;
            }
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public void lockUserAccount(String email) {
        User user = userRepository.findByMail(email);
        if (user != null) {
            user.setLocked(true);
            userRepository.save(user);
        }
    }

    public void unlockUserAccount(String email) {
        User user = userRepository.findByMail(email);
        if (user != null) {
            user.setLocked(false);
            userRepository.save(user);
            resetLoginAttempts(email); // Reset login attempts when unlocking the account
        }
    }

    public void toggleUserLock(String email) {
        User user = userRepository.findByMail(email);
        if (user != null) {
            if (user.isLocked()) {
                unlockUserAccount(email);
            } else {
                lockUserAccount(email);
            }
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByMail(String mail) {
        return userRepository.findByMail(mail);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }


    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setMail(user.getMail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAdmin(user.getAdmin());
        return dto;
    }

}
