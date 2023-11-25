/* 
CREATING THE TABLES
*/
DROP TABLE IF EXISTS BusinessRuleApplication;
DROP TABLE IF EXISTS Mark;
Drop Table IF EXISTS Lecturer;
Drop Table IF EXISTS Student;
Drop Table IF EXISTS Users;
Drop Table IF EXISTS Curriculum;
DROP TABLE IF EXISTS Material;
Drop Table IF EXISTS BusinessRuleModule;
Drop Table IF EXISTS BusinessRuleCourse;
Drop Table IF EXISTS BusinessRule;
Drop Table IF EXISTS Module;
Drop Table IF EXISTS Course;
Drop Table IF EXISTS Department;




Create Table Department(
    DeptNo VARCHAR(10) NOT NULL,
    Name VARCHAR(50) NOT NULL,
    CONSTRAINT pkDepartment PRIMARY KEY(DeptNo)
);


Create Table Course(
    CourseID VARCHAR(10) NOT NULL,
    Name VARCHAR(50) NOT NULL,
    Description VARCHAR(100) NOT NULL,
    LevelOfStudy VARCHAR(20) NOT NULL,
    AmountOfYears Int NOT NULL,
    DeptNo VARCHAR(10) NOT NULL,
    CONSTRAINT pkCourse PRIMARY KEY(CourseID),
    Constraint fkCourse FOREIGN KEY(DeptNo) REFERENCES Department(DeptNo) 
);


Create Table Module(
    ModuleID VARCHAR(10) NOT NULL,
    Name VARCHAR(50) NOT NULL,
    Description VARCHAR(100) NOT NULL,
    Credit INT NOT NULL,
    CONSTRAINT pkModule PRIMARY KEY(ModuleID)
);


CREATE TABLE Material(
    ModuleID VARCHAR(10) NOT NULL,
    Semester INT NOT NULL,
    Week INT NOT NULL,
    LectureNote LONGBLOB,
    LabNote LONGBLOB,
    CONSTRAINT pkMaterial PRIMARY KEY(ModuleID, Semester, Week),
    CONSTRAINT fkMaterial FOREIGN KEY(ModuleID) REFERENCES Module(ModuleID)
);


Create Table Curriculum(
    CourseID VARCHAR(10) NOT NULL,
    ModuleID VARCHAR(10) NOT NULL,
    Semester1 Boolean NOT NULL,
    Semester2 Boolean NOT NULL,
    Year Int NOT NULL, 
    CONSTRAINT pkCurriculum PRIMARY KEY(CourseID, ModuleID),
    Constraint fkCurriculum1 FOREIGN KEY(CourseID) REFERENCES Course(CourseID),
    Constraint fkCurriculum2 FOREIGN KEY(ModuleID) REFERENCES Module(ModuleID)
);


Create Table Users(
    UserID VARCHAR(10) NOT NULL,
    Forename VARCHAR(20) NOT NULL,
    Surname VARCHAR(20) NOT NULL,
    Email VARCHAR(20) NOT NULL,
    Password VARCHAR(20) NOT NULL,
    DoB Date NOT NULL,
    Gender VARCHAR(20) NOT NULL,
    Type VARCHAR(10) NOT NULL,
    ManagedBy VARCHAR(10),
    Activated Boolean NOT NULL,
    CONSTRAINT pkUser PRIMARY KEY(UserID),
    Constraint fkUser FOREIGN KEY(ManagedBy) REFERENCES Users(UserID)
);


Create Table Lecturer(
    UserID VARCHAR(5) NOT NULL,
    ModuleID VARCHAR(5),
    Qualification Varchar(15) NOT NULL,
    CONSTRAINT pkLecturer PRIMARY KEY(UserID),
    Constraint fkLecturer FOREIGN KEY(UserID) REFERENCES Users(UserID),
    Constraint fkLecturer2 FOREIGN KEY(ModuleID) REFERENCES Module(ModuleID)
);


Create Table Student(
    UserID VARCHAR(10) NOT NULL,
    CourseID VARCHAR(10),
    Decision VARCHAR(20) NOT NULL,
    yearOfStudy Int NOT NULL,
    CONSTRAINT pkStudent PRIMARY KEY(UserID),
    Constraint fkStudent FOREIGN KEY(UserID) REFERENCES Users(UserID),
    Constraint fkStudent2 FOREIGN KEY(CourseID) REFERENCES Course(CourseID),
    CONSTRAINT chk_Decision CHECK (Decision in ('Award', 'Resit', 'Withdrawal', 'No Decision'))
);


Create Table Mark(
    ModuleID VARCHAR(10) NOT NULL,
    UserID VARCHAR(10) NOT NULL,
    AttNo INT NOT NULL,
    Lab FLOAT,
    Exam FLOAT,
    CONSTRAINT pkMark PRIMARY KEY(ModuleID, UserID, AttNo),
    Constraint fkMark FOREIGN KEY(ModuleID) REFERENCES Module(ModuleID),
    Constraint fkMark2 FOREIGN KEY(UserID) REFERENCES Users(UserID)
);


Create Table BusinessRule(
    RuleID INT NOT NULL AUTO_INCREMENT,
    Active Boolean NOT NULL,
    Value Int,
    Type VARCHAR(50) NOT NULL,
    CONSTRAINT pkRule PRIMARY KEY(RuleID)
);


Create Table BusinessRuleModule(
    ModuleID VARCHAR(10) NOT NULL,
    RuleID INT NOT NULL,
    CONSTRAINT pkRuleModule PRIMARY KEY(ModuleID, RuleID),
    CONSTRAINT fkRuleModule FOREIGN KEY(ModuleID) REFERENCES Module(ModuleID),
    CONSTRAINT fkRuleModule2 FOREIGN KEY(RuleID) REFERENCES BusinessRule(RuleID)
);


Create Table BusinessRuleCourse(
    CourseID VARCHAR(10) NOT NULL,
    RuleID INT NOT NULL,
    CONSTRAINT pkRuleCourse PRIMARY KEY(CourseID, RuleID),
    CONSTRAINT fkRuleCourse FOREIGN KEY(CourseID) REFERENCES Course(CourseID),
    CONSTRAINT fkRuleCourse2 FOREIGN KEY(RuleID) REFERENCES BusinessRule(RuleID)
);

CREATE TABLE BusinessRuleApplication(
    ModuleID VARCHAR(10) NOT NULL,
    UserID VARCHAR(10) NOT NULL,
    AttNo INT NOT NULL,
    RuleID INT NOT NULL,
    CONSTRAINT pkRuleApplication PRIMARY KEY(ModuleID, UserID, AttNo, RuleID),
    CONSTRAINT fkRuleApplication FOREIGN KEY(ModuleID, UserID, AttNo) REFERENCES Mark(ModuleID, UserID, AttNo),
    CONSTRAINT fkRuleApplication2 FOREIGN KEY(RuleID) REFERENCES BusinessRule(RuleID)
);


/* 
DUMMY INSERT DATA
*/

INSERT INTO `Department` (`DeptNo`, `Name`) VALUES
    ('1', 'Computing and Information Sciences'),
    ('2', 'Physics');

INSERT INTO `Course` (`CourseID`, `Name`, `Description`, `LevelOfStudy`, `AmountOfYears`, `DeptNo`) VALUES
    ('BScCS', 'Computing Science', 'We do programming', 'Undergraduate', 4, '1'),
    ('BScSE', 'Software Engineering', 'We do programming but with work placement', 'Undergraduate', 5, '1');

INSERT INTO `Module` (`ModuleID`, `Name`, `Description`, `Credit`) VALUES
    ('CS101', 'Topics in Computing', 'module description for cs101 :)', 20),
    ('CS103', 'Machines, Languages & Computation', 'module description for cs103 :3', 20),
    ('CS104', 'Information & Information Systems', 'module description for cs104 :ppp', 20),
    ('CS105', 'Programming Foundations', 'module description for cs105 x3', 20),
    ('CS106', 'Computer Systems & Organisation', 'module description for cs106 :o', 20);

INSERT INTO `Curriculum` (`CourseID`, `ModuleID`, `Semester1`, `Semester2`, `Year`) VALUES
    ('BScCS', 'CS101', TRUE,  FALSE, 1),
    ('BScCS', 'CS103', TRUE,  FALSE, 1),
    ('BScCS', 'CS104', FALSE, TRUE,  1),
    ('BScCS', 'CS105', FALSE, TRUE,  1),
    ('BScSE', 'CS103', TRUE,  FALSE, 1),
    ('BScSE', 'CS104', FALSE, TRUE,  1),
    ('BScSE', 'CS105', TRUE,  FALSE, 1),
    ('BScSE', 'CS106', FALSE, TRUE,  1);

INSERT INTO `Users` (`UserID`, `Forename`, `Surname`, `Email`, `Password`, `DoB`, `Gender`, `Type`, `ManagedBy`, `Activated`) VALUES
    ('mng1', 'Big', 'Boss', 'boss@Strathclyde', 'LbhXabjGurEhyrf', '1983-04-27', 'Male', 'Manager', NULL, 1),
    ('mng2', 'Fred', 'Fredrick', 'Fred@Strathclyde', 'NaqFbQbV', '1999-03-13', 'Male', 'Manager', 'mng1', 1),
    ('lec1', 'Veronica', 'Sawyer', 'Veronica@Strathclyde', 'TvirLbhHc', '2003-02-07', 'Female', 'Lecturer', 'mng1', 1),
    ('stu1', 'Matthew', 'Duffy', 'Matthew@Strathclyde', 'ArireTbaan', '2003-10-09', 'Male', 'Student', 'mng1', 0);
UPDATE `Users` SET `ManagedBy` = 'mng2' WHERE `UserID` = 'mng1';

INSERT INTO `Lecturer` (`UserID`, `ModuleID`, `Qualification`) VALUES
    ('lec1', 'CS106', 'MSc');

INSERT INTO `Student` (`UserID`, `CourseID`, `Decision`, `yearOfStudy`) VALUES
    ('stu1', 'BScSE', 'Award', 1);

INSERT INTO `BusinessRule` (`RuleID`, `Active`, `Value`, `Type`) VALUES
    (1, FALSE, 1, 'MAX_RESITS'),
    (2, TRUE,  2, 'MAX_RESITS'),
    (3, TRUE,  2, 'MAX_RESITS'),
    (4, TRUE,  1, 'MAX_RESITS'),
    (5, TRUE,  1, 'MAX_RESITS'),
    (6, TRUE,  0, 'MAX_COMPENSATED_MODULES'),
    (7, TRUE,  2, 'MAX_COMPENSATED_MODULES'),
    (8, TRUE,  2, 'MAX_RESITS');

INSERT INTO `BusinessRuleCourse` (`CourseID`, `RuleID`) VALUES
    ('BScCS', 6),
    ('BScSE', 7),
    ('BScSE', 8);

INSERT INTO `BusinessRuleModule` (`ModuleID`, `RuleID`) VALUES
    ('CS103', 1),
    ('CS103', 2),
    ('CS104', 3),
    ('CS105', 4),
    ('CS106', 5);

INSERT INTO `Mark` (`ModuleID`, `UserID`, `AttNo`, `Lab`, `Exam`) VALUES
    ('CS103', 'stu1', 1, 44, 76),
    ('CS103', 'stu1', 2, 78, 82),
    ('CS104', 'stu1', 1, 44, 76),
    ('CS105', 'stu1', 1, 44, 76),
    ('CS106', 'stu1', 1, 72, 76);

INSERT INTO `BusinessRuleApplication` (`ModuleID`, `UserID`, `AttNo`, `RuleID`) VALUES
    ('CS103', 'stu1', 1, 1),
    ('CS103', 'stu1', 1, 7),
    ('CS103', 'stu1', 1, 8),
    ('CS103', 'stu1', 2, 2),
    ('CS103', 'stu1', 2, 7),
    ('CS103', 'stu1', 2, 8),
    ('CS104', 'stu1', 1, 3),
    ('CS104', 'stu1', 1, 7),
    ('CS104', 'stu1', 1, 8),
    ('CS105', 'stu1', 1, 4),
    ('CS105', 'stu1', 1, 7),
    ('CS105', 'stu1', 1, 8),
    ('CS106', 'stu1', 1, 5),
    ('CS106', 'stu1', 1, 7),
    ('CS106', 'stu1', 1, 8);