package com.enonic.wem.core.account.dao;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.elasticsearch.common.collect.Sets;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.api.exception.UserStoreNotFoundException;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.statistics.UserStoreStatistics;
import com.enonic.wem.core.jcr.JcrConstants;
import com.enonic.wem.core.jcr.JcrHelper;

@Component("accountDao")
public final class AccountDaoImpl
    implements AccountDao
{
    private final UserStoreJcrMapping userStoreJcrMapping;

    private final AccountJcrMapping accountJcrMapping;

    public AccountDaoImpl()
    {
        userStoreJcrMapping = new UserStoreJcrMapping();
        accountJcrMapping = new AccountJcrMapping();
    }

    @Override
    public void createUserStore( final UserStore userStore, final Session session )
        throws Exception
    {
        final Node root = session.getRootNode();
        final Node userStoresNode = root.getNode( JcrConstants.USER_STORES_PATH );
        final String userStoreName = userStore.getName().toString();
        if ( ( userStoreName == null ) || userStoresNode.hasNode( userStoreName ) )
        {
            throw new IllegalArgumentException( "UserStore already exists: " + userStoreName );
        }

        final Node userStoreNode = userStoresNode.addNode( userStoreName, JcrConstants.USER_STORE_TYPE );
        userStoreJcrMapping.userStoreToJcr( userStore, userStoreNode );
        userStoreNode.addNode( JcrConstants.GROUPS_NODE, JcrConstants.GROUPS_TYPE );
        userStoreNode.addNode( JcrConstants.USERS_NODE, JcrConstants.USERS_TYPE );
        if ( userStore.getName().isSystem() )
        {
            userStoreNode.addNode( JcrConstants.ROLES_NODE, JcrConstants.ROLES_TYPE );
        }
    }

    @Override
    public void setUserStoreAdministrators( final UserStoreName userStoreName, final AccountKeys administrators, final Session session )
        throws Exception
    {
        final Node root = session.getRootNode();
        final Node userStoreNode = root.getNode( getNodePath( userStoreName ) );
        if ( userStoreNode == null )
        {
            throw new UserStoreNotFoundException( userStoreName );
        }

        final List<Node> adminsNodeList = Lists.newArrayList();
        for ( AccountKey administrator : administrators )
        {
            final Node accountNode = JcrHelper.getNodeOrNull( root, getNodePath( administrator ) );
            if ( accountNode == null )
            {
                throw new AccountNotFoundException( administrator );
            }
            adminsNodeList.add( accountNode );
        }

        final Node[] memberNodes = adminsNodeList.toArray( new Node[adminsNodeList.size()] );
        JcrHelper.setPropertyReference( userStoreNode, JcrConstants.USER_STORE_ADMINISTRATORS_PROPERTY, memberNodes );
    }

    @Override
    public AccountKeys getUserStoreAdministrators( final UserStoreName userStoreName, final Session session )
        throws Exception
    {
        final Node userStoreNode = getUserStoreNode( session, userStoreName );
        if ( userStoreNode == null )
        {
            throw new UserStoreNotFoundException( userStoreName );
        }

        final Node[] adminNodes = JcrHelper.getPropertyReferences( userStoreNode, JcrConstants.USER_STORE_ADMINISTRATORS_PROPERTY );
        final Set<AccountKey> administrators = Sets.newHashSet();
        for ( Node adminNode : adminNodes )
        {
            administrators.add( accountKeyFromAccountNode( adminNode ) );
        }
        return AccountKeys.from( administrators );
    }

    @Override
    public void createUser( final UserAccount user, final Session session )
        throws Exception
    {
        final AccountKey accountKey = user.getKey();
        final String parentPath = getParentNodePath( accountKey );
        final Node rootNode = session.getRootNode();
        final Node usersNode = JcrHelper.getNodeOrNull( rootNode, parentPath );

        final String userName = accountKey.getLocalName();
        if ( usersNode.hasNode( userName ) )
        {
            throw new IllegalArgumentException( "User already exists: " + user.getKey().toString() );
        }

        final Node userNode = usersNode.addNode( userName, JcrConstants.USER_TYPE );
        userNode.addNode( JcrConstants.USER_PROFILE_NODE, JcrConstants.USER_PROFILE_TYPE );
        accountJcrMapping.userToJcr( user, userNode );
    }

    @Override
    public void createGroup( final GroupAccount group, final Session session )
        throws Exception
    {
        final AccountKey accountKey = group.getKey();
        final String parentPath = getParentNodePath( accountKey );
        final Node rootNode = session.getRootNode();
        final Node groupsNode = JcrHelper.getNodeOrNull( rootNode, parentPath );

        final String groupName = accountKey.getLocalName();
        if ( groupsNode.hasNode( groupName ) )
        {
            throw new IllegalArgumentException( "Group already exists: " + group.getKey().toString() );
        }

        final Node groupNode = groupsNode.addNode( groupName, JcrConstants.GROUP_TYPE );
        groupNode.setProperty( JcrConstants.MEMBERS_PROPERTY, new Value[0] );
        accountJcrMapping.groupToJcr( group, groupNode );
    }

    @Override
    public void createRole( final RoleAccount role, final Session session )
        throws Exception
    {
        final AccountKey accountKey = role.getKey();
        final String parentPath = getParentNodePath( accountKey );
        final Node rootNode = session.getRootNode();
        final Node rolesNode = JcrHelper.getNodeOrNull( rootNode, parentPath );

        final String roleName = accountKey.getLocalName();
        if ( rolesNode.hasNode( roleName ) )
        {
            throw new IllegalArgumentException( "Role already exists: " + role.getKey().toString() );
        }

        final Node roleNode = rolesNode.addNode( roleName, JcrConstants.ROLE_TYPE );
        roleNode.setProperty( JcrConstants.MEMBERS_PROPERTY, new Value[0] );
        accountJcrMapping.roleToJcr( role, roleNode );
    }

    @Override
    public void setMembers( final AccountKey nonUserAccount, final AccountKeys members, final Session session )
        throws Exception
    {
        final Node accountNode = getAccountNode( session, nonUserAccount );
        if ( accountNode == null )
        {
            throw new AccountNotFoundException( nonUserAccount );
        }

        final List<Node> memberNodeList = Lists.newArrayList();
        final Node rootNode = session.getRootNode();
        for ( AccountKey member : members )
        {
            final String memberPath = getNodePath( member );
            final Node memberNode = JcrHelper.getNodeOrNull( rootNode, memberPath );
            if ( memberNode == null )
            {
                throw new AccountNotFoundException( member );
            }
            memberNodeList.add( memberNode );
        }

        final Node[] memberNodes = memberNodeList.toArray( new Node[memberNodeList.size()] );
        JcrHelper.setPropertyReference( accountNode, JcrConstants.MEMBERS_PROPERTY, memberNodes );
    }

    @Override
    public boolean deleteAccount( final AccountKey key, final Session session )
        throws Exception
    {
        final Node accountNode = getAccountNode( session, key );
        if ( accountNode == null )
        {
            return false;
        }

        accountNode.remove();
        return true;
    }

    @Override
    public boolean deleteUserStore( final UserStoreName name, final Session session )
        throws Exception
    {
        final Node userStoreNode = getUserStoreNode( session, name );
        if ( userStoreNode == null )
        {
            return false;
        }
        userStoreNode.remove();
        return true;
    }

    @Override
    public boolean accountExists( final AccountKey accountKey, final Session session )
        throws Exception
    {
        final Node accountNode = getAccountNode( session, accountKey );
        return accountNode != null;
    }

    @Override
    public UserAccount findUser( final AccountKey accountKey, final boolean includeProfile, final boolean includePhoto,
                                 final Session session )
        throws Exception
    {
        final Node accountNode = getAccountNode( session, accountKey );
        if ( accountNode == null )
        {
            return null;
        }

        final UserAccount user = UserAccount.create( accountKey );
        accountJcrMapping.toUser( accountNode, user, includeProfile, includePhoto );
        user.setEditable( true );
        return user;
    }

    @Override
    public GroupAccount findGroup( final AccountKey accountKey, final boolean includeMembers, final Session session )
        throws Exception
    {
        final Node accountNode = getAccountNode( session, accountKey );
        if ( accountNode == null )
        {
            return null;
        }

        final GroupAccount group = GroupAccount.create( accountKey );
        if ( includeMembers )
        {
            group.setMembers( getMembers( accountKey, session ) );
        }
        accountJcrMapping.toGroup( accountNode, group );
        group.setEditable( true );
        return group;
    }

    @Override
    public RoleAccount findRole( final AccountKey accountKey, final boolean includeMembers, final Session session )
        throws Exception
    {
        final Node accountNode = getAccountNode( session, accountKey );
        if ( accountNode == null )
        {
            return null;
        }

        final RoleAccount role = RoleAccount.create( accountKey );
        if ( includeMembers )
        {
            role.setMembers( getMembers( accountKey, session ) );
        }
        accountJcrMapping.toRole( accountNode, role );
        role.setEditable( true );
        return role;
    }

    @Override
    public Account findAccount( final AccountKey accountKey, final Session session )
        throws Exception
    {
        switch ( accountKey.getType() )
        {
            case USER:
                return findUser( accountKey, false, false, session );
            case GROUP:
                return findGroup( accountKey, false, session );
            case ROLE:
                return findRole( accountKey, false, session );
            default:
                return null;
        }
    }

    @Override
    public void updateUser( final UserAccount user, final Session session )
        throws Exception
    {
        final AccountKey accountKey = user.getKey();
        final Node userNode = getAccountNode( session, accountKey );
        if ( userNode == null )
        {
            throw new AccountNotFoundException( accountKey );
        }

        accountJcrMapping.userToJcr( user, userNode );
    }

    @Override
    public void updateGroup( final GroupAccount group, final Session session )
        throws Exception
    {
        final AccountKey accountKey = group.getKey();
        final Node groupNode = getAccountNode( session, accountKey );
        if ( groupNode == null )
        {
            throw new AccountNotFoundException( accountKey );
        }

        accountJcrMapping.groupToJcr( group, groupNode );
        if ( group.getMembers() != null )
        {
            setMembers( accountKey, group.getMembers(), session );
        }
    }

    @Override
    public void updateRole( final RoleAccount role, final Session session )
        throws Exception
    {
        final AccountKey accountKey = role.getKey();
        final Node roleNode = getAccountNode( session, accountKey );
        if ( roleNode == null )
        {
            throw new AccountNotFoundException( accountKey );
        }

        accountJcrMapping.roleToJcr( role, roleNode );
        if ( role.getMembers() != null )
        {
            setMembers( accountKey, role.getMembers(), session );
        }
    }

    @Override
    public UserStoreNames getUserStoreNames( final Session session )
        throws Exception
    {
        final Node root = session.getRootNode();
        final Node userStoresNode = root.getNode( JcrConstants.USER_STORES_PATH );

        final NodeIterator userStoreNodeIte = userStoresNode.getNodes();
        final Set<UserStoreName> userStoreNames = Sets.newHashSet();
        while ( userStoreNodeIte.hasNext() )
        {
            userStoreNames.add( UserStoreName.from( userStoreNodeIte.nextNode().getName() ) );
        }
        return UserStoreNames.from( userStoreNames );
    }

    @Override
    public AccountKeys getMembers( final AccountKey accountKey, final Session session )
        throws Exception
    {
        final Node accountNode = getAccountNode( session, accountKey );
        if ( accountNode == null )
        {
            throw new AccountNotFoundException( accountKey );
        }
        if ( accountKey.isUser() )
        {
            return AccountKeys.empty();
        }
        else
        {
            final Node[] memberNodes = JcrHelper.getPropertyReferences( accountNode, JcrConstants.MEMBERS_PROPERTY );
            final Set<AccountKey> members = Sets.newHashSet();
            for ( Node memberNode : memberNodes )
            {
                members.add( accountKeyFromAccountNode( memberNode ) );
            }
            return AccountKeys.from( members );
        }
    }

    @Override
    public UserStore getUserStore( final UserStoreName userStoreName, final boolean includeConfig, boolean includeStatistics,
                                   final Session session )
        throws Exception
    {
        final Node userStoreNode = getUserStoreNode( session, userStoreName );
        if ( userStoreNode == null )
        {
            throw new UserStoreNotFoundException( userStoreName );
        }

        final UserStore userStore = userStoreJcrMapping.toUserStore( userStoreNode, includeConfig );
        if ( includeStatistics )
        {
            final UserStoreStatistics statistics = new UserStoreStatistics();
            statistics.setNumUsers( Ints.checkedCast( userStoreNode.getNode( USERS_NODE ).getNodes().getSize() ) );
            statistics.setNumGroups( Ints.checkedCast( userStoreNode.getNode( GROUPS_NODE ).getNodes().getSize() ) );
            if ( userStoreName.isSystem() )
            {
                statistics.setNumRoles( Ints.checkedCast( userStoreNode.getNode( ROLES_NODE ).getNodes().getSize() ) );
            }
            userStore.setStatistics( statistics );
        }
        return userStore;
    }

    @Override
    public void updateUserStore( final UserStore userStore, final Session session )
        throws Exception
    {
        final UserStoreName userStoreName = userStore.getName();
        final Node userStoreNode = getUserStoreNode( session, userStoreName );
        if ( userStoreNode == null )
        {
            throw new UserStoreNotFoundException( userStoreName );
        }

        userStoreJcrMapping.userStoreToJcr( userStore, userStoreNode );
    }

    @Override
    public Collection<AccountKey> getAllAccountKeys( final Session session )
        throws Exception
    {
        final List<AccountKey> accounts = Lists.newArrayList();

        final Node rootNode = session.getRootNode();
        final Node userStoresNode = JcrHelper.getNodeOrNull( rootNode, ROOT_NODE );

        if ( userStoresNode == null )
        {
            return accounts;
        }

        final NodeIterator userStores = userStoresNode.getNodes();

        while ( userStores.hasNext() )
        {
            final Node userStoreNode = userStores.nextNode();

            final String userStoreName = userStoreNode.getName();

            final Node usersNode = JcrHelper.getNodeOrNull( userStoreNode, USERS_NODE );
            accounts.addAll( getAccountKeys( userStoreName, usersNode, AccountType.USER ) );

            final Node groupsNode = JcrHelper.getNodeOrNull( userStoreNode, GROUPS_NODE );
            accounts.addAll( getAccountKeys( userStoreName, groupsNode, AccountType.GROUP ) );

            final Node rolesNode = JcrHelper.getNodeOrNull( userStoreNode, ROLES_NODE );
            accounts.addAll( getAccountKeys( userStoreName, rolesNode, AccountType.ROLE ) );
        }

        return accounts;
    }

    private List<Account> getUsers( final String userStoreName, final Node usersNode )
        throws RepositoryException, IOException
    {
        final List<Account> users = Lists.newArrayList();

        final NodeIterator userNodeIterator = usersNode.getNodes();

        while ( userNodeIterator.hasNext() )
        {
            final Node userNode = userNodeIterator.nextNode();

            String userQName = userStoreName + ":" + userNode.getName();

            final UserAccount user = UserAccount.create( userQName );
            accountJcrMapping.toUser( userNode, user, true, false );
            user.setEditable( true );

            users.add( user );
        }

        return users;
    }


    private Collection<AccountKey> getAccountKeys( final String userStoreName, final Node accountsNode, final AccountType accountType )
        throws RepositoryException, IOException
    {
        final List<AccountKey> accountKeys = Lists.newArrayList();

        if ( accountsNode == null )
        {
            return accountKeys;
        }

        final NodeIterator accountsNodeIterator = accountsNode.getNodes();

        while ( accountsNodeIterator.hasNext() )
        {
            final Node userNode = accountsNodeIterator.nextNode();

            String userQName = userStoreName + ":" + userNode.getName();

            final AccountKey accountKey = AccountKey.from( accountType, userQName );

            accountKeys.add( accountKey );
        }

        return accountKeys;
    }


    private AccountKey accountKeyFromAccountNode( final Node accountNode )
        throws RepositoryException
    {
        final String name = accountNode.getName();
        final AccountType type = AccountType.valueOf( JcrHelper.getPropertyString( accountNode, "type" ) );
        final String userStore = accountNode.getParent().getParent().getName();
        switch ( type )
        {
            case USER:
                return AccountKey.user( userStore + ":" + name );
            case GROUP:
                return AccountKey.group( userStore + ":" + name );
            default:
                return AccountKey.role( userStore + ":" + name );
        }
    }

    private Node getAccountNode( final Session session, final AccountKey key )
        throws RepositoryException
    {
        final String path = getNodePath( key );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
    }

    private Node getUserStoreNode( final Session session, final UserStoreName name )
        throws RepositoryException
    {
        final String path = getNodePath( name );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
    }

    private String getNodePath( final UserStoreName userStoreName )
    {
        final StringBuilder str = new StringBuilder();
        str.append( JcrConstants.USER_STORES_PATH ).append( userStoreName.toString() );
        return str.toString();
    }

    private String getNodePath( final AccountKey key )
    {
        final StringBuilder str = new StringBuilder();
        str.append( getParentNodePath( key ) );
        str.append( "/" ).append( key.getLocalName() );
        return str.toString();
    }

    private String getParentNodePath( final AccountKey key )
    {
        final StringBuilder str = new StringBuilder();
        str.append( ROOT_NODE ).append( "/" );
        str.append( key.getUserStore() ).append( "/" );

        if ( key.isUser() )
        {
            str.append( USERS_NODE );
        }
        else if ( key.isGroup() )
        {
            str.append( GROUPS_NODE );
        }
        else
        {
            str.append( ROLES_NODE );
        }

        return str.toString();
    }
}
