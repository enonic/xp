package com.enonic.wem.core.security;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.UserStoreKey;

class PrincipalPathTranslator
{

    public static NodePath toParentPath( final PrincipalKey principalKey )
    {
        final String userStorePart =
            principalKey.getUserStore() == null ? UserStoreKey.system().toString() : principalKey.getUserStore().toString();
        return NodePath.newPath().
            addElement( userStorePart ).
            addElement( principalKey.getType().toString().toLowerCase() ).
            build();
    }

    public static NodePath toPath( final PrincipalKey principalKey )
    {
        final String userStorePart =
            principalKey.getUserStore() == null ? UserStoreKey.system().toString() : principalKey.getUserStore().toString();
        return NodePath.newPath().
            addElement( userStorePart ).
            addElement( principalKey.getType().toString().toLowerCase() ).
            addElement( principalKey.getId() ).
            build();
    }
}
