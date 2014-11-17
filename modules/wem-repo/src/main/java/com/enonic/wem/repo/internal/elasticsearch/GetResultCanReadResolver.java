package com.enonic.wem.repo.internal.elasticsearch;

import java.util.List;

import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResultField;

class GetResultCanReadResolver
{
    public static boolean canRead( final Principals principals, final GetResult getResult )
    {
        final SearchResultField hasRead = getResult.getSearchResult().getField( IndexPaths.HAS_READ_KEY );

        if ( hasRead == null )
        {
            return false;
        }

        final List<Object> values = hasRead.getValues();

        final PrincipalKeys keys = principals.isEmpty() ? PrincipalKeys.from( PrincipalKey.ofAnonymous() ) : principals.getKeys();

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
