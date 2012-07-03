package com.enonic.wem.core.jcr.accounts;

import java.util.List;

import com.enonic.wem.core.jcr.JcrNode;

public class AccountJcrMapping
{
    public JcrUser toUser( JcrNode node )
    {
        return null;
    }

    public void userToJcr( JcrUser user, JcrNode node )
    {
        node.setPropertyString( "key", user.getId() );
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
        return null;
    }

    public void groupToJcr( JcrGroup group, JcrNode node )
    {
        node.setPropertyString( "qualifiedName", group.getName() );
        node.setPropertyString( "displayname", group.getDisplayName() );
        node.setPropertyString( "description", group.getDescription() );
        node.setPropertyString( "key", group.getId() );
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
}
