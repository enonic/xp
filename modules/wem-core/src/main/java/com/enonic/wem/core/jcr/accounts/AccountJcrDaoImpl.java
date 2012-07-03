package com.enonic.wem.core.jcr.accounts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.jcr.JcrCallback;
import com.enonic.wem.core.jcr.JcrDaoSupport;
import com.enonic.wem.core.jcr.JcrNode;
import com.enonic.wem.core.jcr.JcrNodeIterator;
import com.enonic.wem.core.jcr.JcrProperty;
import com.enonic.wem.core.jcr.JcrPropertyIterator;
import com.enonic.wem.core.jcr.JcrSession;
import com.enonic.wem.core.jcr.JcrWemConstants;
import com.enonic.wem.core.jcr.PageList;

import static com.enonic.wem.core.jcr.JcrWemConstants.ACCOUNT_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrWemConstants.GROUPS_NODE;
import static com.enonic.wem.core.jcr.JcrWemConstants.GROUP_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrWemConstants.MEMBERS_NODE;
import static com.enonic.wem.core.jcr.JcrWemConstants.MEMBER_NODE;
import static com.enonic.wem.core.jcr.JcrWemConstants.USERSTORES_ABSOLUTE_PATH;
import static com.enonic.wem.core.jcr.JcrWemConstants.USERSTORES_PATH;
import static com.enonic.wem.core.jcr.JcrWemConstants.USERS_NODE;
import static com.enonic.wem.core.jcr.JcrWemConstants.USER_NODE_TYPE;

@Component
public class AccountJcrDaoImpl
    extends JcrDaoSupport
    implements AccountJcrDao
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountJcrDaoImpl.class );

    private final UserStoreJcrMapping userStoreJcrMapping;

    private final AccountJcrMapping accountJcrMapping;

    public AccountJcrDaoImpl()
    {
        userStoreJcrMapping = new UserStoreJcrMapping();
        accountJcrMapping = new AccountJcrMapping();
    }

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

    @Override
    public void saveAccount( final JcrAccount account )
    {
        switch ( account.getType() )
        {
            case USER:
                saveUser( (JcrUser) account );
                break;

            case GROUP:
                saveGroup( (JcrGroup) account );
                break;

            case ROLE:
                //saveRole(account);
                break;
        }
    }

    @Override
    public void deleteAccount( final JcrAccount account )
    {

    }

    @Override
    public void deleteAccount( final String accountId )
    {

    }

    @Override
    public JcrUserStore findUserStoreByName( String userStoreName )
    {
        return null;
    }

    @Override
    public void createUserStore( final JcrUserStore userStore )
    {
        getTemplate().execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                createUserStoreJcr( session, userStore );
                session.save();
                return null;
            }
        } );
    }

    @Override
    public void addMemberships( final String groupId, final Collection<String> memberIds )
    {
        getTemplate().execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                for ( String memberId : memberIds )
                {
                    addMembershipJcr( session, groupId, memberId );
                }
                session.save();
                return null;
            }
        } );
    }

    @Override
    public void addMembership( final String groupId, final String memberId )
    {
        getTemplate().execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                addMembershipJcr( session, groupId, memberId );
                session.save();
                return null;
            }
        } );
    }

    public void addMembershipJcr( JcrSession session, final String groupId, final String memberId )
    {
        final JcrNode groupNode = session.getNodeByIdentifier( groupId );
        if ( groupNode == null )
        {
            throw new IllegalArgumentException( "Could not find group with id: " + groupId );
        }
        final JcrNode memberNode = session.getNodeByIdentifier( memberId );
        if ( memberNode == null )
        {
            throw new IllegalArgumentException( "Could not find account with id: " + memberId );
        }

        final JcrNode membersNode = groupNode.getNode( MEMBERS_NODE );
        final JcrNode memberReferenceNode = membersNode.addNode( MEMBER_NODE );
        memberReferenceNode.setPropertyReference( "ref", memberNode );
    }

    public void createUserStoreJcr( JcrSession session, JcrUserStore userStore )
    {
        JcrNode userstoresNode = session.getRootNode().getNode( JcrWemConstants.USERSTORES_PATH );
        String userStoreName = userStore.getName();
        if ( ( userStoreName == null ) || userstoresNode.hasNode( userStoreName ) )
        {
            throw new IllegalArgumentException( "Unable to create UserStore with existing name: " + userStoreName );
        }

        JcrNode userstoreNode = userstoresNode.addNode( userStoreName, JcrWemConstants.USERSTORE_NODE_TYPE );
        userStoreJcrMapping.userStoreToJcr( userStore, userstoreNode );

        userstoreNode.addNode( JcrWemConstants.GROUPS_NODE, JcrWemConstants.GROUPS_NODE_TYPE );
        userstoreNode.addNode( JcrWemConstants.USERS_NODE, JcrWemConstants.USERS_NODE_TYPE );
        userstoreNode.addNode( JcrWemConstants.ROLES_NODE, JcrWemConstants.ROLES_NODE_TYPE );
    }

    private void saveGroup( final JcrGroup group )
    {
        getTemplate().execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                if ( group.getId() == null )
                {
                    insertGroupJcr( session, group );
                }
                else
                {
                    updateGroupJcr( session, group );
                }
                session.save();
                return null;
            }
        } );
    }

    private void updateGroupJcr( final JcrSession session, final JcrGroup group )
    {
        final JcrNode groupNode = session.getNodeByIdentifier( group.getId() );
        if ( groupNode == null )
        {
            throw new IllegalArgumentException( "Could not find group with id: " + group.getId() );
        }
        accountJcrMapping.groupToJcr( group, groupNode );
        group.setId( groupNode.getIdentifier() );
    }

    private void insertGroupJcr( final JcrSession session, final JcrGroup group )
    {
        final String groupName = group.getName();
        final String userstoreName = group.getUserStore();
        if ( userstoreName == null )
        {
            throw new IllegalArgumentException( "Undefined userstore in group" );
        }
        final String userParentNodePath = USERSTORES_PATH + userstoreName + "/" + GROUPS_NODE;
        final JcrNode userStoreNode = session.getRootNode().getNode( userParentNodePath );
        if ( userStoreNode.hasNode( groupName ) )
        {
            throw new IllegalArgumentException( "Group already exists in userstore: " + userstoreName + "//" + groupName );
        }
        final JcrNode groupNode = userStoreNode.addNode( groupName, GROUP_NODE_TYPE );
        accountJcrMapping.groupToJcr( group, groupNode );
        groupNode.addNode( MEMBERS_NODE );
        group.setId( groupNode.getIdentifier() );
    }

    private void saveUser( final JcrUser user )
    {
        getTemplate().execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                if ( user.getId() == null )
                {
                    insertUserJcr( session, user );
                }
                else
                {
                    updateUserJcr( session, user );
                }
                session.save();
                return null;
            }
        } );
    }

    private void insertUserJcr( final JcrSession session, final JcrUser user )
    {
        final String userName = user.getName();
        final String userstoreName = user.getUserStore();
        if ( userstoreName == null )
        {
            throw new IllegalArgumentException( "Undefined userstore in user" );
        }

        final String userParentNodePath = USERSTORES_PATH + userstoreName + "/" + USERS_NODE;
        final JcrNode userStoreNode = session.getRootNode().getNode( userParentNodePath );
        if ( userStoreNode.hasNode( userName ) )
        {
            throw new IllegalArgumentException( "User already exists in userstore: " + userstoreName + "//" + userName );
        }
        final JcrNode userNode = userStoreNode.addNode( userName, USER_NODE_TYPE );
        accountJcrMapping.userToJcr( user, userNode );
        user.setId( userNode.getIdentifier() );
    }

    private void updateUserJcr( final JcrSession session, final JcrUser user )
    {
        final JcrNode userNode = session.getNodeByIdentifier( user.getId() );
        if ( userNode == null )
        {
            throw new IllegalArgumentException( "Could not find user with id: " + user.getId() );
        }
        accountJcrMapping.userToJcr( user, userNode );
        user.setId( userNode.getIdentifier() );
    }

    private PageList<JcrAccount> queryAllAccounts( JcrSession session, int index, int count )
        throws RepositoryException
    {
        final JcrNodeIterator nodeIterator = session.createQuery()
            .selectNodeType( ACCOUNT_NODE_TYPE )
            .from( USERSTORES_ABSOLUTE_PATH )
            .offset( index )
            .limit( count )
            .execute();

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
        final JcrNodeIterator nodeIterator = session.createQuery()
            .selectNodeType( USER_NODE_TYPE )
            .from( USERSTORES_ABSOLUTE_PATH )
            .propertyEqualsTo( "key", userId )
            .execute();

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
        final JcrNodeIterator nodeIterator = session.createQuery()
            .selectNodeType( GROUP_NODE_TYPE )
            .from( USERSTORES_ABSOLUTE_PATH )
            .propertyEqualsTo( "key", groupId )
            .execute();

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
        final JcrNodeIterator nodeIterator = session.createQuery()
            .selectNodeType( USER_NODE_TYPE )
            .from( USERSTORES_ABSOLUTE_PATH )
            .propertyEqualsTo( "key", userId )
            .execute();

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

        final JcrUserStore userstore = userStoreJcrMapping.toUserStore( userNode.getParent().getParent() );
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

        final JcrUserStore userstore = userStoreJcrMapping.toUserStore( groupNode.getParent().getParent() );
        group.setUserStore( userstore.getName() );
    }

}
