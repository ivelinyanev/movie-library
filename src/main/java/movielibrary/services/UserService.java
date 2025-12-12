package movielibrary.services;

import movielibrary.models.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getById(Long id);

    User getByUsername(String username);

    User getEntityByUsername(String username);

    User create(User user);

    User update(User user);

    void delete();

    void adminDelete(Long id);
}
