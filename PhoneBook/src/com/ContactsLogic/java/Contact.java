package com.ContactsLogic.java;

import java.time.LocalDate;

public class Contact {

    private int id;
    private String lastName;
    private String firstName;
    private String thirdName;
    private String mobilePhoneNumber;
    private String homePhoneNumber;
    private String address;
    private LocalDate birthdayDate;
    private String additionalInfo;

    public int getId() { return id; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getThirdName() { return thirdName; }
    public String getMobilePhoneNumber() { return mobilePhoneNumber; }
    public String getHomePhoneNumber() { return homePhoneNumber; }
    public String getAddress() { return address; }
    public LocalDate getBirthdayDate() { return birthdayDate; }
    public String getAdditionalInfo() { return additionalInfo; }

    public Contact(String lastName, String firstName, String thirdName, String mobilePhoneNumber, String homePhoneNumber, String address, LocalDate birthdayDate, String additionalInfo)
    {
        setFirstName(firstName);
        setLastName(lastName);
        setThirdName(thirdName);
        setMobilePhoneNumber(mobilePhoneNumber);
        setHomePhoneNumber(homePhoneNumber);
        setAddress(address);
        setBirthdayDate(birthdayDate);
        setAdditionalInfo(additionalInfo);
    }

    public void setId(int value)
    {
        id = value;
        onValueChanged();
    }
    public void setLastName(String value)
    {
        lastName = value.trim();
        onValueChanged();
    }
    public void setFirstName(String value)
    {
        firstName = value.trim();
        onValueChanged();
    }
    public void setThirdName(String value)
    {
        thirdName = value.trim();
        onValueChanged();
    }
    public void setMobilePhoneNumber(String value)
    {
        mobilePhoneNumber = value.trim();
        onValueChanged();
    }
    public void setHomePhoneNumber(String value)
    {
        homePhoneNumber = value.trim();
        onValueChanged();
    }
    public void setAddress(String value)
    {
        address = value.trim();
        onValueChanged();
    }
    public void setBirthdayDate(LocalDate value)
    {
        birthdayDate = value;
        onValueChanged();
    }
    public void setAdditionalInfo(String value)
    {
        additionalInfo = value.trim();
        onValueChanged();
    }

    public void replaceBy(final Contact other)
    {
        lastName = other.lastName;
        firstName = other.firstName;
        thirdName = other.thirdName;
        mobilePhoneNumber = other.mobilePhoneNumber;
        homePhoneNumber = other.homePhoneNumber;
        address = other.address;
        birthdayDate = other.birthdayDate;
        additionalInfo = other.additionalInfo;
    }

    public boolean compareByNames(final Contact other)
    {
        return firstName.toLowerCase().equals(other.getFirstName().toLowerCase()) &&
                lastName.toLowerCase().equals(other.getLastName().toLowerCase()) &&
                thirdName.toLowerCase().equals(other.getThirdName().toLowerCase());
    }

    public boolean validate()
    {
        return !(lastName.isBlank() || firstName.isBlank() || (mobilePhoneNumber.isBlank() && homePhoneNumber.isBlank()));
    }

    @Override
    public String toString()
    {
        return String.format("%s;%s;%s;%s;%s;%s;%s;%s;",
                lastName,
                firstName,
                thirdName,
                mobilePhoneNumber,
                homePhoneNumber,
                address,
                birthdayDate == null ? "" : birthdayDate.toString(),
                additionalInfo);
    }

    private void onValueChanged() {}
}
