package com.enonic.wem.core.account;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.Gender;
import com.enonic.wem.api.account.profile.UserProfile;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.user.field.UserFieldType;

class UserProfileTransformer
{
    private final static String F_COUNTRY = "country";

    private final static String F_ISO_COUNTRY = "iso-country";

    private final static String F_REGION = "region";

    private final static String F_ISO_REGION = "iso-region";

    private final static String F_LABEL = "label";

    private final static String F_STREET = "street";

    private final static String F_POSTAL_CODE = "postal-code";

    private final static String F_POSTAL_ADDRESS = "postal-address";

    public UserProfile userEntityToUserProfile( final UserEntity userEntity )
    {
        final UserProfile profile = new UserProfile();
        final List<Address> addresses = Lists.newArrayList();
        final Map<String, String> userFields = userEntity.getFieldMap();
        if ( userFields == null )
        {
            return profile;
        }
        for ( String fieldName : userFields.keySet() )
        {
            final String fieldId = StringUtils.substringBefore( fieldName, "[" );
            final UserFieldType field = UserFieldType.fromName( fieldId );
            final String value = userFields.get( fieldName );
            if ( field == null )
            {
                throw new IllegalArgumentException("Unexpected field type in user profile: " + fieldName);
            }
            switch ( field )
            {
                case FIRST_NAME:
                    profile.setFirstName( value );
                    break;
                case LAST_NAME:
                    profile.setLastName( value );
                    break;
                case MIDDLE_NAME:
                    profile.setMiddleName( value );
                    break;
                case NICK_NAME:
                    profile.setNickName( value );
                    break;
                case BIRTHDAY:
                    profile.setBirthday( DateTime.parse( value ) );
                    break;
                case COUNTRY:
                    profile.setCountry( value );
                    break;
                case DESCRIPTION:
                    profile.setDescription( value );
                    break;
                case INITIALS:
                    profile.setInitials( value );
                    break;
                case GLOBAL_POSITION:
                    profile.setGlobalPosition( value );
                    break;
                case HTML_EMAIL:
                    profile.setHtmlEmail( Boolean.parseBoolean( value ) );
                    break;
                case LOCALE:
                    profile.setLocale( new Locale( value ) );
                    break;
                case PERSONAL_ID:
                    profile.setPersonalId( value );
                    break;
                case MEMBER_ID:
                    profile.setMemberId( value );
                    break;
                case ORGANIZATION:
                    profile.setOrganization( value );
                    break;
                case PHONE:
                    profile.setPhone( value );
                    break;
                case FAX:
                    profile.setFax( value );
                    break;
                case MOBILE:
                    profile.setMobile( value );
                    break;
                case PREFIX:
                    profile.setPrefix( value );
                    break;
                case SUFFIX:
                    profile.setSuffix( value );
                    break;
                case TITLE:
                    profile.setTitle( value );
                    break;
                case TIME_ZONE:
                    profile.setTimeZone( TimeZone.getTimeZone( value ) );
                    break;
                case HOME_PAGE:
                    profile.setHomePage( value );
                    break;
                case GENDER:
                    profile.setGender( Gender.valueOf( value ) );
                    break;
                case ADDRESS:
                    final String addressField = StringUtils.substringAfter( fieldName, "." );
                    final int addressIndex = Integer.parseInt( StringUtils.substringBetween( fieldName, "[", "]" ) );
                    while ( addressIndex >= addresses.size() )
                    {
                        addresses.add( new Address() );
                    }
                    final Address address = addresses.get( addressIndex );
                    setAddressField( address, addressField, value );
                    break;
                case PHOTO:
                    // ignore photo field
                    break;
                default:
                    throw new IllegalArgumentException( "Unexpected field type in user profile: " + fieldName );
            }
        }

        profile.setAddresses( addresses.isEmpty() ? Addresses.empty() : Addresses.from( addresses ) );
        return profile;
    }

    private void setAddressField( final Address address, final String addressField, final String value )
    {
        if ( F_COUNTRY.equals( addressField ) )
        {
            address.setCountry( value );
        }
        else if ( F_ISO_COUNTRY.equals( addressField ) )
        {
            address.setIsoCountry( value );
        }
        else if ( F_REGION.equals( addressField ) )
        {
            address.setRegion( value );
        }
        else if ( F_ISO_REGION.equals( addressField ) )
        {
            address.setIsoRegion( value );
        }
        else if ( F_LABEL.equals( addressField ) )
        {
            address.setLabel( value );
        }
        else if ( F_STREET.equals( addressField ) )
        {
            address.setStreet( value );
        }
        else if ( F_POSTAL_CODE.equals( addressField ) )
        {
            address.setPostalCode( value );
        }
        else if ( F_POSTAL_ADDRESS.equals( addressField ) )
        {
            address.setPostalAddress( value );
        }
    }
}
