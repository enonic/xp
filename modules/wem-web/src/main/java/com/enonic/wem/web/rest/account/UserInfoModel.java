package com.enonic.wem.web.rest.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class UserInfoModel
{
    public static final String FIRST_NAME = "firstName";

    public static final String LAST_NAME = "lastName";

    public static final String MIDDLE_NAME = "middleName";

    public static final String BIRTHDAY = "birthday";

    public static final String COUNTRY = "country";

    public static final String DESCRIPTION = "description";

    public static final String INITIALS = "initials";

    public static final String GLOBAL_POSITION = "globalPosition";

    public static final String LOCALE = "locale";

    public static final String NICK_NAME = "nickName";

    public static final String PERSONAL_ID = "personalId";

    public static final String ORGANIZATION = "organization";

    public static final String PREFIX = "prefix";

    public static final String SUFFIX = "suffix";

    public static final String TITLE = "title";

    public static final String HOME_PAGE = "homePage";

    public static final String MOBILE = "mobile";

    public static final String PHONE = "phone";

    public static final String FAX = "fax";

    public static final String TIMEZONE = "timezone";

    public static final String GENDER = "gender";

    public static final String ADDRESSES = "addresses";

    public static final String MEMBER_ID = "memberId";

    public static final String HTML_EMAIL = "htmlEmail";


    public UserInfoModel()
    {
    }

    public UserInfoModel( Map<String, Object> userData )
    {
        this.firstName = getUserField( userData, FIRST_NAME );
        this.lastName = getUserField( userData, LAST_NAME );
        this.middleName = getUserField( userData, MIDDLE_NAME );
        this.birthday = getUserField( userData, BIRTHDAY );
        this.country = getUserField( userData, COUNTRY );
        this.description = getUserField( userData, DESCRIPTION );
        this.fax = getUserField( userData, FAX );
        this.globalPosition = getUserField( userData, GLOBAL_POSITION );
        this.homePage = getUserField( userData, HOME_PAGE );
        this.initials = getUserField( userData, INITIALS );
        this.memberId = getUserField( userData, MEMBER_ID );
        this.mobile = getUserField( userData, MOBILE );
        this.nickName = getUserField( userData, NICK_NAME );
        this.organization = getUserField( userData, ORGANIZATION );
        this.personalId = getUserField( userData, PERSONAL_ID );
        this.phone = getUserField( userData, PHONE );
        this.prefix = getUserField( userData, PREFIX );
        this.suffix = getUserField( userData, SUFFIX );
        this.title = getUserField( userData, TITLE );
        this.timeZone = getUserField( userData, TIMEZONE );
        this.gender = getUserField( userData, GENDER );
        this.title = getUserField( userData, TITLE );
        this.locale = getUserField( userData, LOCALE );
        if ( userData.get( ADDRESSES ) != null )
        {
            List<Map<String, Object>> addressesData = (List<Map<String, Object>>) userData.get( ADDRESSES );
            this.addresses = new ArrayList<AddressModel>();
            for ( Map<String, Object> address : addressesData )
            {
                this.addresses.add( new AddressModel( address ) );
            }

        }
    }

    private String firstName;


    private String lastName;


    private String middleName;

    private String birthday;

    private String country;

    private String description;

    private String initials;

    private String globalPosition;

    private String htmlEmail;

    private String locale;

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

    private String timeZone;

    private String gender;

    private List<AddressModel> addresses = new ArrayList<AddressModel>();

    @JsonProperty(FIRST_NAME)
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    @JsonProperty(LAST_NAME)
    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    @JsonProperty(MIDDLE_NAME)
    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    public String getBirthday()
    {
        return birthday;
    }

    public void setBirthday( String birthday )
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

    public String getInitials()
    {
        return initials;
    }

    public void setInitials( String initials )
    {
        this.initials = initials;
    }

    @JsonProperty(GLOBAL_POSITION)
    public String getGlobalPosition()
    {
        return globalPosition;
    }

    public void setGlobalPosition( String globalPosition )
    {
        this.globalPosition = globalPosition;
    }

    @JsonProperty(HTML_EMAIL)
    public String getHtmlEmail()
    {
        return htmlEmail;
    }

    public void setHtmlEmail( String htmlEmail )
    {
        this.htmlEmail = htmlEmail;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale( String locale )
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

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile( String mobile )
    {
        this.mobile = mobile;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone( String phone )
    {
        this.phone = phone;
    }

    public String getFax()
    {
        return fax;
    }

    public void setFax( String fax )
    {
        this.fax = fax;
    }

    @JsonProperty("timezone")
    public String getTimeZone()
    {
        return timeZone;
    }

    public void setTimeZone( String timeZone )
    {
        this.timeZone = timeZone;
    }

    public List<AddressModel> getAddresses()
    {
        return addresses;
    }

    public void setAddresses( List<AddressModel> addresses )
    {
        this.addresses = addresses;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public String getUserField( Map<String, Object> userData, String fieldName )
    {
        return userData.get( fieldName ) != null ? userData.get( fieldName ).toString() : null;
    }
}
