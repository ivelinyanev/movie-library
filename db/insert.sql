-- Users
INSERT INTO `movie-library`.users (user_id, password, username) VALUES (1, '$2a$10$P5coc7DxTvNesAkVwL2VmOehtAbD.BPqXn3LEhoWhYUogBq4sxy/u', 'admin');
-- password = admin

INSERT INTO `movie-library`.users (user_id, password, username) VALUES (2, '$2a$10$k2veKBQsaD/sWxkfbZoDPu6R/xWIZzbqlqWLH38mzJknjtgZWT21S', 'user');
-- password = user

INSERT INTO `movie-library`.users (user_id, password, username) VALUES (3, '$2a$10$MwXCqjE9ofrkkYQOGkMX6uUZgjvIEA9HsMMYOR0RC9RBJOt57BxzG', 'test');
-- password = test

-- Roles
INSERT INTO `movie-library`.roles (role_id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO `movie-library`.roles (role_id, name) VALUES (2, 'ROLE_USER');

-- User Roles
INSERT INTO `movie-library`.users_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO `movie-library`.users_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO `movie-library`.users_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO `movie-library`.users_roles (user_id, role_id) VALUES (3, 2);
