package com.enonic.wem.core.jcr.accounts;

import java.util.Arrays;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.core.jcr.JcrNode;
import com.enonic.wem.core.jcr.JcrWemConstants;
import com.enonic.wem.core.jcr.MockJcrNode;

import static com.enonic.wem.core.jcr.JcrWemConstants.GROUPS_NODE;
import static com.enonic.wem.core.jcr.JcrWemConstants.ROLES_NODE;
import static com.enonic.wem.core.jcr.JcrWemConstants.USERSTORES_PATH;
import static com.enonic.wem.core.jcr.JcrWemConstants.USERS_NODE;
import static com.enonic.wem.core.jcr.accounts.AccountJcrMapping.ADDRESS;
import static com.enonic.wem.core.jcr.accounts.AccountJcrMapping.ADDRESSES;
import static org.junit.Assert.*;

public class AccountJcrMappingTest
{

    private MockJcrNode createUserstoreNode( final String userStoreName )
    {
        final MockJcrNode rootNode = new MockJcrNode( JcrWemConstants.ROOT_NODE );
        final MockJcrNode userstoresNode = new MockJcrNode( USERSTORES_PATH );
        final MockJcrNode userstoreNode = new MockJcrNode( userStoreName );
        rootNode.addNode( userstoresNode );
        userstoresNode.addNode( userstoreNode );
        return userstoreNode;
    }

    @Test
    public void roundTripUserConversionTest()
    {
        final String USERSTORE_NAME = "enonic";
        final String USER_ID = "58a6410c-724d-4081-814d-f9ba9ec37f36";
        final MockJcrNode userstoreNode = createUserstoreNode( USERSTORE_NAME );
        final MockJcrNode usersNode = new MockJcrNode( USERS_NODE );
        final MockJcrNode addressesNode = new MockJcrNode( ADDRESSES );
        userstoreNode.addNode( usersNode );
        final MockJcrNode userNode = new MockJcrNode();
        userNode.addNode( addressesNode );
        usersNode.addNode( userNode );

        final AccountJcrMapping mapping = new AccountJcrMapping();
        userNode.setId( USER_ID );
        final JcrUser user = new JcrUser();
        user.setId( USER_ID );
        user.setName( "name" );
        user.setUserStore( USERSTORE_NAME );
        user.setSyncValue( "sync" );
        user.setDisplayName( "disp" );
        user.setLastModified( new DateTime() );
        user.setLastLogged( new DateTime().minusMinutes( 1 ) );
        user.setEmail( "abc@host.com" );
        final JcrUserInfo userInfo = user.getUserInfo();
        userInfo.setBirthday( DateTime.parse( "2000-12-31" ) );
        userInfo.setCountry( "Norway" );
        userInfo.setFax( "12345678" );
        userInfo.setFirstName( "first" );
        userInfo.setGlobalPosition( "there" );
        userInfo.setHomePage( "http://www.enonic.com" );
        userInfo.setHtmlEmail( false );
        userInfo.setInitials( "F. L." );
        userInfo.setLastName( "last" );
        userInfo.setLocale( "no" );
        userInfo.setMemberId( "222" );
        userInfo.setMiddleName( "middle" );
        userInfo.setMobile( "12 34 56 78" );
        userInfo.setOrganization( "Enonic" );
        userInfo.setPhone( "99 88 77 66 55" );
        userInfo.setPrefix( "Mr." );
        userInfo.setSuffix( "Jr." );
        userInfo.setTimeZone( "CEST" );
        userInfo.setTitle( "CEO" );
        userInfo.setGender( Gender.FEMALE );
        userInfo.setNickName( "nick" );
        final JcrAddress address = new JcrAddress();
        userInfo.getAddresses().add( address );
        address.setCountry( "Norway" );
        address.setIsoCountry( "NO" );
        address.setIsoRegion( "AK" );
        address.setLabel( "Home" );
        address.setPostalAddress( "Oslo" );
        address.setPostalCode( "1234" );
        address.setRegion( "Akershus" );
        address.setStreet( "Kirkegata" );

        // convert to JCR
        mapping.userToJcr( user, userNode );

        // convert back from JCR
        final JcrUser userFromJcr = mapping.toUser( userNode );
        assertTrue( userFromJcr.isUser() );
        assertFalse( userFromJcr.isGroup() );
        assertFalse( userFromJcr.isRole() );
        AssertAccounts.assertUserEquals( user, userFromJcr );
    }

    @Test
    public void toUserTest()
    {
        final AccountJcrMapping mapping = new AccountJcrMapping();

        final String userStoreName = "enonic";
        final MockJcrNode userstoreNode = createUserstoreNode( userStoreName );
        final MockJcrNode usersNode = new MockJcrNode( USERS_NODE );
        final MockJcrNode addressesNode = new MockJcrNode( ADDRESSES );
        final MockJcrNode userNode = new MockJcrNode( "myname" );

        final DateTime lastModified = new DateTime();
        final byte[] photo = "photodata".getBytes();
        // main properties
        userNode.setPropertyString( "name", "myname" );
        userNode.setPropertyString( "displayName", "disp_name" );
        userNode.setPropertyString( "email", "name@host.com" );
        userNode.setPropertyDateTime( "lastModified", lastModified );
        userNode.setPropertyString( "syncValue", "sync1234" );
        userNode.setPropertyBinary( "photo", photo );
        userNode.setPropertyString( "type", "user" );
        // user info
        userNode.setPropertyDateTime( "birthday", DateTime.parse( "2000-12-31" ) );
        userNode.setPropertyString( "country", "Norway" );
        userNode.setPropertyString( "fax", "12345678" );
        userNode.setPropertyString( "firstName", "first" );
        userNode.setPropertyString( "globalPosition", "there" );
        userNode.setPropertyString( "homePage", "http://www.enonic.com" );
        userNode.setPropertyBoolean( "htmlEmail", true );
        userNode.setPropertyString( "initials", "F. L." );
        userNode.setPropertyString( "lastName", "last" );
        userNode.setPropertyString( "locale", "no" );
        userNode.setPropertyString( "memberId", "222" );
        userNode.setPropertyString( "middleName", "middle" );
        userNode.setPropertyString( "mobile", "12 34 56 78" );
        userNode.setPropertyString( "organization", "Enonic" );
        userNode.setPropertyString( "personalId", "2000123156987" );
        userNode.setPropertyString( "phone", "99 88 77 66 55" );
        userNode.setPropertyString( "prefix", "Mr." );
        userNode.setPropertyString( "suffix", "Jr." );
        userNode.setPropertyString( "timezone", "CEST" );
        userNode.setPropertyString( "title", "CEO" );
        userNode.setPropertyString( "gender", "MALE" );
        userNode.setPropertyString( "nickName", "nick" );
        // address
        final MockJcrNode addressNode = (MockJcrNode) addressesNode.addNode( ADDRESS );
        addressNode.setPropertyString( "label", "Home" );
        addressNode.setPropertyString( "street", "Kirkegata" );
        addressNode.setPropertyString( "postalAddress", "Oslo" );
        addressNode.setPropertyString( "postalCode", "1234" );
        addressNode.setPropertyString( "region", "Akershus" );
        addressNode.setPropertyString( "country", "Norway" );
        addressNode.setPropertyString( "isoRegion", "AK" );
        addressNode.setPropertyString( "isoCountry", "NO" );

        userstoreNode.addNode( usersNode );
        usersNode.addNode( userNode );
        userNode.addNode( addressesNode );

        final JcrUser user = mapping.toUser( userNode );

        // verify
        assertEquals( "myname", user.getName() );
        assertEquals( "name@host.com", user.getEmail() );
        assertEquals( "disp_name", user.getDisplayName() );
        assertEquals( lastModified, user.getLastModified() );
        assertEquals( JcrAccountType.USER, user.getAccountType() );
        assertEquals( "sync1234", user.getSyncValue() );
        assertTrue( user.hasPhoto() );
        assertTrue( Arrays.equals( photo, user.getPhoto() ) );
        // user info
        final JcrUserInfo userInfo = user.getUserInfo();
        assertEquals( DateTime.parse( "2000-12-31" ), userInfo.getBirthday() );
        assertEquals( "Norway", userInfo.getCountry() );
        assertEquals( "12345678", userInfo.getFax() );
        assertEquals( "first", userInfo.getFirstName() );
        assertEquals( "there", userInfo.getGlobalPosition() );
        assertEquals( "http://www.enonic.com", userInfo.getHomePage() );
        assertEquals( true, userInfo.getHtmlEmail() );
        assertEquals( "F. L.", userInfo.getInitials() );
        assertEquals( "last", userInfo.getLastName() );
        assertEquals( "no", userInfo.getLocale() );
        assertEquals( "222", userInfo.getMemberId() );
        assertEquals( "middle", userInfo.getMiddleName() );
        assertEquals( "12 34 56 78", userInfo.getMobile() );
        assertEquals( "Enonic", userInfo.getOrganization() );
        assertEquals( "99 88 77 66 55", userInfo.getPhone() );
        assertEquals( "Mr.", userInfo.getPrefix() );
        assertEquals( "Jr.", userInfo.getSuffix() );
        assertEquals( "CEST", userInfo.getTimeZone() );
        assertEquals( "CEO", userInfo.getTitle() );
        assertEquals( Gender.MALE, userInfo.getGender() );
        assertEquals( "nick", userInfo.getNickName() );
        // address
        assertNotNull( userInfo.getAddresses() );
        assertEquals( 1, userInfo.getAddresses().size() );
        final JcrAddress address = userInfo.getAddresses().get( 0 );
        assertEquals( "Home", address.getLabel() );
        assertEquals( "Kirkegata", address.getStreet() );
        assertEquals( "Oslo", address.getPostalAddress() );
        assertEquals( "1234", address.getPostalCode() );
        assertEquals( "Akershus", address.getRegion() );
        assertEquals( "Norway", address.getCountry() );
        assertEquals( "AK", address.getIsoRegion() );
        assertEquals( "NO", address.getIsoCountry() );
    }

    @Test
    public void userToJcrTest()
    {
        final String USERSTORE_NAME = "enonic";
        final String USER_ID = "58a6410c-724d-4081-814d-f9ba9ec37f36";
        final MockJcrNode userstoreNode = createUserstoreNode( USERSTORE_NAME );
        final MockJcrNode usersNode = new MockJcrNode( USERS_NODE );
        final MockJcrNode addressesNode = new MockJcrNode( ADDRESSES );
        userstoreNode.addNode( usersNode );
        final MockJcrNode userNode = new MockJcrNode();
        userNode.addNode( addressesNode );
        usersNode.addNode( userNode );

        final AccountJcrMapping mapping = new AccountJcrMapping();
        userNode.setId( USER_ID );
        final JcrUser user = new JcrUser();
        user.setId( USER_ID );
        user.setName( "name" );
        user.setUserStore( USERSTORE_NAME );
        user.setSyncValue( "sync" );
        user.setDisplayName( "disp" );
        user.setLastModified( new DateTime() );
        user.setEmail( "abc@host.com" );
        final JcrUserInfo userInfo = user.getUserInfo();
        userInfo.setBirthday( DateTime.parse( "2000-12-31" ) );
        userInfo.setCountry( "Norway" );
        userInfo.setFax( "12345678" );
        userInfo.setFirstName( "first" );
        userInfo.setGlobalPosition( "there" );
        userInfo.setHomePage( "http://www.enonic.com" );
        userInfo.setHtmlEmail( false );
        userInfo.setInitials( "F. L." );
        userInfo.setLastName( "last" );
        userInfo.setLocale( "no" );
        userInfo.setMemberId( "222" );
        userInfo.setMiddleName( "middle" );
        userInfo.setMobile( "12 34 56 78" );
        userInfo.setOrganization( "Enonic" );
        userInfo.setPhone( "99 88 77 66 55" );
        userInfo.setPrefix( "Mr." );
        userInfo.setSuffix( "Jr." );
        userInfo.setTimeZone( "CEST" );
        userInfo.setTitle( "CEO" );
        userInfo.setGender( Gender.FEMALE );
        userInfo.setNickName( "nick" );
        final JcrAddress address = new JcrAddress();
        userInfo.getAddresses().add( address );
        address.setCountry( "Norway" );
        address.setIsoCountry( "NO" );
        address.setIsoRegion( "AK" );
        address.setLabel( "Home" );
        address.setPostalAddress( "Oslo" );
        address.setPostalCode( "1234" );
        address.setRegion( "Akershus" );
        address.setStreet( "Kirkegata" );

        // convert to JCR
        mapping.userToJcr( user, userNode );

        assertEquals( user.getId(), userNode.getIdentifier() );
        assertEquals( user.getEmail(), userNode.getPropertyString( "email" ) );
        assertEquals( user.getName(), userNode.getPropertyString( "name" ) );
        assertEquals( user.getSyncValue(), userNode.getPropertyString( "syncValue" ) );
        assertEquals( user.getDisplayName(), userNode.getPropertyString( "displayName" ) );
        assertEquals( user.getLastModified(), userNode.getPropertyDateTime( "lastModified" ) );
        // user info
        assertEquals( userInfo.getBirthday(), userNode.getPropertyDateTime( "birthday" ) );
        assertEquals( userInfo.getCountry(), userNode.getPropertyString( "country" ) );
        assertEquals( userInfo.getFax(), userNode.getPropertyString( "fax" ) );
        assertEquals( userInfo.getFirstName(), userNode.getPropertyString( "firstName" ) );
        assertEquals( userInfo.getGlobalPosition(), userNode.getPropertyString( "globalPosition" ) );
        assertEquals( userInfo.getHomePage(), userNode.getPropertyString( "homePage" ) );
        assertEquals( userInfo.getHtmlEmail(), userNode.getPropertyBoolean( "htmlEmail" ) );
        assertEquals( userInfo.getInitials(), userNode.getPropertyString( "initials" ) );
        assertEquals( userInfo.getLastName(), userNode.getPropertyString( "lastName" ) );
        assertEquals( userInfo.getLocale(), userNode.getPropertyString( "locale" ) );
        assertEquals( userInfo.getMemberId(), userNode.getPropertyString( "memberId" ) );
        assertEquals( userInfo.getMiddleName(), userNode.getPropertyString( "middleName" ) );
        assertEquals( userInfo.getMobile(), userNode.getPropertyString( "mobile" ) );
        assertEquals( userInfo.getOrganization(), userNode.getPropertyString( "organization" ) );
        assertEquals( userInfo.getPhone(), userNode.getPropertyString( "phone" ) );
        assertEquals( userInfo.getPrefix(), userNode.getPropertyString( "prefix" ) );
        assertEquals( userInfo.getSuffix(), userNode.getPropertyString( "suffix" ) );
        assertEquals( userInfo.getTimeZone(), userNode.getPropertyString( "timezone" ) );
        assertEquals( userInfo.getTitle(), userNode.getPropertyString( "title" ) );
        assertEquals( userInfo.getGender(), Gender.valueOf( userNode.getPropertyString( "gender" ) ) );
        assertEquals( userInfo.getNickName(), userNode.getPropertyString( "nickName" ) );
        // address
        final JcrNode addressNode = userNode.getNode( ADDRESSES ).getNode( ADDRESS );
        assertEquals( address.getCountry(), addressNode.getPropertyString( "country" ) );
        assertEquals( address.getIsoCountry(), addressNode.getPropertyString( "isoCountry" ) );
        assertEquals( address.getIsoRegion(), addressNode.getPropertyString( "isoRegion" ) );
        assertEquals( address.getLabel(), addressNode.getPropertyString( "label" ) );
        assertEquals( address.getPostalAddress(), addressNode.getPropertyString( "postalAddress" ) );
        assertEquals( address.getPostalCode(), addressNode.getPropertyString( "postalCode" ) );
        assertEquals( address.getRegion(), addressNode.getPropertyString( "region" ) );
        assertEquals( address.getStreet(), addressNode.getPropertyString( "street" ) );
    }

    @Test
    public void roundTripGroupConversionTest()
    {
        final String USERSTORE_NAME = "enonic";
        final String GROUP_ID = "4497e4de-ca96-464f-a9e8-10f260e2468e";
        final MockJcrNode userstoreNode = createUserstoreNode( USERSTORE_NAME );
        final MockJcrNode groupsNode = new MockJcrNode( GROUPS_NODE );
        userstoreNode.addNode( groupsNode );
        final MockJcrNode groupNode = new MockJcrNode();
        groupsNode.addNode( groupNode );

        final AccountJcrMapping mapping = new AccountJcrMapping();
        groupNode.setId( GROUP_ID );
        final JcrGroup group = new JcrGroup();
        group.setId( GROUP_ID );
        group.setName( "name" );
        group.setUserStore( USERSTORE_NAME );
        group.setSyncValue( "sync" );
        group.setDisplayName( "disp" );
        group.setLastModified( new DateTime() );

        // convert to JCR
        mapping.groupToJcr( group, groupNode );

        // convert back from JCR
        final JcrGroup groupFromJcr = mapping.toGroup( groupNode );
        assertFalse( groupFromJcr.isUser() );
        assertTrue( groupFromJcr.isGroup() );
        assertFalse( groupFromJcr.isRole() );
        AssertAccounts.assertGroupEquals( group, groupFromJcr );
    }

    @Test
    public void toGroupTest()
    {
        final AccountJcrMapping mapping = new AccountJcrMapping();

        final String userStoreName = "enonic";
        final MockJcrNode userstoreNode = createUserstoreNode( userStoreName );
        final MockJcrNode groupsNode = new MockJcrNode( GROUPS_NODE );
        final MockJcrNode groupNode = new MockJcrNode( "myname" );
        userstoreNode.addNode( groupsNode );
        groupsNode.addNode( groupNode );

        final DateTime lastModified = new DateTime();
        // main properties
        groupNode.setPropertyString( "name", "test group" );
        groupNode.setPropertyString( "displayName", "disp_name" );
        groupNode.setPropertyDateTime( "lastModified", lastModified );
        groupNode.setPropertyString( "syncValue", "sync1234" );
        groupNode.setPropertyString( "type", "user" );
        final JcrGroup group = mapping.toGroup( groupNode );

        // verify
        assertEquals( "test group", group.getName() );
        assertEquals( "disp_name", group.getDisplayName() );
        assertEquals( lastModified, group.getLastModified() );
        assertEquals( JcrAccountType.GROUP, group.getAccountType() );
        assertEquals( "sync1234", group.getSyncValue() );
        assertFalse( group.hasPhoto() );
    }

    @Test
    public void groupToJcrTest()
    {
        final String USERSTORE_NAME = "enonic";
        final String GROUP_ID = "4497e4de-ca96-464f-a9e8-10f260e2468e";
        final MockJcrNode userstoreNode = createUserstoreNode( USERSTORE_NAME );
        final MockJcrNode groupsNode = new MockJcrNode( GROUPS_NODE );
        userstoreNode.addNode( groupsNode );
        final MockJcrNode groupNode = new MockJcrNode();
        groupsNode.addNode( groupNode );

        final AccountJcrMapping mapping = new AccountJcrMapping();
        groupNode.setId( GROUP_ID );
        final JcrGroup group = new JcrGroup();
        group.setId( GROUP_ID );
        group.setName( "name" );
        group.setUserStore( USERSTORE_NAME );
        group.setSyncValue( "sync" );
        group.setDisplayName( "disp" );
        group.setLastModified( new DateTime() );

        // convert to JCR
        mapping.groupToJcr( group, groupNode );
        assertEquals( group.getId(), groupNode.getIdentifier() );
        assertEquals( group.getName(), groupNode.getPropertyString( "name" ) );
        assertEquals( group.getSyncValue(), groupNode.getPropertyString( "syncValue" ) );
        assertEquals( group.getDisplayName(), groupNode.getPropertyString( "displayName" ) );
        assertEquals( group.getLastModified(), groupNode.getPropertyDateTime( "lastModified" ) );
    }


    @Test
    public void toRoleTest()
    {
        final AccountJcrMapping mapping = new AccountJcrMapping();

        final String userStoreName = "enonic";
        final MockJcrNode userstoreNode = createUserstoreNode( userStoreName );
        final MockJcrNode rolesNode = new MockJcrNode( ROLES_NODE );
        final MockJcrNode roleNode = new MockJcrNode( "test role" );
        userstoreNode.addNode( rolesNode );
        rolesNode.addNode( roleNode );

        final DateTime lastModified = new DateTime();
        // main properties
        roleNode.setPropertyString( "name", "test role" );
        roleNode.setPropertyString( "displayName", "disp_name" );
        roleNode.setPropertyDateTime( "lastModified", lastModified );
        roleNode.setPropertyString( "syncValue", "sync1234" );
        roleNode.setPropertyString( "type", "user" );
        final JcrRole role = mapping.toRole( roleNode );

        // verify
        assertFalse( role.isUser() );
        assertFalse( role.isGroup() );
        assertTrue( role.isRole() );
        assertEquals( "test role", role.getName() );
        assertEquals( "disp_name", role.getDisplayName() );
        assertEquals( lastModified, role.getLastModified() );
        assertEquals( JcrAccountType.ROLE, role.getAccountType() );
        assertEquals( "sync1234", role.getSyncValue() );
        assertFalse( role.hasPhoto() );
    }

    @Test
    public void roleToJcrTest()
    {
        final String USERSTORE_NAME = "enonic";
        final String ROLE_ID = "4497e4de-ca96-464f-a9e8-10f260e2468e";
        final MockJcrNode userstoreNode = createUserstoreNode( USERSTORE_NAME );
        final MockJcrNode rolesNode = new MockJcrNode( ROLES_NODE );
        userstoreNode.addNode( rolesNode );
        final MockJcrNode roleNode = new MockJcrNode();
        rolesNode.addNode( roleNode );

        final AccountJcrMapping mapping = new AccountJcrMapping();
        roleNode.setId( ROLE_ID );
        final JcrRole role = new JcrRole();
        role.setId( ROLE_ID );
        role.setName( "name" );
        role.setUserStore( USERSTORE_NAME );
        role.setSyncValue( "sync" );
        role.setDisplayName( "disp" );
        role.setLastModified( new DateTime() );

        // convert to JCR
        mapping.roleToJcr( role, roleNode );
        assertEquals( role.getId(), roleNode.getIdentifier() );
        assertEquals( role.getName(), roleNode.getPropertyString( "name" ) );
        assertEquals( role.getSyncValue(), roleNode.getPropertyString( "syncValue" ) );
        assertEquals( role.getDisplayName(), roleNode.getPropertyString( "displayName" ) );
        assertEquals( role.getLastModified(), roleNode.getPropertyDateTime( "lastModified" ) );
    }

    @Test
    public void jcrAccountTypeTest()
    {
        final JcrAccountType roleType = JcrAccountType.fromName( "role" );
        final JcrAccountType userType = JcrAccountType.fromName( "user" );
        final JcrAccountType groupType = JcrAccountType.fromName( "group" );
        final JcrAccountType nullType = JcrAccountType.fromName( null );
        final JcrAccountType unknownType = JcrAccountType.fromName( "invalidType" );

        assertEquals( JcrAccountType.USER, userType );
        assertEquals( JcrAccountType.GROUP, groupType );
        assertEquals( JcrAccountType.ROLE, roleType );
        assertNull( unknownType );
        assertNull( nullType );
    }

}
