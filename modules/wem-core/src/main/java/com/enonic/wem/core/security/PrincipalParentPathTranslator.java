package com.enonic.wem.core.security;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.core.entity.NodePath;

class PrincipalParentPathTranslator
{

    public static NodePath toParentPath( final PrincipalKey principalKey )
    {
        return NodePath.newPath().
            addElement( principalKey.getUserStore().toString() ).
            addElement( principalKey.getType().toString() ).
            build();
    }

}
