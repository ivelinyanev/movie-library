package movielibrary.services;

import lombok.RequiredArgsConstructor;
import movielibrary.dtos.users.UserCreateDto;
import movielibrary.dtos.users.UserResponseDto;
import movielibrary.dtos.users.UserUpdateDto;
import movielibrary.enums.ERole;
import movielibrary.exceptions.DuplicateEntityException;
import movielibrary.exceptions.EntityNotFoundException;
import movielibrary.mappers.UserMapper;
import movielibrary.models.Role;
import movielibrary.models.User;
import movielibrary.repositories.RoleRepository;
import movielibrary.repositories.UserRepository;
import movielibrary.utils.AuthUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static movielibrary.utils.StringConstants.USER_ROLE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtils authUtils;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {

        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", String.valueOf(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public User getByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));
    }

    /*
        For AuthController (internal use)
     */
    @Override
    @Transactional(readOnly = true)
    public User getEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public User create(User user) {
        if (hasDuplicate(user.getUsername())) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException(USER_ROLE_NOT_FOUND));

        user.getRoles().add(userRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(User user) {
        User actingUser = authUtils.getAuthenticatedUser();

        if (hasDuplicate(user.getUsername())) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }

        if (user.getUsername() != null) actingUser.setUsername(user.getUsername());
        if (user.getPassword() != null) actingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.saveAndFlush(actingUser);
    }

    @Override
    @Transactional
    public void delete() {
        User actingUser = authUtils.getAuthenticatedUser();

        userRepository.delete(actingUser);
    }

    @Override
    @Transactional
    public void adminDelete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", String.valueOf(id)));

        userRepository.delete(user);
    }

    private boolean hasDuplicate(String username) {
        User actingUser = authUtils.getAuthenticatedUser();

        if (username == null) return false;
        if (username.equals(actingUser.getUsername())) return false;

        return userRepository.existsByUsernameAndIdNot(username, actingUser.getId());
    }
}
