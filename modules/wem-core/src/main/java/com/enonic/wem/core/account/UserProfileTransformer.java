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
import com.enonic.cms.core.user.field.UserFields;

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
                throw new IllegalArgumentException( "Unexpected field type in user profile: " + fieldName );
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

    public UserFields userProfileToUserFields( final UserProfile profile )
    {
        final UserFields userFields = new UserFields();
        userFields.setFirstName( profile.getFirstName() );
        userFields.setLastName( profile.getLastName() );
        userFields.setMiddleName( profile.getMiddleName() );
        userFields.setNickName( profile.getNickName() );
        if ( profile.getBirthday() != null )
        {
            userFields.setBirthday( profile.getBirthday().toDate() );
        }
        userFields.setCountry( profile.getCountry() );
        userFields.setDescription( profile.getDescription() );
        userFields.setInitials( profile.getInitials() );
        userFields.setGlobalPosition( profile.getGlobalPosition() );
        userFields.setHtmlEmail( profile.getHtmlEmail() );
        userFields.setLocale( profile.getLocale() );
        userFields.setPersonalId( profile.getPersonalId() );
        userFields.setMemberId( profile.getMemberId() );
        userFields.setOrganization( profile.getOrganization() );
        userFields.setPhone( profile.getPhone() );
        userFields.setFax( profile.getFax() );
        userFields.setMobile( profile.getMobile() );
        userFields.setPrefix( profile.getPrefix() );
        userFields.setSuffix( profile.getSuffix() );
        userFields.setTitle( profile.getTitle() );
        userFields.setTimezone( profile.getTimeZone() );
        userFields.setHomePage( profile.getHomePage() );
        if ( profile.getGender() != null )
        {
            userFields.setGender( com.enonic.cms.api.client.model.user.Gender.valueOf( profile.getGender().name() ) );
        }

        if ( profile.getAddresses() != null )
        {
            userFields.setAddresses( profileAddressesToEntityAddresses( profile.getAddresses() ) );
        }
        return userFields;
    }

    private com.enonic.cms.api.client.model.user.Address[] profileAddressesToEntityAddresses( final Addresses addresses )
    {
        final com.enonic.cms.api.client.model.user.Address[] userAddresses =
            new com.enonic.cms.api.client.model.user.Address[addresses.getSize()];
        final List<Address> addressList = addresses.getList();
        for ( int i = 0; i < userAddresses.length; i++ )
        {
            userAddresses[i] = profileAddressToEntityAddress( addressList.get( i ) );
        }
        return userAddresses;
    }

    private com.enonic.cms.api.client.model.user.Address profileAddressToEntityAddress( final Address address )
    {
        final com.enonic.cms.api.client.model.user.Address userAddress = new com.enonic.cms.api.client.model.user.Address();
        userAddress.setLabel( address.getLabel() );
        userAddress.setCountry( address.getCountry() );
        userAddress.setIsoCountry( address.getIsoCountry() );
        userAddress.setRegion( address.getRegion() );
        userAddress.setIsoRegion( address.getIsoRegion() );
        userAddress.setPostalCode( address.getPostalCode() );
        userAddress.setPostalAddress( address.getPostalAddress() );
        userAddress.setStreet( address.getStreet() );
        return userAddress;
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
