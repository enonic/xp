package com.enonic.wem.core.account.dao;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.core.jcr.JcrConstants;
import com.enonic.wem.core.jcr.JcrHelper;

@Component
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
    public void createUserStore( final Session session, final UserStore userStore )
        throws Exception
    {
        Node userStoresNode = session.getRootNode().getNode( JcrConstants.USER_STORES_PATH );
        final String userStoreName = userStore.getName().toString();
        if ( ( userStoreName == null ) || userStoresNode.hasNode( userStoreName ) )
        {
            throw new IllegalArgumentException( "UserStore already exists: " + userStoreName );
        }

        final Node userStoreNode = userStoresNode.addNode( userStoreName, JcrConstants.USER_STORE_TYPE );
        userStoreJcrMapping.userStoreToJcr( userStore, userStoreNode );
        userStoreNode.addNode( JcrConstants.GROUPS_NODE, JcrConstants.GROUPS_TYPE );
        userStoreNode.addNode( JcrConstants.USERS_NODE, JcrConstants.USERS_TYPE );
        userStoreNode.addNode( JcrConstants.ROLES_NODE, JcrConstants.ROLES_TYPE );
    }

    @Override
    public void createUser( final Session session, final UserAccount user )
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
    public void createGroup( final Session session, final GroupAccount group )
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
    public void createRole( final Session session, final RoleAccount role )
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
    public void setMembers( final Session session, final AccountKey nonUserAccount, final AccountKeys members )
        throws Exception
    {
        final String path = getNodePath( nonUserAccount );
        final Node rootNode = session.getRootNode();
        final Node accountNode = JcrHelper.getNodeOrNull( rootNode, path );

        if ( accountNode == null )
        {
            throw new AccountNotFoundException( nonUserAccount );
        }

        final List<Node> memberNodeList = Lists.newArrayList();
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
    public boolean delete( final Session session, final AccountKey key )
        throws Exception
    {
        final String path = getNodePath( key );
        final Node rootNode = session.getRootNode();
        final Node accountNode = JcrHelper.getNodeOrNull( rootNode, path );

        if ( accountNode == null )
        {
            return false;
        }

        accountNode.remove();
        return true;
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
