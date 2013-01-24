package com.enonic.wem.migrate.account;

import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;

final class OldUserInfoTransformer
{
    public OldUserInfo toUserInfo( OldUserFields fields )
    {
        OldUserInfo info = new OldUserInfo();
        updateUserInfo( info, fields );
        return info;
    }

    public void updateUserInfo( OldUserInfo info, OldUserFields fields )
    {
        for ( OldUserField field : fields )
        {
            if ( !field.isOfType( OldUserFieldType.ADDRESS ) )
            {
                updateUserInfo( info, field );
            }
        }

        updateAddresses( info, fields );
    }

    private void updateUserInfo( OldUserInfo info, OldUserField field )
    {
        switch ( field.getType() )
        {
            case FIRST_NAME:
                info.setFirstName( (String) field.getValue() );
                break;
            case BIRTHDAY:
                info.setBirthday( (Date) field.getValue() );
                break;
            case COUNTRY:
                info.setCountry( (String) field.getValue() );
                break;
            case DESCRIPTION:
                info.setDescription( (String) field.getValue() );
                break;
            case FAX:
                info.setFax( (String) field.getValue() );
                break;
            case GENDER:
                info.setGender( (OldGender) field.getValue() );
                break;
            case GLOBAL_POSITION:
                info.setGlobalPosition( (String) field.getValue() );
                break;
            case HOME_PAGE:
                info.setHomePage( (String) field.getValue() );
                break;
            case HTML_EMAIL:
                info.setHtmlEmail( (Boolean) field.getValue() );
                break;
            case INITIALS:
                info.setInitials( (String) field.getValue() );
                break;
            case LAST_NAME:
                info.setLastName( (String) field.getValue() );
                break;
            case LOCALE:
                info.setLocale( (Locale) field.getValue() );
                break;
            case MEMBER_ID:
                info.setMemberId( (String) field.getValue() );
                break;
            case MIDDLE_NAME:
                info.setMiddleName( (String) field.getValue() );
                break;
            case MOBILE:
                info.setMobile( (String) field.getValue() );
                break;
            case NICK_NAME:
                info.setNickName( (String) field.getValue() );
                break;
            case ORGANIZATION:
                info.setOrganization( (String) field.getValue() );
                break;
            case PERSONAL_ID:
                info.setPersonalId( (String) field.getValue() );
                break;
            case PHONE:
                info.setPhone( (String) field.getValue() );
                break;
            case PHOTO:
                info.setPhoto( (byte[]) field.getValue() );
                break;
            case PREFIX:
                info.setPrefix( (String) field.getValue() );
                break;
            case SUFFIX:
                info.setSuffix( (String) field.getValue() );
                break;
            case TIME_ZONE:
                info.setTimezone( (TimeZone) field.getValue() );
                break;
            case TITLE:
                info.setTitle( (String) field.getValue() );
                break;
        }
    }

    private void updateAddresses( OldUserInfo info, OldUserFields fields )
    {
        OldAddress[] existing = info.getAddresses();
        OldAddress[] addresses = toAddresses( fields );

        if ( addresses.length == 0 )
        {
            return;
        }

        if ( existing.length == 0 )
        {
            // Overwrite all
            info.setAddresses( addresses );
        }
        else
        {
            // Overwrite only primary
            existing[0] = addresses[0];
            info.setAddresses( existing );
        }
    }

    private OldAddress[] toAddresses( OldUserFields fields )
    {
        LinkedList<OldAddress> list = new LinkedList<>();
        for ( OldUserField field : fields.getFields( OldUserFieldType.ADDRESS ) )
        {
            list.add( (OldAddress) field.getValue() );
        }

        return list.toArray( new OldAddress[list.size()] );
    }
}
