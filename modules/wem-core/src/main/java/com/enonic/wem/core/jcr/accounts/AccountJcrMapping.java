package com.enonic.wem.core.jcr.accounts;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.core.jcr.JcrNode;
import com.enonic.wem.core.jcr.JcrNodeIterator;

public class AccountJcrMapping
{
    public JcrUser toUser( JcrNode userNode )
    {
        final JcrUser user = new JcrUser();
        user.setId( userNode.getIdentifier() );
        user.setName( userNode.getName() );
        user.setDisplayName( userNode.getPropertyString( "displayname" ) );
        user.setEmail( userNode.getPropertyString( "email" ) );
        user.setLastModified( userNode.getPropertyDateTime( "lastModified" ) );
        if ( userNode.hasProperty( "photo" ) )
        {
            user.setHasPhoto( true );
        }
        final String userstore = userNode.getParent().getParent().getName();
        user.setUserStore( userstore );

        final JcrUserInfo userInfo = toUserInfo( userNode );
        final List<JcrAddress> addresses = nodePropertiesToAddresses( userNode );
        userInfo.setAddresses( addresses );
        user.setUserInfo( userInfo );
        return user;
    }

    public void userToJcr( JcrUser user, JcrNode node )
    {
        node.setPropertyString( "name", user.getName() );
        node.setPropertyString( "displayname", user.getDisplayName() );
        node.setPropertyString( "email", user.getEmail() );
        node.setPropertyDateTime( "lastModified", user.getLastModified() );
        node.setPropertyString( "syncValue", user.getSyncValue() );
        if ( user.getPhoto() != null )
        {
            node.setPropertyBinary( "photo", user.getPhoto() );
        }
        node.setPropertyString( "type", JcrAccountType.USER.name() );

        userInfoToJcr( user.getUserInfo(), node );
    }

    public JcrGroup toGroup( JcrNode node )
    {
        final JcrGroup group = new JcrGroup();
        group.setName( node.getName() );
        group.setDescription( node.getPropertyString( "description" ) );
        group.setId( node.getIdentifier() );
        final String userstore = node.getParent().getParent().getName();
        group.setUserStore( userstore );
        return group;
    }

    public void groupToJcr( JcrGroup group, JcrNode node )
    {
        node.setPropertyString( "qualifiedName", group.getName() );
        node.setPropertyString( "displayname", group.getDisplayName() );
        node.setPropertyString( "description", group.getDescription() );
        node.setPropertyDateTime( "lastModified", group.getLastModified() );
        node.setPropertyString( "syncValue", group.getSyncValue() );
//        node.setPropertyLong( "groupType", groupType.toInteger() );
        node.setPropertyString( "type", JcrAccountType.GROUP.name() );
    }

    private void userInfoToJcr( JcrUserInfo userInfo, JcrNode userNode )
    {
        userNode.setPropertyDateTime( "birthday", userInfo.getBirthday() );
        userNode.setPropertyString( "country", userInfo.getCountry() );
        userNode.setPropertyString( "description", userInfo.getDescription() );
        userNode.setPropertyString( "fax", userInfo.getFax() );
        userNode.setPropertyString( "firstname", userInfo.getFirstName() );
        userNode.setPropertyString( "globalposition", userInfo.getGlobalPosition() );
        userNode.setPropertyString( "homepage", userInfo.getHomePage() );
        if ( userInfo.getHtmlEmail() != null )
        {
            userNode.setPropertyBoolean( "htmlemail", userInfo.getHtmlEmail() );
        }
        userNode.setPropertyString( "initials", userInfo.getInitials() );
        userNode.setPropertyString( "lastname", userInfo.getLastName() );
        userNode.setPropertyString( "locale", userInfo.getLocale() );
        userNode.setPropertyString( "memberid", userInfo.getMemberId() );
        userNode.setPropertyString( "middlename", userInfo.getMiddleName() );
        userNode.setPropertyString( "mobile", userInfo.getMobile() );
        userNode.setPropertyString( "organization", userInfo.getOrganization() );
        userNode.setPropertyString( "personalid", userInfo.getPersonalId() );
        userNode.setPropertyString( "phone", userInfo.getPhone() );
        userNode.setPropertyString( "prefix", userInfo.getPrefix() );
        userNode.setPropertyString( "suffix", userInfo.getSuffix() );
        userNode.setPropertyString( "timezone", userInfo.getTimeZone() );
        userNode.setPropertyString( "title", userInfo.getTitle() );
        Gender gender = userInfo.getGender();
        if ( gender != null )
        {
            userNode.setPropertyString( "gender", gender.toString() );
        }
        userNode.setPropertyString( "organization", userInfo.getOrganization() );
        final List<JcrAddress> addresses = userInfo.getAddresses();
        final JcrNode addressesNode = userNode.addNode( "addresses" );
        for ( JcrAddress address : addresses )
        {
            addAddressNode( address, addressesNode );
        }
    }

    private void addAddressNode( JcrAddress address, JcrNode addressesNode )
    {
        final JcrNode addressNode = addressesNode.addNode( "address" );
        addressNode.setPropertyString( "country", address.getCountry() );
        addressNode.setPropertyString( "isoCountry", address.getIsoCountry() );
        addressNode.setPropertyString( "isoRegion", address.getIsoRegion() );
        addressNode.setPropertyString( "label", address.getLabel() );
        addressNode.setPropertyString( "postalAddress", address.getPostalAddress() );
        addressNode.setPropertyString( "postalCode", address.getPostalCode() );
        addressNode.setPropertyString( "region", address.getRegion() );
        addressNode.setPropertyString( "street", address.getStreet() );
    }

    private JcrUserInfo toUserInfo( final JcrNode userNode )
    {
        final JcrUserInfo info = new JcrUserInfo();
        info.setBirthday( userNode.getPropertyDateTime( "birthday" ) );
        info.setCountry( userNode.getPropertyString( "country" ) );
        info.setDescription( userNode.getPropertyString( "description" ) );
        info.setFax( userNode.getPropertyString( "fax" ) );
        info.setFirstName( userNode.getPropertyString( "firstname" ) );
        info.setGlobalPosition( userNode.getPropertyString( "globalposition" ) );
        info.setHomePage( userNode.getPropertyString( "homepage" ) );
        info.setHtmlEmail( userNode.getPropertyBoolean( "htmlemail" ) );
        info.setInitials( userNode.getPropertyString( "initials" ) );
        info.setLastName( userNode.getPropertyString( "lastname" ) );
        info.setLocale( userNode.getPropertyString( "locale" ) );
        info.setMemberId( userNode.getPropertyString( "memberid" ) );
        info.setMiddleName( userNode.getPropertyString( "middlename" ) );
        info.setMobile( userNode.getPropertyString( "mobile" ) );
        info.setOrganization( userNode.getPropertyString( "organization" ) );
        info.setPersonalId( userNode.getPropertyString( "personalid" ) );
        info.setPhone( userNode.getPropertyString( "phone" ) );
        info.setPrefix( userNode.getPropertyString( "prefix" ) );
        info.setSuffix( userNode.getPropertyString( "suffix" ) );
        info.setTimeZone( userNode.getPropertyString( "timezone" ) );
        info.setTitle( userNode.getPropertyString( "title" ) );
        info.setGender( Gender.fromName( userNode.getPropertyString( "gender" ) ) );
        info.setOrganization( userNode.getPropertyString( "organization" ) );
        return info;
    }

    private List<JcrAddress> nodePropertiesToAddresses( JcrNode userNode )
    {
        final List<JcrAddress> addressList = new ArrayList<JcrAddress>();
        final JcrNode addresses = userNode.getNode( "addresses" );
        JcrNodeIterator addressNodeIt = addresses.getNodes( "address" );
        while ( addressNodeIt.hasNext() )
        {
            JcrNode addressNode = addressNodeIt.next();
            final JcrAddress address = new JcrAddress();
            address.setLabel( addressNode.getPropertyString( "label" ) );
            address.setStreet( addressNode.getPropertyString( "street" ) );
            address.setPostalAddress( addressNode.getPropertyString( "postalAddress" ) );
            address.setPostalCode( addressNode.getPropertyString( "postalCode" ) );
            address.setRegion( addressNode.getPropertyString( "region" ) );
            address.setCountry( addressNode.getPropertyString( "country" ) );
            address.setIsoRegion( addressNode.getPropertyString( "isoRegion" ) );
            address.setIsoCountry( addressNode.getPropertyString( "isoCountry" ) );
            addressList.add( address );
        }
        return addressList;
    }

}
