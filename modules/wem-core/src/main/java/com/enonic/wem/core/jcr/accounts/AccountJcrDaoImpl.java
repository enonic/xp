package com.enonic.wem.core.jcr.accounts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.primitives.Ints;

import com.enonic.wem.core.jcr.JcrCallback;
import com.enonic.wem.core.jcr.JcrDaoSupport;
import com.enonic.wem.core.jcr.JcrNode;
import com.enonic.wem.core.jcr.JcrNodeIterator;
import com.enonic.wem.core.jcr.JcrProperty;
import com.enonic.wem.core.jcr.JcrPropertyIterator;
import com.enonic.wem.core.jcr.JcrSession;
import com.enonic.wem.core.jcr.JcrWemConstants;

import static com.enonic.wem.core.jcr.JcrWemConstants.ACCOUNT_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrWemConstants.GROUPS_NODE;
import static com.enonic.wem.core.jcr.JcrWemConstants.GROUP_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrWemConstants.MEMBERS_NODE;
import static com.enonic.wem.core.jcr.JcrWemConstants.MEMBER_NODE;
import static com.enonic.wem.core.jcr.JcrWemConstants.ROLES_NODE;
import static com.enonic.wem.core.jcr.JcrWemConstants.ROLE_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrWemConstants.USERSTORES_ABSOLUTE_PATH;
import static com.enonic.wem.core.jcr.JcrWemConstants.USERSTORES_PATH;
import static com.enonic.wem.core.jcr.JcrWemConstants.USERSTORE_NODE_TYPE;
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
                return queryUserById( session, accountId );
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
    public JcrRole findRoleById( final String accountId )
    {
        JcrRole role = (JcrRole) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryRoleById( session, accountId, true );
            }
        } );
        return role;
    }

    @Override
    public JcrAccount findAccountById( final String accountId )
    {
        JcrAccount account = (JcrAccount) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryAccountById( session, accountId );
            }
        } );
        return account;
    }

    @Override
    public int getGroupsCount()
    {
        Integer count = (Integer) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryGroupsCount( session );
            }
        } );
        return count;
    }

    @Override
    public int getUsersCount()
    {
        Integer count = (Integer) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryUsersCount( session );
            }
        } );
        return count;
    }

    @Override
    public List<JcrAccount> findAll( final int from, final int count )
    {
        @SuppressWarnings("unchecked")
        List<JcrAccount> accounts = (List<JcrAccount>) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryAllAccounts( session, from, count );
            }
        } );
        return accounts;
    }

    @Override
    public List<JcrUser> findAllUsers( final int from, final int count )
    {
        @SuppressWarnings("unchecked")
        List<JcrUser> users = (List<JcrUser>) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryAllUsers( session, from, count );
            }
        } );
        return users;
    }

    @Override
    public List<JcrGroup> findAllGroups( final int from, final int count )
    {
        @SuppressWarnings("unchecked")
        List<JcrGroup> groups = (List<JcrGroup>) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryAllGroups( session, from, count );
            }
        } );
        return groups;
    }

    @Override
    public byte[] findUserPhotoById( final String userId )
    {
        byte[] photo = (byte[]) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryUserPhotoById( session, userId );
            }
        } );
        return photo;
    }

    @Override
    public void saveAccount( final JcrAccount account )
    {
        switch ( account.getAccountType() )
        {
            case USER:
                saveUser( (JcrUser) account );
                break;

            case GROUP:
                saveGroup( (JcrGroup) account );
                break;

            case ROLE:
                saveRole( (JcrRole) account );
                break;
        }
    }

    @Override
    public void deleteAccount( final JcrAccount account )
    {
        deleteAccount( account.getId() );
    }

    @Override
    public void deleteAccount( final String accountId )
    {
        getTemplate().execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                deleteAccountJcr( session, accountId );
                session.save();
                return null;
            }
        } );
    }

    private void deleteAccountJcr( final JcrSession session, final String accountId )
    {
        final JcrNode accountNode = session.getNodeByIdentifier( accountId );
        if ( ( accountNode != null ) && ( accountNode.isNodeType( ACCOUNT_NODE_TYPE ) ) )
        {
            accountNode.remove();
        }
    }

    @Override
    public JcrUserStore findUserStoreByName( final String userStoreName )
    {
        JcrUserStore userStore = (JcrUserStore) getTemplate().execute( new JcrCallback()
        {
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                return queryUserstoreByName( session, userStoreName );
            }
        } );
        return userStore;
    }

    private JcrUserStore queryUserstoreByName( final JcrSession session, final String userStoreName )
    {
        final JcrNodeIterator nodeIterator = session.createQuery()
            .selectNodeType( USERSTORE_NODE_TYPE )
            .withName( userStoreName )
            .execute();

        if ( nodeIterator.hasNext() )
        {
            final JcrNode userStoreNode = nodeIterator.next();
            return userStoreJcrMapping.toUserStore( userStoreNode );
        }
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

    private void addMembershipJcr( JcrSession session, final String groupId, final String memberId )
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
        memberReferenceNode.setPropertyReference( AccountJcrMapping.MEMBER_REFERENCE, memberNode );
    }

    private void createUserStoreJcr( JcrSession session, JcrUserStore userStore )
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

    private void saveRole( final JcrRole role )
    {
        getTemplate().execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( final JcrSession session )
                throws IOException, RepositoryException
            {
                if ( role.getId() == null )
                {
                    insertRoleJcr( session, role );
                }
                else
                {
                    updateRoleJcr( session, role );
                }
                session.save();
                return null;
            }
        } );
    }

    private void updateRoleJcr( final JcrSession session, final JcrRole role )
    {
        final JcrNode roleNode = session.getNodeByIdentifier( role.getId() );
        if ( roleNode == null )
        {
            throw new IllegalArgumentException( "Could not find role with id: " + role.getId() );
        }
        accountJcrMapping.roleToJcr( role, roleNode );
        role.setId( roleNode.getIdentifier() );
    }

    private void insertRoleJcr( final JcrSession session, final JcrRole role )
    {
        final String roleName = role.getName();
        final String userstoreName = role.getUserStore();
        if ( userstoreName == null )
        {
            throw new IllegalArgumentException( "Undefined userstore for role" );
        }
        final String userParentNodePath = USERSTORES_PATH + userstoreName + "/" + ROLES_NODE;
        final JcrNode userStoreNode = session.getRootNode().getNode( userParentNodePath );
        if ( userStoreNode.hasNode( roleName ) )
        {
            throw new IllegalArgumentException( "Role already exists in userstore: " + userstoreName + "//" + roleName );
        }
        final JcrNode roleNode = userStoreNode.addNode( roleName, ROLE_NODE_TYPE );
        accountJcrMapping.roleToJcr( role, roleNode );
        roleNode.addNode( MEMBERS_NODE );
        role.setId( roleNode.getIdentifier() );
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
            throw new IllegalArgumentException( "Undefined userstore for group" );
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
            throw new IllegalArgumentException( "Undefined userstore for user" );
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

    private List<JcrAccount> queryAllAccounts( JcrSession session, int index, int count )
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
            final JcrUser user = accountJcrMapping.toUser( userNode );
            userList.add( user );
        }
        return userList;
    }

    private List<JcrUser> queryAllUsers( JcrSession session, int index, int count )
    {
        final JcrNodeIterator nodeIterator = session.createQuery()
            .selectNodeType( USER_NODE_TYPE )
            .from( USERSTORES_ABSOLUTE_PATH )
            .offset( index )
            .limit( count )
            .execute();

        LOG.info( nodeIterator.getSize() + " users found" );

        final List<JcrUser> userList = new ArrayList<JcrUser>();
        while ( nodeIterator.hasNext() )
        {
            final JcrNode userNode = nodeIterator.nextNode();
            final JcrUser user = accountJcrMapping.toUser( userNode );
            userList.add( user );
        }
        return userList;
    }

    private List<JcrGroup> queryAllGroups( JcrSession session, int index, int count )
    {
        final JcrNodeIterator nodeIterator = session.createQuery()
            .selectNodeType( GROUP_NODE_TYPE )
            .from( USERSTORES_ABSOLUTE_PATH )
            .offset( index )
            .limit( count )
            .execute();

        LOG.info( nodeIterator.getSize() + " groups found" );

        final List<JcrGroup> groupList = new ArrayList<JcrGroup>();
        while ( nodeIterator.hasNext() )
        {
            final JcrNode groupNode = nodeIterator.nextNode();
            LOG.info( groupNode.getName() + " group found" );
            if ( groupNode.isNodeType( ROLE_NODE_TYPE ) )
            {
                final JcrRole role = accountJcrMapping.toRole( groupNode );
                groupList.add( role );
            }
            else
            {
                final JcrGroup group = accountJcrMapping.toGroup( groupNode );
                groupList.add( group );
            }
        }
        return groupList;
    }

    private int queryUsersCount( JcrSession session )
    {
        final JcrNodeIterator nodeIterator = session.createQuery()
            .selectNodeType( USER_NODE_TYPE )
            .from( USERSTORES_ABSOLUTE_PATH )
            .execute();

        return Ints.saturatedCast( nodeIterator.getSize() );
    }

    private int queryGroupsCount( JcrSession session )
    {
        final JcrNodeIterator nodeIterator = session.createQuery()
        .selectNodeType( GROUP_NODE_TYPE )
        .from( USERSTORES_ABSOLUTE_PATH )
        .execute();

        return Ints.saturatedCast( nodeIterator.getSize() );
    }

    private JcrUser queryUserById( final JcrSession session, final String userId )
    {
        final JcrNode userNode = session.getNodeByIdentifier( userId );
        if ( ( userNode != null ) && ( userNode.isNodeType( USER_NODE_TYPE ) ) )
        {
            return buildUser( session, userNode, true );
        }
        else
        {
            return null;
        }
    }

    private JcrUser buildUser( JcrSession session, JcrNode userNode, boolean includeMemberships )
    {
        final JcrUser user = accountJcrMapping.toUser( userNode );
        if ( includeMemberships )
        {
            setUserMemberships( session, userNode, user );
        }
        return user;
    }

    private void setUserMemberships( JcrSession session, JcrNode userNode, JcrUser user )
    {
        final JcrPropertyIterator refIterator = userNode.getReferences();
        while ( refIterator.hasNext() )
        {
            final JcrProperty property = refIterator.next();
            final JcrNode memberOwnerNode = property.getParent();
            final JcrNode groupNode = memberOwnerNode.getParent().getParent();

            final String groupId = groupNode.getIdentifier();
            // TODO: roles
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

    private JcrAccount queryAccountById( JcrSession session, String accountId )
    {
        final JcrNode accountNode = session.getNodeByIdentifier( accountId );
        if ( accountNode != null )
        {
            if ( accountNode.isNodeType( USER_NODE_TYPE ) )
            {
                return queryUserById( session, accountId );
            }
            else if ( accountNode.isNodeType( ROLE_NODE_TYPE ) )
            {
                return queryRoleById( session, accountId, true );
            }
            else if ( accountNode.isNodeType( GROUP_NODE_TYPE ) )
            {
                return queryGroupById( session, accountId, true );
            }
        }
        return null;
    }

    private JcrRole queryRoleById( JcrSession session, String roleId, boolean includeMembers )
    {
        final JcrNode roleNode = session.getNodeByIdentifier( roleId );
        if ( ( roleNode != null ) && ( roleNode.isNodeType( ROLE_NODE_TYPE ) ) )
        {
            return buildRole( session, roleNode, includeMembers );
        }
        else
        {
            return null;
        }
    }

    private JcrGroup queryGroupById( JcrSession session, String groupId, boolean includeMembers )
    {
        final JcrNode groupNode = session.getNodeByIdentifier( groupId );
        if ( ( groupNode != null ) && ( groupNode.isNodeType( GROUP_NODE_TYPE ) ) )
        {
            return buildGroup( session, groupNode, includeMembers );
        }
        else
        {
            return null;
        }
    }

    private JcrAccount buildAccount( JcrSession session, JcrNode accountNode )
    {
        final String accountType = accountNode.getPropertyString( AccountJcrMapping.TYPE );
        final JcrAccountType type = JcrAccountType.fromName( accountType );
        switch ( type )
        {
            case USER:
                return buildUser( session, accountNode, false );
            case ROLE:
                return buildRole( session, accountNode, false );
            case GROUP:
                return buildGroup( session, accountNode, false );
        }

        throw new IllegalArgumentException( "Invalid account type: " + accountType );
    }

    private JcrRole buildRole( JcrSession session, JcrNode roleNode, boolean includeMembers )
    {
        final JcrRole role = accountJcrMapping.toRole( roleNode );
        if ( includeMembers )
        {
            setGroupMembers( session, roleNode, role );
        }
        return role;
    }

    private JcrGroup buildGroup( JcrSession session, JcrNode groupNode, boolean includeMembers )
    {
        final JcrGroup group = accountJcrMapping.toGroup( groupNode );
        if ( includeMembers )
        {
            setGroupMembers( session, groupNode, group );
        }
        return group;
    }

    private void setGroupMembers( JcrSession session, JcrNode groupNode, JcrGroup group )
    {
        final JcrNodeIterator memberIterator = groupNode.getNode( MEMBERS_NODE ).getNodes( MEMBER_NODE );
        while ( memberIterator.hasNext() )
        {
            final JcrNode memberRef = memberIterator.next();
            final JcrNode groupMember = memberRef.getProperty( AccountJcrMapping.MEMBER_REFERENCE ).getNode();
            final JcrAccount member = buildAccount( session, groupMember );
            group.addMember( member );
        }
    }

    private byte[] queryUserPhotoById( JcrSession session, String userId )
    {
        final JcrNode userNode = session.getNodeByIdentifier( userId );
        if ( ( userNode != null ) && ( userNode.isNodeType( USER_NODE_TYPE ) ) )
        {
            if ( userNode.hasProperty( AccountJcrMapping.PHOTO ) )
            {
                return userNode.getPropertyBinary( AccountJcrMapping.PHOTO );
            }
        }
        return null;
    }

}
