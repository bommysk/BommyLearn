DROP TABLE IF EXISTS student CASCADE;

CREATE TABLE student (
    id                  serial PRIMARY KEY,
    login               varchar(40) NOT NULL UNIQUE,
    password            varchar(40) NOT NULL,
    first_name          varchar(100) NOT NULL,
    last_name           varchar(100) NOT NULL,
    email               varchar(100) NOT NULL,
    postal_address      varchar(100) NOT NULL,
    created_date        date NOT NULL
);

INSERT INTO student (login, password, first_name, last_name, email, postal_address, created_date) VALUES 
('Carla','ECY16XSW7PO','Dora','Page','sapien.molestie.orci@bommy.edu','P.O. Box 602, 840 Ultricies Ave', '04/13/17'),
('Suki','ABM89EGZ9SM','Ava','Gonzalez','ac.turpis.egestas@bommy.edu','Ap #136-1258 Lorem, St.','08/01/16'),
('Michelle','NAU18OOK2VQ','Amy','Odom','gravida.nunc@bommy.edu','624-5412 Turpis St.','08/11/16'),
('Jael','ZAK50BXP3QK','Tamekah','Leach','interdum@bommy.edu','P.O. Box 484, 7208 In St.','06/16/16'),
('Kadeem','JNT58VUM4UU','Nola','Mclaughlin','quis@bommy.edu','7851 Fames Rd.','12/30/17'),
('Hu','KHR48YZE5FR','Fatima','Savage','fringilla@bommy.edu','Ap #385-5581 Suspendisse Av.','12/07/16'),
('Ulysses','ASC93PCO1QL','Addison','Tyson','Fusce.aliquam@bommy.edu','Ap #686-413 Lectus Rd.','11/09/17'),
('Arsenio','KNR46YPJ9FY','Branden','Lane','libero@bommy.edu','P.O. Box 623, 7050 Metus. Ave','06/27/16'),
('Wang','PPC26ONX8KS','Fuller','Hayes','Donec@bommy.edu','5986 Cursus, Road','04/26/17'),
('Len','MXK49VYF6EQ','Damon','Ortiz','Mauris.molestie@bommy.edu','P.O. Box 915, 7507 Sit Road','05/17/17');
INSERT INTO student (login, password, first_name, last_name, email, postal_address, created_date)
VALUES('nkahal', 'password', 'Neeraj', 'Kahal', 'nkahal@bommy.edu', '123 Main Street', '04/15/17');

DROP TABLE IF EXISTS teacher CASCADE;

CREATE TABLE teacher (
    id                  serial PRIMARY KEY,
    login               varchar(40) NOT NULL UNIQUE,
    password            varchar(40) NOT NULL,
    first_name          varchar(100) NOT NULL,
    last_name           varchar(100) NOT NULL,
    email               varchar(100) NOT NULL,
    postal_address      varchar(100) NOT NULL,
    tenure              int NOT NULL,
    created_date        date NOT NULL
);

INSERT INTO teacher(login, password, first_name, last_name, email, postal_address, tenure, created_date)
VALUES('skahal', 'password', 'Shubham', 'Kahal', 'shubhamkahal@bommy.edu', '123 Main Street', 1, '04/20/2017');

INSERT INTO teacher(login, password, first_name, last_name, email, postal_address, tenure, created_date)
VALUES('siddkahal', 'password', 'Siddhant', 'Kahal', 'siddkahal@bommy.edu', '123 Main Street', 1, '04/21/2017');