/* 
CREATING THE TABLES
*/
DROP TABLE IF EXISTS Mark;
Drop Table IF EXISTS Lecturer;
Drop Table IF EXISTS Student;
Drop Table IF EXISTS Users;
Drop Table IF EXISTS Curriculum;
DROP TABLE IF EXISTS Material;
Drop Table IF EXISTS BusinessRuleModule;
Drop Table IF EXISTS BusinessRuleCourse;
Drop Table IF EXISTS BusinessRule;
Drop Table IF EXISTS BusinessRuleType;
Drop Table IF EXISTS Module;
Drop Table IF EXISTS Course;
Drop Table IF EXISTS Department;




Create Table Department(
    DeptNo VARCHAR(5) NOT NULL,
    Name VARCHAR(50) NOT NULL,
    CONSTRAINT pkDepartment PRIMARY KEY(DeptNo)
);


Create Table Course(
    CourseID VARCHAR(5) NOT NULL,
    Name VARCHAR(50) NOT NULL,
    Description VARCHAR(100) NOT NULL,
    LevelOfStudy VARCHAR(20) NOT NULL,
    AmountOfYears Int NOT NULL,
    DeptNo VARCHAR(5) NOT NULL,
    CONSTRAINT pkCourse PRIMARY KEY(CourseID),
    Constraint fkCourse FOREIGN KEY(DeptNo) REFERENCES Department(DeptNo) 
);


Create Table Module(
    ModuleID VARCHAR(5) NOT NULL,
    Name VARCHAR(50) NOT NULL,
    Description VARCHAR(100) NOT NULL,
    Credit INT NOT NULL,
    CONSTRAINT pkModule PRIMARY KEY(ModuleID)
);


CREATE TABLE Material(
    ModuleID VARCHAR(5) NOT NULL,
    Week INT NOT NULL,
    LectureNote VARCHAR(1000),
    LabNote VARCHAR(1000),
    CONSTRAINT pkMaterial PRIMARY KEY(ModuleID, Week),
    CONSTRAINT fkMaterial FOREIGN KEY(ModuleID) REFERENCES Module(ModuleID)
);


Create Table Curriculum(
    CourseID VARCHAR(5) NOT NULL,
    ModuleID VARCHAR(5) NOT NULL,
    Semester1 Boolean NOT NULL,
    Semester2 Boolean NOT NULL,
    Year Int NOT NULL, 
    CONSTRAINT pkCurriculum PRIMARY KEY(CourseID, ModuleID),
    Constraint fkCurriculum1 FOREIGN KEY(CourseID) REFERENCES Course(CourseID),
    Constraint fkCurriculum2 FOREIGN KEY(ModuleID) REFERENCES Module(ModuleID)
);


Create Table Users(
    UserID VARCHAR(5) NOT NULL,
    Forename VARCHAR(20) NOT NULL,
    Surname VARCHAR(20) NOT NULL,
    Email VARCHAR(20) NOT NULL,
    Password VARCHAR(20) NOT NULL,
    DoB Date NOT NULL,
    Gender VARCHAR(20) NOT NULL,
    Type VARCHAR(10) NOT NULL,
    ManagedBy VARCHAR(5),
    Activated Boolean NOT NULL,
    CONSTRAINT pkUser PRIMARY KEY(UserID),
    Constraint fkUser FOREIGN KEY(ManagedBy) REFERENCES Users(UserID)
);


Create Table Lecturer(
    UserID VARCHAR(5) NOT NULL,
    ModuleID VARCHAR(5) NOT NULL,
    Qualification Varchar(15) NOT NULL,
    CONSTRAINT pkLecturer PRIMARY KEY(UserID),
    Constraint fkLecturer FOREIGN KEY(UserID) REFERENCES Users(UserID),
    Constraint fkLecturer2 FOREIGN KEY(ModuleID) REFERENCES Module(ModuleID)
);


Create Table Student(
    UserID VARCHAR(5) NOT NULL,
    CourseID VARCHAR(5) NOT NULL,
    Decision VARCHAR(10) NOT NULL,
    yearOfStudy Int NOT NULL,
    CONSTRAINT pkStudent PRIMARY KEY(UserID),
    Constraint fkStudent FOREIGN KEY(UserID) REFERENCES Users(UserID),
    Constraint fkStudent2 FOREIGN KEY(CourseID) REFERENCES Course(CourseID),
    CONSTRAINT chk_Decision CHECK (Decision in ('Award', 'Resit', 'Withdrawal'))
);


Create Table Mark(
    ModuleID VARCHAR(5) NOT NULL,
    UserID VARCHAR(5) NOT NULL,
    AttNo INT NOT NULL,
    Lab FLOAT NOT NULL,
    Exam FLOAT NOT NULL,
    CONSTRAINT pkMark PRIMARY KEY(ModuleID, UserID, AttNo),
    Constraint fkMark FOREIGN KEY(ModuleID) REFERENCES Module(ModuleID),
    Constraint fkMark2 FOREIGN KEY(UserID) REFERENCES Users(UserID)
);


Create Table BusinessRuleType(
    TypeID VARCHAR(5) NOT NULL,
    Description VARCHAR(50) NOT NULL,
    CONSTRAINT pkType PRIMARY KEY(TypeID)
);


Create Table BusinessRule(
    RuleID VARCHAR(5) NOT NULL,
    Active Boolean NOT NULL,
    Value Int,
    TypeID VARCHAR(5) NOT NULL,
    CONSTRAINT pkRule PRIMARY KEY(RuleID),
    CONSTRAINT fkRule FOREIGN KEY(TypeID) REFERENCES BusinessRuleType(TypeID)
);


Create Table BusinessRuleModule(
    ModuleID VARCHAR(5) NOT NULL,
    RuleID VARCHAR(5) NOT NULL,
    CONSTRAINT pkRuleModule PRIMARY KEY(ModuleID, RuleID),
    CONSTRAINT fkRuleModule FOREIGN KEY(ModuleID) REFERENCES Module(ModuleID),
    CONSTRAINT fkRuleModule2 FOREIGN KEY(RuleID) REFERENCES BusinessRule(RuleID)
);


Create Table BusinessRuleCourse(
    CourseID VARCHAR(5) NOT NULL,
    RuleID VARCHAR(5) NOT NULL,
    CONSTRAINT pkRuleCourse PRIMARY KEY(CourseID, RuleID),
    CONSTRAINT fkRuleCourse FOREIGN KEY(CourseID) REFERENCES Course(CourseID),
    CONSTRAINT fkRuleCourse2 FOREIGN KEY(RuleID) REFERENCES BusinessRule(RuleID)
);


/* 
DUMMY INSERT DATA
*/

INSERT INTO Department 
VALUES(1, "Computing and Information Sciences");

INSERT INTO Department 
VALUES (2, "Physics");

INSERT INTO Course 
VALUES(1, "Computing Science", "We do programming", "Undergraduate", 4, 1);

INSERT INTO Course 
VALUES(2, "Software Engineering", "We do programming but with work placement", "Undergraduate", 5, 1);

INSERT INTO Course 
VALUES(3, "Physics", "We do physics", "Undergraduate", 4, 2);

INSERT INTO Module 
VALUES("CS101", "Topics in Computing", "module description for cs101 :)", 20);

INSERT INTO Module 
VALUES("CS103", "Machines, Languages & Computation", "module description for cs103 :3", 20);

INSERT INTO Module 
VALUES("CS104", "Information & Information Systems", "module description for cs104 :ppp", 20);

INSERT INTO Module 
VALUES("CS105", "Programming Foundations", "module description for cs105 x3", 20);

INSERT INTO Module 
VALUES("CS106", "Computer Systems & Organisation", "module description for cs106 :o", 20);

INSERT INTO Curriculum 
VALUES(1, "CS101", True, True, 1);

INSERT INTO Curriculum 
VALUES(2, "CS103", True, True, 1);

INSERT INTO Users 
VALUES("mng1", "Big", "Boss", "boss@Strathclyde", "YouKnowTheRules", "1983-04-27", "Male", "Manager", NULL, true);

INSERT INTO Users 
VALUES("mng2", "Fred", "Fredrick", "Fred@Strathclyde", "AndSoDoI", "1999-03-13", "Male", "Manager", "mng1", true);

UPDATE Users SET ManagedBy = "mng2" WHERE UserID = "mng1";

INSERT INTO Users
VALUES("stu1", "Matthew", "Duffy", "Matthew@Strathclyde", "NeverGonna", "2003-10-09", "Male", "Student", "mng1", false);

INSERT INTO Users
VALUES("lec1", "Veronica", "Sawyer", "Veronica@Strathclyde", "GiveYouUp", "2003-02-07", "Female", "Lecturer", "mng1", true);

INSERT INTO Lecturer
VALUES("lec1", "CS106", "MSc"); 

INSERT INTO Student 
VALUES("stu1", 2, "Award", 3);

INSERT INTO Mark 
VALUES("CS106", "stu1", 1, 88.00, 99.00);

INSERT INTO BusinessRuleType 
VALUES("BR001", "MaxResits");

INSERT INTO BusinessRule
VALUES("RU001", False, 3, "BR001");

INSERT INTO BusinessRule
VALUES("RU002", True, 3, "BR001");

INSERT INTO BusinessRuleModule
VALUES("CS101", "RU002");

INSERT INTO BusinessRuleCourse
VALUES(1, "RU002");