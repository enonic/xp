package com.enonic.wem.api.account.profile;

import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;

public final class UserProfile
{
    private String firstName;

    private String lastName;

    private String middleName;

    private DateTime birthday;

    private String country;

    private String description;

    private Gender gender;

    private String initials;

    private String globalPosition;

    private Boolean htmlEmail;

    private Locale locale;

    private String nickName;

    private String personalId;

    private String memberId;

    private String organization;

    private String prefix;

    private String suffix;

    private String title;

    private String homePage;

    private String mobile;

    private String phone;

    private String fax;

    private TimeZone timeZone;

    private Addresses addresses;

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( final String firstName )
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( final String lastName )
    {
        this.lastName = lastName;
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( final String middleName )
    {
        this.middleName = middleName;
    }

    public DateTime getBirthday()
    {
        return birthday;
    }

    public void setBirthday( final DateTime birthday )
    {
        this.birthday = birthday;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry( final String country )
    {
        this.country = country;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public Gender getGender()
    {
        return gender;
    }

    public void setGender( final Gender gender )
    {
        this.gender = gender;
    }

    public String getInitials()
    {
        return initials;
    }

    public void setInitials( final String initials )
    {
        this.initials = initials;
    }

    public String getGlobalPosition()
    {
        return globalPosition;
    }

    public void setGlobalPosition( final String globalPosition )
    {
        this.globalPosition = globalPosition;
    }

    public Boolean getHtmlEmail()
    {
        return htmlEmail;
    }

    public void setHtmlEmail( final Boolean htmlEmail )
    {
        this.htmlEmail = htmlEmail;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale( final Locale locale )
    {
        this.locale = locale;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName( final String nickName )
    {
        this.nickName = nickName;
    }

    public String getPersonalId()
    {
        return personalId;
    }

    public void setPersonalId( final String personalId )
    {
        this.personalId = personalId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId( final String memberId )
    {
        this.memberId = memberId;
    }

    public String getOrganization()
    {
        return organization;
    }

    public void setOrganization( final String organization )
    {
        this.organization = organization;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix( final String prefix )
    {
        this.prefix = prefix;
    }

    public String getSuffix()
    {
        return suffix;
    }

    public void setSuffix( final String suffix )
    {
        this.suffix = suffix;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( final String title )
    {
        this.title = title;
    }

    public String getHomePage()
    {
        return homePage;
    }

    public void setHomePage( final String homePage )
    {
        this.homePage = homePage;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile( final String mobile )
    {
        this.mobile = mobile;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone( final String phone )
    {
        this.phone = phone;
    }

    public String getFax()
    {
        return fax;
    }

    public void setFax( final String fax )
    {
        this.fax = fax;
    }

    public TimeZone getTimeZone()
    {
        return timeZone;
    }

    public void setTimeZone( final TimeZone timeZone )
    {
        this.timeZone = timeZone;
    }

    public Addresses getAddresses()
    {
        return addresses;
    }

    public void setAddresses( final Addresses addresses )
    {
        this.addresses = addresses;
    }
}
