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
    private final UserMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {

        return userRepository
                .findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", String.valueOf(id)));

        return mapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));

        return mapper.toResponseDto(user);
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
    public UserResponseDto create(UserCreateDto dto) {
        if (hasDuplicate(dto.username())) {
            throw new DuplicateEntityException("User", "username", dto.username());
        }

        User user = mapper.toUser(dto);

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException(USER_ROLE_NOT_FOUND));

        user.getRoles().add(userRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return mapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto update(UserUpdateDto dto) {
        User actingUser = authUtils.getAuthenticatedUser();

        if (hasDuplicate(dto.username())) {
            throw new DuplicateEntityException("User", "username", dto.username());
        }

        if (dto.username() != null) actingUser.setUsername(dto.username());
        if (dto.password() != null) actingUser.setPassword(passwordEncoder.encode(dto.password()));

        userRepository.saveAndFlush(actingUser);
        return mapper.toResponseDto(actingUser);
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
