INSERT INTO `user` (`id`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`, `email`, `enabled`, `firstname`, `lastname`, `password`, `username`) VALUES
('2193db77-2319-4d12-a84a-b3c543a40bf2', 'Admin', '2016-11-07 23:14:23', 'Admin', '2016-11-07 23:14:23', 'eikebierman@gmx.de', b'1', 'Eike', 'Bierman', '$2a$10$/ig.F95zb69SZPPo5/9Qt.ZklhtbdCGlrYEkH266AJSw9UIlr8uOa', 'Damlo'),
('a15c80bf-6db3-4cb4-9b14-9de86771d8b0', 'user', '2016-11-07 23:12:28', 'user', '2016-11-07 23:12:28', 'admin@miyava.de', b'1', 'Eike', 'Bierman', '$2a$10$bmjp.eH5bwARHSpYgNemb.XVWlYJNwsM4rlwJtfUg8tM5q4yragtq', 'Admin');

INSERT INTO `user_roles` (`id`, `user_roles`) VALUES
('2193db77-2319-4d12-a84a-b3c543a40bf2', 'ROLE_USER'),
('a15c80bf-6db3-4cb4-9b14-9de86771d8b0', 'ROLE_ADMIN');

INSERT INTO `user_sites` (`id`, `user_site`) VALUES
('2193db77-2319-4d12-a84a-b3c543a40bf2', 'SITE_WEBSITE'),
('a15c80bf-6db3-4cb4-9b14-9de86771d8b0', 'SITE_WEBSITE');

Benutzername: Admin Oder Damlo
Passwort: "123456"