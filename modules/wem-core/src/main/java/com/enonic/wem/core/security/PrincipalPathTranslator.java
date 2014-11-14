package com.enonic.wem.core.security;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.repo.NodePath;

class PrincipalPathTranslator
{

    public static NodePath toParentPath( final PrincipalKey principalKey )
    {
        return NodePath.newPath().
            addElement( principalKey.getUserStore().toString() ).
            addElement( principalKey.getType().toString().toLowerCase() ).
            build();
    }

    public static NodePath toPath( final PrincipalKey principalKey )
    {
        return NodePath.newPath().
            addElement( principalKey.getUserStore().toString() ).
            addElement( principalKey.getType().toString().toLowerCase() ).
            addElement( principalKey.getId() ).
            build();
    }
}
