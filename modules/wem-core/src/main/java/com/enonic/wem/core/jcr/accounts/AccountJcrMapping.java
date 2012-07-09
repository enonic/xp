package com.enonic.wem.core.jcr.accounts;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.core.jcr.JcrNode;
import com.enonic.wem.core.jcr.JcrNodeIterator;

public class AccountJcrMapping
{

    static final String DISPLAY_NAME = "displayname";

    static final String EMAIL = "email";

    static final String LAST_MODIFIED = "lastModified";

    static final String PHOTO = "photo";

    static final String NAME = "name";

    static final String SYNC_VALUE = "syncValue";

    static final String TYPE = "type";

    static final String DESCRIPTION = "description";

    static final String QUALIFIED_NAME = "qualifiedName";

    static final String BIRTHDAY = "birthday";

    static final String COUNTRY = "country";

    static final String FAX = "fax";

    static final String FIRST_NAME = "firstname";

    static final String GLOBAL_POSITION = "globalposition";

    static final String HOME_PAGE = "homepage";

    static final String HTML_EMAIL = "htmlemail";

    static final String INITIALS = "initials";

    static final String LAST_NAME = "lastname";

    static final String LOCALE = "locale";

    static final String MEMBER_ID = "memberid";

    static final String MIDDLE_NAME = "middlename";

    static final String MOBILE = "mobile";

    static final String ORGANIZATION = "organization";

    static final String PERSONAL_ID = "personalid";

    static final String PHONE = "phone";

    static final String PREFIX = "prefix";

    static final String SUFFIX = "suffix";

    static final String TIMEZONE = "timezone";

    static final String TITLE = "title";

    static final String GENDER = "gender";

    static final String ADDRESSES = "addresses";

    static final String ADDRESS = "address";

    static final String ISO_COUNTRY = "isoCountry";

    static final String ISO_REGION = "isoRegion";

    static final String LABEL = "label";

    static final String POSTAL_ADDRESS = "postalAddress";

    static final String POSTAL_CODE = "postalCode";

    static final String REGION = "region";

    static final String STREET = "street";

    static final String MEMBER_REFERENCE = "ref";

    public JcrUser toUser( JcrNode userNode )
    {
        final JcrUser user = new JcrUser();
        user.setId( userNode.getIdentifier() );
        user.setName( userNode.getName() );
        user.setDisplayName( userNode.getPropertyString( DISPLAY_NAME ) );
        user.setEmail( userNode.getPropertyString( EMAIL ) );
        user.setLastModified( userNode.getPropertyDateTime( LAST_MODIFIED ) );
        if ( userNode.hasProperty( PHOTO ) )
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
        node.setPropertyString( NAME, user.getName() );
        node.setPropertyString( DISPLAY_NAME, user.getDisplayName() );
        node.setPropertyString( EMAIL, user.getEmail() );
        node.setPropertyDateTime( LAST_MODIFIED, user.getLastModified() );
        node.setPropertyString( SYNC_VALUE, user.getSyncValue() );
        if ( user.getPhoto() != null )
        {
            node.setPropertyBinary( PHOTO, user.getPhoto() );
        }
        node.setPropertyString( TYPE, JcrAccountType.USER.name() );

        userInfoToJcr( user.getUserInfo(), node );
    }

    public JcrRole toRole( JcrNode node )
    {
        final JcrRole role = new JcrRole();
        role.setName( node.getName() );
        role.setDescription( node.getPropertyString( DESCRIPTION ) );
        role.setId( node.getIdentifier() );
        final String userstore = node.getParent().getParent().getName();
        role.setUserStore( userstore );
        return role;
    }

    public void roleToJcr( JcrRole role, JcrNode node )
    {
        groupPropertiesToJcr( role, node );
        node.setPropertyString( TYPE, JcrAccountType.ROLE.name() );
    }

    public JcrGroup toGroup( JcrNode node )
    {
        final JcrGroup group = new JcrGroup();
        group.setName( node.getName() );
        group.setDescription( node.getPropertyString( DESCRIPTION ) );
        group.setId( node.getIdentifier() );
        final String userstore = node.getParent().getParent().getName();
        group.setUserStore( userstore );
        return group;
    }

    public void groupToJcr( JcrGroup group, JcrNode node )
    {
        groupPropertiesToJcr( group, node );
        node.setPropertyString( TYPE, JcrAccountType.GROUP.name() );
    }

    private void groupPropertiesToJcr( JcrGroup group, JcrNode node )
    {
        node.setPropertyString( QUALIFIED_NAME, group.getName() );
        node.setPropertyString( DISPLAY_NAME, group.getDisplayName() );
        node.setPropertyString( DESCRIPTION, group.getDescription() );
        node.setPropertyDateTime( LAST_MODIFIED, group.getLastModified() );
        node.setPropertyString( SYNC_VALUE, group.getSyncValue() );
    }

    private void userInfoToJcr( JcrUserInfo userInfo, JcrNode userNode )
    {
        userNode.setPropertyDateTime( BIRTHDAY, userInfo.getBirthday() );
        userNode.setPropertyString( COUNTRY, userInfo.getCountry() );
        userNode.setPropertyString( DESCRIPTION, userInfo.getDescription() );
        userNode.setPropertyString( FAX, userInfo.getFax() );
        userNode.setPropertyString( FIRST_NAME, userInfo.getFirstName() );
        userNode.setPropertyString( GLOBAL_POSITION, userInfo.getGlobalPosition() );
        userNode.setPropertyString( HOME_PAGE, userInfo.getHomePage() );
        if ( userInfo.getHtmlEmail() != null )
        {
            userNode.setPropertyBoolean( HTML_EMAIL, userInfo.getHtmlEmail() );
        }
        userNode.setPropertyString( INITIALS, userInfo.getInitials() );
        userNode.setPropertyString( LAST_NAME, userInfo.getLastName() );
        userNode.setPropertyString( LOCALE, userInfo.getLocale() );
        userNode.setPropertyString( MEMBER_ID, userInfo.getMemberId() );
        userNode.setPropertyString( MIDDLE_NAME, userInfo.getMiddleName() );
        userNode.setPropertyString( MOBILE, userInfo.getMobile() );
        userNode.setPropertyString( ORGANIZATION, userInfo.getOrganization() );
        userNode.setPropertyString( PERSONAL_ID, userInfo.getPersonalId() );
        userNode.setPropertyString( PHONE, userInfo.getPhone() );
        userNode.setPropertyString( PREFIX, userInfo.getPrefix() );
        userNode.setPropertyString( SUFFIX, userInfo.getSuffix() );
        userNode.setPropertyString( TIMEZONE, userInfo.getTimeZone() );
        userNode.setPropertyString( TITLE, userInfo.getTitle() );
        Gender gender = userInfo.getGender();
        if ( gender != null )
        {
            userNode.setPropertyString( GENDER, gender.toString() );
        }
        userNode.setPropertyString( ORGANIZATION, userInfo.getOrganization() );
        final List<JcrAddress> addresses = userInfo.getAddresses();
        final JcrNode addressesNode = userNode.addNode( ADDRESSES );
        for ( JcrAddress address : addresses )
        {
            addAddressNode( address, addressesNode );
        }
    }

    private void addAddressNode( JcrAddress address, JcrNode addressesNode )
    {
        final JcrNode addressNode = addressesNode.addNode( ADDRESS );
        addressNode.setPropertyString( COUNTRY, address.getCountry() );
        addressNode.setPropertyString( ISO_COUNTRY, address.getIsoCountry() );
        addressNode.setPropertyString( ISO_REGION, address.getIsoRegion() );
        addressNode.setPropertyString( LABEL, address.getLabel() );
        addressNode.setPropertyString( POSTAL_ADDRESS, address.getPostalAddress() );
        addressNode.setPropertyString( POSTAL_CODE, address.getPostalCode() );
        addressNode.setPropertyString( REGION, address.getRegion() );
        addressNode.setPropertyString( STREET, address.getStreet() );
    }

    private JcrUserInfo toUserInfo( final JcrNode userNode )
    {
        final JcrUserInfo info = new JcrUserInfo();
        info.setBirthday( userNode.getPropertyDateTime( BIRTHDAY ) );
        info.setCountry( userNode.getPropertyString( COUNTRY ) );
        info.setDescription( userNode.getPropertyString( DESCRIPTION ) );
        info.setFax( userNode.getPropertyString( FAX ) );
        info.setFirstName( userNode.getPropertyString( FIRST_NAME ) );
        info.setGlobalPosition( userNode.getPropertyString( GLOBAL_POSITION ) );
        info.setHomePage( userNode.getPropertyString( HOME_PAGE ) );
        info.setHtmlEmail( userNode.getPropertyBoolean( HTML_EMAIL ) );
        info.setInitials( userNode.getPropertyString( INITIALS ) );
        info.setLastName( userNode.getPropertyString( LAST_NAME ) );
        info.setLocale( userNode.getPropertyString( LOCALE ) );
        info.setMemberId( userNode.getPropertyString( MEMBER_ID ) );
        info.setMiddleName( userNode.getPropertyString( MIDDLE_NAME ) );
        info.setMobile( userNode.getPropertyString( MOBILE ) );
        info.setOrganization( userNode.getPropertyString( ORGANIZATION ) );
        info.setPersonalId( userNode.getPropertyString( PERSONAL_ID ) );
        info.setPhone( userNode.getPropertyString( PHONE ) );
        info.setPrefix( userNode.getPropertyString( PREFIX ) );
        info.setSuffix( userNode.getPropertyString( SUFFIX ) );
        info.setTimeZone( userNode.getPropertyString( TIMEZONE ) );
        info.setTitle( userNode.getPropertyString( TITLE ) );
        info.setGender( Gender.fromName( userNode.getPropertyString( GENDER ) ) );
        info.setOrganization( userNode.getPropertyString( ORGANIZATION ) );
        return info;
    }

    private List<JcrAddress> nodePropertiesToAddresses( JcrNode userNode )
    {
        final List<JcrAddress> addressList = new ArrayList<JcrAddress>();
        final JcrNode addresses = userNode.getNode( ADDRESSES );
        JcrNodeIterator addressNodeIt = addresses.getNodes( ADDRESS );
        while ( addressNodeIt.hasNext() )
        {
            JcrNode addressNode = addressNodeIt.next();
            final JcrAddress address = new JcrAddress();
            address.setLabel( addressNode.getPropertyString( LABEL ) );
            address.setStreet( addressNode.getPropertyString( STREET ) );
            address.setPostalAddress( addressNode.getPropertyString( POSTAL_ADDRESS ) );
            address.setPostalCode( addressNode.getPropertyString( POSTAL_CODE ) );
            address.setRegion( addressNode.getPropertyString( REGION ) );
            address.setCountry( addressNode.getPropertyString( COUNTRY ) );
            address.setIsoRegion( addressNode.getPropertyString( ISO_REGION ) );
            address.setIsoCountry( addressNode.getPropertyString( ISO_COUNTRY ) );
            addressList.add( address );
        }
        return addressList;
    }

}
