package model;

import ru.spbau.kozlov.annotations.Test;
import ru.spbau.kozlov.annotations.TestClass;
import ru.spbau.kozlov.annotations.TestLevel;
import ru.spbau.kozlov.annotations.TestSpeed;

/**
 * @author adkozlov
 */
@TestClass(testUnit = "TestUnit")
public class Student implements Person {

    private final String firstName;
    private final String lastName;

    public Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @TestSpeed
    @Override
    public String getFirstName() {
        return firstName;
    }

    @TestSpeed
    @Override
    public String getLastName() {
        return lastName;
    }

    @Test(testName = "testFullName", testLevel = TestLevel.MEDIUM)
    public static void testFullName() {
        Student student = new Student("FirstName", "LastName");
        assert student.getFirstName().equals("FirstName");
        assert student.getLastName().equals("LastName");
    }
}
