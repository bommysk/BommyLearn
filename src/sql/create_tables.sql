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
('carla','password','Dora','Page','sapien.molestie.orci@bommy.edu','P.O. Box 602, 840 Ultricies Ave', '04/13/17'),
('suki','password','Ava','Gonzalez','ac.turpis.egestas@bommy.edu','Ap #136-1258 Lorem, St.','08/01/16'),
('michelle','password','Amy','Odom','gravida.nunc@bommy.edu','624-5412 Turpis St.','08/11/16'),
('jael','password','Tamekah','Leach','interdum@bommy.edu','P.O. Box 484, 7208 In St.','06/16/16'),
('kadeem','password','Nola','Mclaughlin','quis@bommy.edu','7851 Fames Rd.','12/30/17'),
('hu','password','Fatima','Savage','fringilla@bommy.edu','Ap #385-5581 Suspendisse Av.','12/07/16'),
('ulysses','password','Addison','Tyson','Fusce.aliquam@bommy.edu','Ap #686-413 Lectus Rd.','11/09/17'),
('arsenio','password','Branden','Lane','libero@bommy.edu','P.O. Box 623, 7050 Metus. Ave','06/27/16'),
('wang','password','Fuller','Hayes','Donec@bommy.edu','5986 Cursus, Road','04/26/17'),
('len','password','Damon','Ortiz','Mauris.molestie@bommy.edu','P.O. Box 915, 7507 Sit Road','05/17/17');

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
    day_schedule varchar(10),
    start_time time NOT NULL,
    end_time time NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL
);

INSERT INTO class(name, description, day_schedule, start_time, end_time, start_date, end_date)
VALUES('Calculus', 'Calculus', 'MTWR', '04:10 PM', '05:10 PM', '03/31/2017', '06/15/2017');

INSERT INTO class(name, description, day_schedule, start_time, end_time, start_date, end_date)
VALUES('Medieval History', 'Study of European history from 800-1300.', 'MW', '03:10 PM', '05:10 PM', '03/31/2017', '06/15/2017');

INSERT INTO class(name, description, day_schedule, start_time, end_time, start_date, end_date)
VALUES('English', 'English', 'TR', '01:10 PM', '03:10 PM', '03/31/2017', '06/15/2017');

DROP TABLE IF EXISTS teaches CASCADE;

CREATE TABLE teaches (
    teacher_id int NOT NULL,
    class_id int NOT NULL,
    PRIMARY KEY(class_id, teacher_id),
    FOREIGN KEY(class_id) REFERENCES class ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(teacher_id) REFERENCES teacher ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO teaches(teacher_id, class_id) VALUES(1, 1);

INSERT INTO teaches(teacher_id, class_id) VALUES(1, 2);

INSERT INTO teaches(teacher_id, class_id) VALUES(2, 3); 

DROP TABLE IF EXISTS class_schedule CASCADE;

CREATE TABLE class_schedule (
    class_id int NOT NULL,
    student_id int NOT NULL,
    teacher_id int NOT NULL,
    PRIMARY KEY(class_id, student_id, teacher_id),
    FOREIGN KEY(class_id) REFERENCES class ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(student_id) REFERENCES student ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(teacher_id) REFERENCES teacher ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO class_schedule(class_id, student_id, teacher_id) VALUES(1, 1, 1);

INSERT INTO class_schedule(class_id, student_id, teacher_id) VALUES(2, 1, 1);

INSERT INTO class_schedule(class_id, student_id, teacher_id) VALUES(3, 11, 2);

INSERT INTO class_schedule(class_id, student_id, teacher_id) VALUES(1, 11, 2);

DROP TABLE IF EXISTS attend CASCADE;
/*  attendance_outcome possible values are: “in progress”, “completed successfully”, “completed partially” and “has not completed class” */
CREATE TABLE attend (
    id serial PRIMARY KEY,
    class_id int NOT NULL,
    student_id int NOT NULL,
    class_enrollment_date date NOT NULL,
    class_drop_date date,
    drop_class_reason text,
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
    teacher_id int NOT NULL,
    FOREIGN KEY(class_id) REFERENCES class ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(teacher_id) REFERENCES teacher ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO assignment(name, description, due_date, class_id, teacher_id) VALUES('Calculus Assgn 1', 'Find the derivative.', '04/5/2017', 1, 1);

INSERT INTO assignment(name, description, due_date, class_id, teacher_id) VALUES('Medieval England', 'Read Ch 1-2.', '04/4/2017', 2, 1);

INSERT INTO assignment(name, description, due_date, class_id, teacher_id) VALUES('Medieval Spain', 'Read Ch 1-3.', '05/25/2017', 2, 2);

INSERT INTO assignment(name, description, due_date, class_id, teacher_id) VALUES('Intro Essay', 'Write an essay that describes you.', '04/5/2017', 3, 2);

DROP TABLE IF EXISTS assignment_submit CASCADE;

CREATE TABLE assignment_submit (
    id serial PRIMARY KEY,
    assignment_id int NOT NULL,
    student_id int NOT NULL,
    file_path text NOT NULL,
    submit_date date NOT NULL,
    graded int NOT NULL,
    grade int,
    FOREIGN KEY(assignment_id) REFERENCES assignment ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(student_id) REFERENCES student ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS teacher_comment CASCADE;

CREATE TABLE teacher_comment (
    id serial PRIMARY KEY,
    comment text NOT NULL,
    teacher_id int NOT NULL,
    class_id int NOT NULL,
    student_response_id int, /* if responding to student comment, this will not be null. */
    post_date timestamp  NOT NULL,
    FOREIGN KEY(teacher_id) REFERENCES teacher ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(class_id) REFERENCES class ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS student_comment CASCADE;

CREATE TABLE student_comment (
    id serial PRIMARY KEY,
    comment text NOT NULL,
    student_id int NOT NULL,
    class_id int NOT NULL,
    teacher_response_id int, /* if responding to teacher comment, this will not be null. */
    student_response_id int, /* if responding to student comment, this will not be null. */
    post_date timestamp  NOT NULL,
    FOREIGN KEY(student_id) REFERENCES student ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(class_id) REFERENCES class ON DELETE CASCADE ON UPDATE CASCADE
);

ALTER TABLE teacher_comment 
   ADD CONSTRAINT fk_student_response_id
   FOREIGN KEY (student_response_id) 
   REFERENCES student(id);

ALTER TABLE student_comment 
   ADD CONSTRAINT fk_teacher_response_id
   FOREIGN KEY (teacher_response_id) 
   REFERENCES teacher(id);

ALTER TABLE student_comment 
   ADD CONSTRAINT fk_student_response_id
   FOREIGN KEY (student_response_id) 
   REFERENCES student(id);

DROP TABLE IF EXISTS forum CASCADE;

CREATE TABLE forum (
    id serial PRIMARY KEY,
    class_id int NOT NULL,
    teacher_comment_id int,
    student_comment_id int,
    FOREIGN KEY(teacher_comment_id) REFERENCES teacher_comment ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(student_comment_id) REFERENCES student_comment ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(class_id) REFERENCES class ON DELETE CASCADE ON UPDATE CASCADE
);
