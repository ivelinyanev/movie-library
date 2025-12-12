package movielibrary.services;

import movielibrary.enums.ERole;
import movielibrary.exceptions.DuplicateEntityException;
import movielibrary.exceptions.EntityNotFoundException;
import movielibrary.models.Role;
import movielibrary.models.User;
import movielibrary.repositories.RoleRepository;
import movielibrary.repositories.UserRepository;
import movielibrary.utils.AuthUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getAll_returnsAll() {
        List<User> list = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(list);

        assertSame(list, service.getAll());
    }

    @Test
    void getById_found() {
        User u = new User(); u.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        assertSame(u, service.getById(1L));
    }

    @Test
    void getById_notFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void getByUsername_found() {
        User u = new User(); u.setUsername("bob");
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(u));

        assertSame(u, service.getByUsername("bob"));
    }

    @Test
    void getByUsername_notFound_throws() {
        when(userRepository.findByUsername("x")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getByUsername("x"));
    }

    @Test
    void getEntityByUsername_internal_found() {
        User u = new User(); u.setUsername("int");
        when(userRepository.findByUsername("int")).thenReturn(Optional.of(u));

        assertSame(u, service.getEntityByUsername("int"));
    }

    @Test
    void create_duplicate_throws() {
        User u = new User(); u.setUsername("dup");

        User acting = new User(); acting.setUsername("someone"); acting.setId(1L);
        when(authUtils.getAuthenticatedUser()).thenReturn(acting);
        when(userRepository.existsByUsernameAndIdNot("dup", acting.getId())).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> service.create(u));
        verify(userRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void create_success_saves() {
        User u = new User();
        u.setUsername("new");
        u.setPassword("raw");
        u.setRoles(new java.util.HashSet<>()); // ensure roles collection is initialized

        User saved = new User(); saved.setId(2L); saved.setUsername("new");

        User acting = new User(); acting.setUsername("someoneElse"); acting.setId(99L);
        when(authUtils.getAuthenticatedUser()).thenReturn(acting);

        when(userRepository.existsByUsernameAndIdNot(eq("new"), anyLong())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(saved);

        User result = service.create(u);

        assertSame(saved, result);
        verify(passwordEncoder).encode("raw");
        verify(userRepository).save(u);
    }

    @Test
    void update_duplicate_throws() {
        User acting = new User(); acting.setUsername("me"); acting.setId(10L);
        when(authUtils.getAuthenticatedUser()).thenReturn(acting);

        User incoming = new User();
        incoming.setUsername("someoneElse");

        when(userRepository.existsByUsernameAndIdNot(incoming.getUsername(), acting.getId())).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> service.update(incoming));
    }

    @Test
    void update_success_applies() {
        User acting = new User(); acting.setUsername("me"); acting.setId(10L);
        when(authUtils.getAuthenticatedUser()).thenReturn(acting);

        User incoming = new User();
        incoming.setUsername("newName");
        incoming.setPassword("newPass");

        when(userRepository.existsByUsernameAndIdNot("newName", acting.getId())).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("encNew");
        when(userRepository.saveAndFlush(acting)).thenReturn(acting);

        User result = service.update(incoming);

        assertEquals("newName", acting.getUsername());
        assertEquals("encNew", acting.getPassword());
        verify(userRepository).saveAndFlush(acting);
        assertSame(acting, result);
    }

    @Test
    void delete_deletesActingUser() {
        User acting = new User(); acting.setId(55L);
        when(authUtils.getAuthenticatedUser()).thenReturn(acting);

        service.delete();

        verify(userRepository).delete(acting);
    }

    @Test
    void adminDelete_notFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.adminDelete(1L));
    }

    @Test
    void adminDelete_found_deletes() {
        User u = new User(); u.setId(3L);
        when(userRepository.findById(3L)).thenReturn(Optional.of(u));

        service.adminDelete(3L);

        verify(userRepository).delete(u);
    }
}
