package com.enonic.wem.core.account.dao;

import javax.jcr.Node;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.core.jcr.JcrHelper;

@Component
public final class AccountDaoImpl
    implements AccountDao
{
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

        str.append( "/" ).append( key.getLocalName() );
        return str.toString();
    }
}
