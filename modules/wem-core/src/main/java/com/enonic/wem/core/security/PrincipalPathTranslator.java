package com.enonic.wem.core.security;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.UserStoreKey;

class PrincipalPathTranslator
{
    private final static String ROLES_NODE_NAME = "roles";

    public static NodePath toParentPath( final PrincipalKey principalKey )
    {
        if ( principalKey.isRole() )
        {
            return NodePath.newPath().
                addElement( ROLES_NODE_NAME ).
                build();
        }
        else
        {
            return NodePath.newPath().
                addElement( UserStoreKey.system().toString() ).
                addElement( principalKey.getType().toString().toLowerCase() ).
                build();
        }
    }

    public static NodePath toPath( final PrincipalKey principalKey )
    {
        if ( principalKey.isRole() )
        {
            return NodePath.newPath().
                addElement( ROLES_NODE_NAME ).
                addElement( principalKey.getId() ).
                build();
        }
        else
        {
            return NodePath.newPath().
                addElement( UserStoreKey.system().toString() ).
                addElement( principalKey.getType().toString().toLowerCase() ).
                addElement( principalKey.getId() ).
                build();
        }
    }
}
