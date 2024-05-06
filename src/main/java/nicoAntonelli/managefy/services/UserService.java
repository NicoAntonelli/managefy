package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public UserService(UserRepository userRepository, ErrorLogService errorLogService) {
        this.userRepository = userRepository;
        this.errorLogService = errorLogService;
    }

    public List<User> GetUsers() {
        return userRepository.findAll();
    }

    public Boolean ExistsUser(Long userID) {
        return userRepository.existsById(userID);
    }

    public User GetOneUser(Long userID) {
        try {
            Optional<User> user = userRepository.findById(userID);
            if (user.isEmpty()) {
                throw new IllegalStateException("Error at 'GetOneUser' - User with ID: " + userID + " doesn't exist");
            }

            return user.get();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public User GetOneUserByMail(String mail) {
        try {
            // Mail format validation (TO-DO: add regex)
            if (mail == null || mail.isBlank()) {
                throw new IllegalStateException("Error at 'GetOneUserByMail' - Mail bad formatted: " + mail);
            }

            Optional<User> user = userRepository.findByMail(mail);
            if (user.isEmpty()) {
                throw new IllegalStateException("Error at 'GetOneUserByMail' - User with mail: " + mail + " doesn't exist");
            }

            return user.get();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public User CreateUser(User user) {
        try {
            // Mail unique validation
            Optional<User> possibleUser = userRepository.findByMail(user.getMail());
            if (possibleUser.isPresent()) {
                throw new IllegalStateException("Error at 'CreateUser' - Mail '" + user.getMail() + "' already taken");
            }

            user.setId(null);
            return userRepository.save(user);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public User ValidateUser(String mail, String password) {
        try {
            // Mail format validation (TO-DO: add regex)
            if (mail == null || mail.isBlank()) {
                throw new IllegalStateException("Error at 'ValidateUser' - Mail bad formatted: " + mail);
            }

            // Password format validation (TO-DO: add regex)
            if (password == null || password.isBlank()) {
                throw new IllegalStateException("Error at 'ValidateUser' - Password bad formatted for the attempted mail: " + mail);
            }

            // Correct mail & password validation (TO-DO: add encryption for password)
            User user = GetOneUserByMail(mail);
            if (!Objects.equals(mail, user.getMail()) || !Objects.equals(password, user.getPassword())) {
                throw new SecurityException("Error at 'ValidateUser' - Mail or password mismatch, attempted mail: " + mail);
            }

            return user;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public User UpdateUser(User user) {
        try {
            boolean exists = ExistsUser(user.getId());
            if (!exists) {
                throw new IllegalStateException("Error at 'UpdateUser' - User with ID: " + user.getId() + " doesn't exist");
            }
            // Mail unique validation
            Optional<User> possibleUser = userRepository.findByMail(user.getMail());
            if (possibleUser.isPresent()) {
                // Only fail validation if it's not the same user
                if (!Objects.equals(possibleUser.get().getId(), user.getId())) {
                    throw new IllegalStateException("Error at 'UpdateUser' - Mail '" + user.getMail() + "' already taken");
                }
            }

            return userRepository.save(user);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Long DeleteUser(Long userID) {
        try {
            boolean exists = ExistsUser(userID);
            if (!exists) {
                throw new IllegalStateException("Error at 'DeleteUser' - User with ID: " + userID + " doesn't exist");
            }

            userRepository.deleteById(userID);
            return userID;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }
}
