# StudentManagementSystem

### The Software Engineering project consists of the development of an online system for managing teaching activity

#### Involves:
- Time & Attendance Control
- Participation based on Difficulties and Merits

#### Requirements

1. The Data Model consists of Users (Teachers or Students), Curricular Units, Classes, Attendance and Participation Facts.

    - The Facts of Participation are at the choice of the group, consider for example: interpretation, autonomy, good or bad interventions, among others.
   
    - These facts happen during a class and include a student

    - Attendance will also be the result of an association between a student and a class

    - Classes must be created by the teacher when choosing a curricular unit, at which point he can create a class and set a date.

    - A course unit has multiple students and one teacher and vice versa

2. The System should allow for a class-by-class control with the addition of facts relating to the students
3. The System should allow you to enter the student and see your history of Merits and Difficulties, as well as the attendance map, and the graphics are at the student's discretion, if you don't have much inspiration you can always opt for summary tables with totals per fact. It should, however, make it possible to see the facts in chronological order.
4. The teacher's work interface should be a table with all students.

- Must deliver a set of prototypes at least on paper with the interfaces
- Must deliver a data model in UML Classes
- You must deliver a list of created Rest APIs describing at least the imput and output in JSON and/or URL parameters
- Must have the project on GITHUB, GITLAB, or BitBUCKET
- You must deliver in a document the list of Implemented Tests with a description
- You must present the project to the teacher in a scheduled class for this purpose and show the system in operation, the tests in operation

#### Dependencies:
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Validation
- MySQL Driver
- Lombok
- Spring Boot Starter DevTools
- Spring Boot Starter Security