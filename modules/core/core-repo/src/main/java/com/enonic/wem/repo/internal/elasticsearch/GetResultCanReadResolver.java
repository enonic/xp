package com.enonic.wem.repo.internal.elasticsearch;

import java.util.List;

import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

class GetResultCanReadResolver
{
    public static boolean canRead( final PrincipalKeys principalsKeys, final GetResult getResult )
    {
        if ( principalsKeys.contains( RoleKeys.ADMIN ) )
        {
            return true;
        }

        final SearchResultFieldValue hasRead = getResult.getSearchResult().getField( NodeIndexPath.PERMISSIONS_READ.getPath() );

        if ( hasRead == null )
        {
            return false;
        }

        final List<Object> values = hasRead.getValues();

        final PrincipalKeys keys = principalsKeys.isEmpty() ? PrincipalKeys.from( PrincipalKey.ofAnonymous() ) : principalsKeys;

        for ( final Object value : values )
        {
            if ( keys.contains( PrincipalKey.from( value.toString() ) ) )
            {
                return true;
            }
        }

        return false;
    }

}
