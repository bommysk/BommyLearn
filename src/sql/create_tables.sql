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

DROP TABLE IF EXISTS class CASCADE;

CREATE TABLE class (
    id serial PRIMARY KEY,
    name varchar(40) NOT NULL UNIQUE,
    description text NOT NULL,
    start_date date NOT NULL,
    end_date NOT NULL
);

DROP TABLE IF EXISTS teaches CASCADE;

CREATE TABLE teaches (
    class_id int NOT NULL,
    teacher_id int NOT NULL,
    PRIMARY KEY(class_id, teacher_id),
    FOREIGN KEY(class_id) REFERENCES class ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(teacher_id) REFERENCES teacher ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS classschedule CASCADE;

CREATE TABLE classschedule (
    class_id int NOT NULL,
    student_id int NOT NULL,
    teacher_id int NOT NULL,
    PRIMARY KEY(class_id, student_id, teacher_id),
    FOREIGN KEY(class_id) REFERENCES class ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(student_id) REFERENCES student ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(teacher_id) REFERENCES teacher ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS attend CASCADE;
/*  A set of possible values is: “in progress”, “completed successfully”, “completed partially” and “has not completed class” */
CREATE TABLE attend (
    student_id int NOT NULL,
    class_id int NOT NULL,
    class_enrollment_date date NOT NULL,
    class_drop_date date,
    drop_class_reason text NOT NULL,
    attendance_outcome text NOT NULL,
    FOREIGN KEY(class_id) REFERENCES class ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(student_id) REFERENCES student ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS assignment CASCADE;

CREATE TABLE assignment (
    id serial PRIMARY KEY,
    name varchar(40) NOT NULL UNIQUE,
    description text NOT NULL,
    due_date date NOT NULL,
    class_id int NOT NULL,
    FOREIGN KEY(class_id) REFERENCES class ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS grade CASCADE;

CREATE TABLE grade (
    id serial PRIMARY KEY,
    points int NOT NULL,
    assignment_id int NOT NULL,
    FOREIGN KEY(assignment_id) REFERENCES assignment ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS comment CASCADE;

CREATE TABLE comment (
    id serial PRIMARY KEY,
    comment text NOT NULL,
    student_id int NOT NULL,
    assignment_id int NOT NULL,
    FOREIGN KEY(student_id) REFERENCES student ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(assignment_id) REFERENCES assignment ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS forum CASCADE;

CREATE TABLE forum (
    id serial PRIMARY KEY,
    comment text NOT NULL,
    student_id int NOT NULL,
    assignment_id int,
    class_id int NOT NULL,
    FOREIGN KEY(student_id) REFERENCES student ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(assignment_id) REFERENCES assignment ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(class_id) REFERENCES class ON DELETE CASCADE ON UPDATE CASCADE
);