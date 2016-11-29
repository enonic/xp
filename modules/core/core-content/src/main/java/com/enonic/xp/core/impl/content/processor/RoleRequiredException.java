package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.exception.BaseException;
import com.enonic.xp.security.PrincipalKey;

public class RoleRequiredException
    extends BaseException
{
    public RoleRequiredException( final PrincipalKey principalKey, final ContentPath contentPath, final PrincipalKey... roleKeys )
    {
        super( "User [{0}] is required to have at least one of the following roles [{1}] to edit [{2}]", principalKey, roleKeys,
               contentPath );
    }
}
