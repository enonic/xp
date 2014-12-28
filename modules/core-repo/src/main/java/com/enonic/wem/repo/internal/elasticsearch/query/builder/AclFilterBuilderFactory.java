package com.enonic.wem.repo.internal.elasticsearch.query.builder;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.User;

public class AclFilterBuilderFactory
{
    public static Filter create( final PrincipalKeys principalsKeys )
    {
        if ( principalsKeys.isEmpty() )
        {
            return createNoPrincipalsFilter();
        }

        final ValueFilter.Builder valueFilterBuilder = ValueFilter.create().
            fieldName( NodeIndexPath.PERMISSIONS_READ.toString() );

        for ( final PrincipalKey principalKey : principalsKeys )
        {
            valueFilterBuilder.addValue( Value.newString( principalKey.toString() ) );
        }

        return valueFilterBuilder.build();
    }

    private static Filter createNoPrincipalsFilter()
    {
        return ValueFilter.create().
            fieldName( NodeIndexPath.PERMISSIONS_READ.toString() ).
            addValue( Value.newString( User.anonymous().getKey().toString() ) ).
            build();
    }
}
