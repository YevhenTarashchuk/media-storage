INSERT INTO users(id, password, email, status, is_deleted, role)
VALUES (1, 'password', 'wade.smith@gmail.com', 'EMAIL_CONFIRMATION', false, 'ROLE_USER'),
       (2, 'password', 'boris.johnson@gmail.com', 'ACTIVE', false, 'ROLE_USER'),
       (3, 'password', 'harvey.williams@gmail.com', 'ACTIVE', true, 'ROLE_USER'),
       (4, 'password', 'roberto.brown@gmail.com', 'ACTIVE', false, 'ROLE_USER'),
       (5, 'password', 'david.miller@gmail.com', 'WAITING_FOR_APPROVING', false, 'ROLE_USER'),
       (6, 'password', 'luke.skywalker@gmail.com', 'DECLINED', false, 'ROLE_USER'),
       (7, 'password', 'darth.vader@gmail.com', 'ACTIVE', false, 'ROLE_USER');
