package com.enonic.wem.api.account.profile;

import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserProfileTest
{
    @Test
    public void testSetters()
    {
        final UserProfile profile = new UserProfile();

        final Addresses addresses = Addresses.empty();
        assertNull( profile.getAddresses() );
        profile.setAddresses( addresses );
        assertSame( addresses, profile.getAddresses() );

        final DateTime now = DateTime.now();
        assertNull( profile.getBirthday() );
        profile.setBirthday( now );
        assertEquals( now, profile.getBirthday() );

        assertNull( profile.getCountry() );
        profile.setCountry( "country" );
        assertEquals( "country", profile.getCountry() );

        assertNull( profile.getDescription() );
        profile.setDescription( "description" );
        assertEquals( "description", profile.getDescription() );

        assertNull( profile.getFax() );
        profile.setFax( "fax" );
        assertEquals( "fax", profile.getFax() );

        assertNull( profile.getFirstName() );
        profile.setFirstName( "firstName" );
        assertEquals( "firstName", profile.getFirstName() );

        assertNull( profile.getGender() );
        profile.setGender( Gender.MALE );
        assertEquals( Gender.MALE, profile.getGender() );

        assertNull( profile.getGlobalPosition() );
        profile.setGlobalPosition( "globalPosition" );
        assertEquals( "globalPosition", profile.getGlobalPosition() );

        assertNull( profile.getHomePage() );
        profile.setHomePage( "homePage" );
        assertEquals( "homePage", profile.getHomePage() );

        assertNull( profile.getHtmlEmail() );
        profile.setHtmlEmail( true );
        assertEquals( true, profile.getHtmlEmail() );

        assertNull( profile.getInitials() );
        profile.setInitials( "initials" );
        assertEquals( "initials", profile.getInitials() );

        assertNull( profile.getLastName() );
        profile.setLastName( "lastName" );
        assertEquals( "lastName", profile.getLastName() );

        assertNull( profile.getLocale() );
        profile.setLocale( Locale.ENGLISH );
        assertEquals( Locale.ENGLISH, profile.getLocale() );

        assertNull( profile.getMemberId() );
        profile.setMemberId( "memberId" );
        assertEquals( "memberId", profile.getMemberId() );

        assertNull( profile.getMiddleName() );
        profile.setMiddleName( "middleName" );
        assertEquals( "middleName", profile.getMiddleName() );

        assertNull( profile.getMobile() );
        profile.setMobile( "mobile" );
        assertEquals( "mobile", profile.getMobile() );

        assertNull( profile.getNickName() );
        profile.setNickName( "nickName" );
        assertEquals( "nickName", profile.getNickName() );

        assertNull( profile.getOrganization() );
        profile.setOrganization( "organization" );
        assertEquals( "organization", profile.getOrganization() );

        assertNull( profile.getPersonalId() );
        profile.setPersonalId( "personalId" );
        assertEquals( "personalId", profile.getPersonalId() );

        assertNull( profile.getPhone() );
        profile.setPhone( "phone" );
        assertEquals( "phone", profile.getPhone() );

        assertNull( profile.getPrefix() );
        profile.setPrefix( "prefix" );
        assertEquals( "prefix", profile.getPrefix() );

        assertNull( profile.getSuffix() );
        profile.setSuffix( "suffix" );
        assertEquals( "suffix", profile.getSuffix() );

        assertNull( profile.getTimeZone() );
        profile.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        assertEquals( TimeZone.getTimeZone( "UTC" ), profile.getTimeZone() );

        assertNull( profile.getTitle() );
        profile.setTitle( "title" );
        assertEquals( "title", profile.getTitle() );
    }
}
