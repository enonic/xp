package com.enonic.wem.migrate.account;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

final class OldUserInfo
{
    private String firstName;

    private String lastName;

    private String middleName;

    private Date birthday;

    private String country;

    private String description;

    private OldGender gender;

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

    private byte[] photo;

    private TimeZone timeZone;

    private OldAddress[] addresses;

    public OldUserInfo()
    {
        this.addresses = new OldAddress[0];
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday( Date birthday )
    {
        this.birthday = birthday;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry( String country )
    {
        this.country = country;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public OldGender getGender()
    {
        return gender;
    }

    public void setGender( OldGender gender )
    {
        this.gender = gender;
    }

    public String getInitials()
    {
        return initials;
    }

    public void setInitials( String initials )
    {
        this.initials = initials;
    }

    public String getGlobalPosition()
    {
        return globalPosition;
    }

    public void setGlobalPosition( String globalPosition )
    {
        this.globalPosition = globalPosition;
    }

    public Boolean getHtmlEmail()
    {
        return htmlEmail;
    }

    public void setHtmlEmail( Boolean htmlEmail )
    {
        this.htmlEmail = htmlEmail;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale( Locale locale )
    {
        this.locale = locale;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName( String nickName )
    {
        this.nickName = nickName;
    }

    public String getPersonalId()
    {
        return personalId;
    }

    public void setPersonalId( String personalId )
    {
        this.personalId = personalId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId( String memberId )
    {
        this.memberId = memberId;
    }

    public String getOrganization()
    {
        return organization;
    }

    public void setOrganization( String organization )
    {
        this.organization = organization;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
    }

    public String getSuffix()
    {
        return suffix;
    }

    public void setSuffix( String suffix )
    {
        this.suffix = suffix;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getHomePage()
    {
        return homePage;
    }

    public void setHomePage( String homePage )
    {
        this.homePage = homePage;
    }

    public void setPhoto( byte[] photo )
    {
        this.photo = photo;
    }

    public OldAddress[] getAddresses()
    {
        return this.addresses;
    }

    public void setAddresses( OldAddress... addresses )
    {
        this.addresses = addresses != null ? addresses : new OldAddress[0];
    }

    public String getMobile()
    {
        return this.mobile;
    }

    public void setMobile( String mobile )
    {
        this.mobile = mobile;
    }

    public String getPhone()
    {
        return this.phone;
    }

    public void setPhone( String phone )
    {
        this.phone = phone;
    }

    public String getFax()
    {
        return this.fax;
    }

    public void setFax( String fax )
    {
        this.fax = fax;
    }

    public TimeZone getTimeZone()
    {
        return timeZone;
    }

    public void setTimezone( TimeZone timeZone )
    {
        this.timeZone = timeZone;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        OldUserInfo userInfo = (OldUserInfo) o;

        if ( !Arrays.equals( addresses, userInfo.addresses ) )
        {
            return false;
        }
        if ( birthday != null ? !birthday.equals( userInfo.birthday ) : userInfo.birthday != null )
        {
            return false;
        }
        if ( country != null ? !country.equals( userInfo.country ) : userInfo.country != null )
        {
            return false;
        }
        if ( description != null ? !description.equals( userInfo.description ) : userInfo.description != null )
        {
            return false;
        }
        if ( fax != null ? !fax.equals( userInfo.fax ) : userInfo.fax != null )
        {
            return false;
        }
        if ( firstName != null ? !firstName.equals( userInfo.firstName ) : userInfo.firstName != null )
        {
            return false;
        }
        if ( gender != userInfo.gender )
        {
            return false;
        }
        if ( globalPosition != null ? !globalPosition.equals( userInfo.globalPosition ) : userInfo.globalPosition != null )
        {
            return false;
        }
        if ( homePage != null ? !homePage.equals( userInfo.homePage ) : userInfo.homePage != null )
        {
            return false;
        }
        if ( htmlEmail != null ? !htmlEmail.equals( userInfo.htmlEmail ) : userInfo.htmlEmail != null )
        {
            return false;
        }
        if ( initials != null ? !initials.equals( userInfo.initials ) : userInfo.initials != null )
        {
            return false;
        }
        if ( lastName != null ? !lastName.equals( userInfo.lastName ) : userInfo.lastName != null )
        {
            return false;
        }
        if ( locale != null ? !locale.equals( userInfo.locale ) : userInfo.locale != null )
        {
            return false;
        }
        if ( memberId != null ? !memberId.equals( userInfo.memberId ) : userInfo.memberId != null )
        {
            return false;
        }
        if ( middleName != null ? !middleName.equals( userInfo.middleName ) : userInfo.middleName != null )
        {
            return false;
        }
        if ( mobile != null ? !mobile.equals( userInfo.mobile ) : userInfo.mobile != null )
        {
            return false;
        }
        if ( nickName != null ? !nickName.equals( userInfo.nickName ) : userInfo.nickName != null )
        {
            return false;
        }
        if ( organization != null ? !organization.equals( userInfo.organization ) : userInfo.organization != null )
        {
            return false;
        }
        if ( personalId != null ? !personalId.equals( userInfo.personalId ) : userInfo.personalId != null )
        {
            return false;
        }
        if ( phone != null ? !phone.equals( userInfo.phone ) : userInfo.phone != null )
        {
            return false;
        }
        if ( !Arrays.equals( photo, userInfo.photo ) )
        {
            return false;
        }
        if ( prefix != null ? !prefix.equals( userInfo.prefix ) : userInfo.prefix != null )
        {
            return false;
        }
        if ( suffix != null ? !suffix.equals( userInfo.suffix ) : userInfo.suffix != null )
        {
            return false;
        }
        if ( timeZone != null ? !timeZone.equals( userInfo.timeZone ) : userInfo.timeZone != null )
        {
            return false;
        }
        if ( title != null ? !title.equals( userInfo.title ) : userInfo.title != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + ( lastName != null ? lastName.hashCode() : 0 );
        result = 31 * result + ( middleName != null ? middleName.hashCode() : 0 );
        return result;
    }
}

