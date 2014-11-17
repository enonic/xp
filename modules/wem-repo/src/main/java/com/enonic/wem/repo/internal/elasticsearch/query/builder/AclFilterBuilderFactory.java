package com.enonic.wem.repo.internal.elasticsearch.query.builder;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.User;

public class AclFilterBuilderFactory
{
    public static Filter create( final Principals principals )
    {
        if ( principals.isEmpty() )
        {
            return createNoPrincipalsFilter();
        }

        final ValueFilter.Builder valueFilterBuilder = ValueFilter.create().
            fieldName( IndexPaths.HAS_READ_KEY );

        for ( final Principal principal : principals )
        {
            valueFilterBuilder.addValue( Value.newString( principal.getKey() ) );
        }

        return valueFilterBuilder.build();
    }

    private static Filter createNoPrincipalsFilter()
    {
        return ValueFilter.create().
            fieldName( IndexPaths.HAS_READ_KEY ).
            addValue( Value.newString( User.anonymous().getKey() ) ).
            build();
    }
}
