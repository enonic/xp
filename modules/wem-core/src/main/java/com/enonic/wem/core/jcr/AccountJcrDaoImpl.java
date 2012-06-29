package com.enonic.wem.core.jcr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.jcr.accounts.Gender;
import com.enonic.wem.core.jcr.accounts.JcrAccount;
import com.enonic.wem.core.jcr.accounts.JcrAccountType;
import com.enonic.wem.core.jcr.accounts.JcrAddress;
import com.enonic.wem.core.jcr.accounts.JcrGroup;
import com.enonic.wem.core.jcr.accounts.JcrUser;
import com.enonic.wem.core.jcr.accounts.JcrUserInfo;
import com.enonic.wem.core.jcr.accounts.JcrUserStore;

import static com.enonic.wem.core.jcr.JcrCmsConstants.ACCOUNT_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.GROUP_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.MEMBERS_NODE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.MEMBER_NODE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USERSTORES_ABSOLUTE_PATH;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USER_NODE_TYPE;

@Component
public class AccountJcrDaoImpl
    extends JcrDaoSupport
    implements AccountJcrDao
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountJcrDaoImpl.class );

    @Override
    public JcrUser findUserById( final String accountId )
    {
        JcrUser user = (JcrUser) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryUserById( session, accountId, true );
            }
        } );
        return user;
    }

    @Override
    public JcrGroup findGroupById( final String accountId )
    {
        JcrGroup group = (JcrGroup) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryGroupById( session, accountId, true );
            }
        } );
        return group;

    }

    @Override
    public PageList<JcrAccount> findAll( final int index, final int count )
    {
        @SuppressWarnings("unchecked") PageList<JcrAccount> accounts = (PageList<JcrAccount>) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryAllAccounts( session, index, count );
            }
        } );
        return accounts;
    }

    @Override
    public byte[] findUserPhotoByKey( final String userId )
    {
        byte[] photo = (byte[]) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryUserPhotoByKey( session, userId );
            }
        } );
        return photo;
    }

    private PageList<JcrAccount> queryAllAccounts( JcrSession session, int index, int count )
        throws RepositoryException
    {
        final JcrQuery query = new JcrQuery( session )
            .descendantOf( USERSTORES_ABSOLUTE_PATH )
            .selectNodeType( ACCOUNT_NODE_TYPE )
            .offset( index )
            .limit( count );
        final JcrNodeIterator nodeIterator = session.execute( query );

        LOG.info( nodeIterator.getSize() + " accounts found" );

        final List<JcrAccount> userList = new ArrayList<JcrAccount>();
        while ( nodeIterator.hasNext() )
        {
            final JcrNode userNode = nodeIterator.nextNode();
            final JcrUser user = new JcrUser();
            nodePropertiesToUserFields( userNode, user );
            userList.add( user );
        }
        return new PageList<JcrAccount>( index, (int) nodeIterator.getSize(), userList );
    }

    private JcrUser queryUserById( final JcrSession session, final String userId, final boolean includeMemberships )
        throws RepositoryException, IOException
    {
        final JcrQuery query = new JcrQuery( session )
            .descendantOf( USERSTORES_ABSOLUTE_PATH )
            .selectNodeType( USER_NODE_TYPE )
            .propertyEqualsTo( "key", userId );
        final JcrNodeIterator nodeIterator = session.execute( query );

        if ( nodeIterator.hasNext() )
        {
            final JcrNode userNode = nodeIterator.nextNode();
            return buildUser( session, userNode, includeMemberships );
        }
        else
        {
            return null;
        }
    }

    private JcrUser buildUser( JcrSession session, JcrNode userNode, boolean includeMemberships )
        throws RepositoryException, IOException
    {
        final JcrUser user = new JcrUser();
        nodePropertiesToUserFields( userNode, user );
        if ( includeMemberships )
        {
            setUserMemberships( session, userNode, user );
        }
        return user;
    }

    private void setUserMemberships( JcrSession session, JcrNode userNode, JcrUser user )
        throws RepositoryException, IOException
    {
        final JcrPropertyIterator refIterator = userNode.getReferences();
        while ( refIterator.hasNext() )
        {
            final JcrProperty property = refIterator.next();
            final JcrNode memberOwnerNode = property.getParent();
            final JcrNode groupNode = memberOwnerNode.getParent().getParent();

            final String groupId = groupNode.getPropertyString( "key" );
            final JcrGroup group = this.queryGroupById( session, groupId, false );
            if ( group != null )
            {
                user.addMembership( group );
            }
            else
            {
                LOG.warn( "Could not find group with id '" + groupId + "'" );
            }
        }
    }

    private JcrGroup queryGroupById( JcrSession session, String groupId, boolean includeMembers )
        throws RepositoryException, IOException
    {
        final JcrQuery query = new JcrQuery( session )
            .descendantOf( USERSTORES_ABSOLUTE_PATH )
            .selectNodeType( GROUP_NODE_TYPE )
            .propertyEqualsTo( "key", groupId );
        final JcrNodeIterator nodeIterator = session.execute( query );

        if ( nodeIterator.hasNext() )
        {
            final JcrNode groupNode = nodeIterator.nextNode();
            return buildGroup( session, groupNode, includeMembers );
        }
        else
        {
            return null;
        }
    }

    private JcrAccount buildAccount( JcrSession session, JcrNode accountNode, boolean includeMemberships )
        throws RepositoryException, IOException
    {
        final String accountType = accountNode.getPropertyString( "type" );
        final JcrAccountType type = JcrAccountType.fromName( accountType );
        switch ( type )
        {
            case USER:
                return buildUser( session, accountNode, includeMemberships );

            case ROLE:
            case GROUP:
                return buildGroup( session, accountNode, includeMemberships );
        }

        throw new IllegalArgumentException( "Invalid account type: " + accountType );
    }

    private JcrGroup buildGroup( JcrSession session, JcrNode groupNode, boolean includeMembers )
        throws RepositoryException, IOException
    {
        final JcrGroup group = new JcrGroup();
        nodePropertiesToGroupFields( groupNode, group );
        if ( includeMembers )
        {
            setGroupMembers( session, groupNode, group );
        }
        return group;
    }

    private void setGroupMembers( JcrSession session, JcrNode groupNode, JcrGroup group )
        throws RepositoryException, IOException
    {
        final JcrNodeIterator memberIterator = groupNode.getNode( MEMBERS_NODE ).getNodes( MEMBER_NODE );
        while ( memberIterator.hasNext() )
        {
            final JcrNode memberRef = memberIterator.next();
            final JcrNode groupMember = memberRef.getProperty( "ref" ).getNode();
            final JcrAccount member = buildAccount( session, groupMember, false );
            group.addMember( member );
        }
    }

    private byte[] queryUserPhotoByKey( JcrSession session, String userId )
        throws RepositoryException, IOException
    {
        final JcrQuery query = new JcrQuery( session )
            .descendantOf( USERSTORES_ABSOLUTE_PATH )
            .selectNodeType( USER_NODE_TYPE )
            .propertyEqualsTo( "key", userId );
        final JcrNodeIterator nodeIterator = session.execute( query );

        if ( nodeIterator.hasNext() )
        {
            JcrNode userNode = nodeIterator.nextNode();
            if ( userNode.hasProperty( "photo" ) )
            {
                return userNode.getPropertyBinary( "photo" );
            }
        }
        return null;
    }

    private void nodePropertiesToUserFields( JcrNode userNode, JcrUser user )
        throws RepositoryException
    {
        user.setName( userNode.getName() );
        user.setDisplayName( userNode.getPropertyString( "displayname" ) );
        user.setEmail( userNode.getPropertyString( "email" ) );
        user.setId( userNode.getPropertyString( "key" ) );
        user.setLastModified( userNode.getPropertyDateTime( "lastModified" ) );
        if ( userNode.hasProperty( "photo" ) )
        {
            user.setHasPhoto( true );
        }

//        user.setType( userTypeFromString( userNode.getProperty( "userType" ).getString() ) );

        final JcrUserStore userstore = nodeToUserstore( userNode.getParent().getParent() );
        user.setUserStore( userstore.getName() );

        final JcrUserInfo userInfo = nodePropertiesToUserFields( userNode );
        final List<JcrAddress> addresses = nodePropertiesToAddresses( userNode );
        userInfo.setAddresses( addresses );
        user.setUserInfo( userInfo );
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

    private JcrUserInfo nodePropertiesToUserFields( final JcrNode userNode )
        throws RepositoryException
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

    private void nodePropertiesToGroupFields( JcrNode groupNode, JcrGroup group )
        throws RepositoryException
    {
        group.setName( groupNode.getName() );
        if ( groupNode.hasProperty( "description" ) )
        {
            group.setDescription( groupNode.getPropertyString( "description" ) );
        }
        group.setId( groupNode.getPropertyString( "key" ) );
//        group.setType( groupTypeFromString( (int) groupNode.getProperty( "groupType" ).getLong() ) );
//        group.setRestricted( true );

        final JcrUserStore userstore = nodeToUserstore( groupNode.getParent().getParent() );
        group.setUserStore( userstore.getName() );
    }

    private JcrUserStore nodeToUserstore( JcrNode userStoreNode )
        throws RepositoryException
    {
        final JcrUserStore userStore = new JcrUserStore();
        userStore.setName( userStoreNode.getName() );
        userStore.setId( userStoreNode.getPropertyString( "key" ) );
        userStore.setDefaultStore( userStoreNode.getPropertyBoolean( "default" ) );
        userStore.setConnectorName( userStoreNode.getPropertyString( "connector" ) );
        userStore.setXmlConfig( userStoreNode.getPropertyString( "xmlconfig" ) );
        return userStore;
    }
}
