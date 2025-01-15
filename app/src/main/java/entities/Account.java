package entities;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * represents a user account in the system
 */
public abstract class Account {

    // Private fields
    /**
     * the name of the account holder
     */
    private final String name;

    /**
     * the email address associated with the account
     */
    private final String email;

    /**
     * the password for the account
     */
    private final String password;

    /**
     * the role of the user in the system
     */
    private String role;

    /**
     * the phone number associated with the account
     */
    private String phoneNumber;

    /**
     * the profile picture of the user
     */
    private byte[] profilePicture;

    /**
     * the date when the account was created
     */
    private final LocalDate accountCreationDate;

    /**
     * the age of the account holder
     */
    private int age;

    /**
     * the city where the account holder resides
     */
    private String residenceCity;

    /**
     * the unique identifier for the user
     */
    private final String userID;

    /**
     * a counter used to generate unique user ids
     */
    private static int idMemory = 1;

    private boolean isDisabled;

    /**
     * constructs a new account with all details
     *
     * @param name             the name of the account holder
     * @param email            the email address of the account holder
     * @param password         the password for the account
     * @param role             the role of the user in the system
     * @param phoneNumber      the phone number of the account holder
     * @param profilePicture   the profile picture of the user
     * @param accountCreationDate the date when the account was created
     * @param age              the age of the account holder
     * @param residenceCity    the city where the account holder resides
     * @param userID           the unique identifier for the user
     */
    public Account(String name, String email, String password, String role, String phoneNumber,
                   byte[] profilePicture, long accountCreationDate, int age, String residenceCity, String userID) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.userID = String.valueOf(idMemory);
        idMemory++;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        this.accountCreationDate = getCurrentDate();
        this.residenceCity = residenceCity;
        this.age = age;
        this.residenceCity = residenceCity;
    }

    /**
     * constructs a new account with basic details
     *
     * @param name     the name of the account holder
     * @param email    the email address of the account holder
     * @param password the password for the account
     * @param role     the role of the user in the system
     */
    public Account(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.userID = String.valueOf(idMemory);
        idMemory++;
        this.accountCreationDate = getCurrentDate();
        this.isDisabled = false;
    }

    /**
     * gets the name of the account holder
     *
     * @return the name of the account holder
     */
    public String getName() {
        return name;
    }

    /**
     * gets the email address of the account holder
     *
     * @return the email address of the account holder
     */
    public String getEmail() {
        return email;
    }

    /**
     * gets the phone number of the account holder
     *
     * @return the phone number of the account holder
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * gets the account creation date
     *
     * @return the date when the account was created
     */
    public LocalDate getAccountCreationDate() {
        return accountCreationDate;
    }

    /**
     * gets the age of the account holder
     *
     * @return the age of the account holder
     */
    public int getAge() {
        return age;
    }

    /**
     * gets the residence city of the account holder
     *
     * @return the city where the account holder resides
     */
    public String getResidenceCity() {
        return residenceCity;
    }

    /**
     * gets the user id
     *
     * @return the unique identifier for the user
     */
    public String getUserID() {
        return userID;
    }

    /**
     * gets the user role
     *
     * @return the role of the user in the system
     */
    public String getUserRole() {
        return role;
    }

    /**
     * gets the account password
     *
     * @return the password for the account
     */
    public String getPassword() {
        return password;
    }

    /**
     * sets the user role
     *
     * @param role the new role to be assigned to the user
     */
    public void setUserRoles(String role) {
        this.role = role;
    }

    /**
     * gets the current date
     *
     * @return the current date
     */
    private LocalDate getCurrentDate() {
        LocalDate today = LocalDate.now();
        return today;
    }
}