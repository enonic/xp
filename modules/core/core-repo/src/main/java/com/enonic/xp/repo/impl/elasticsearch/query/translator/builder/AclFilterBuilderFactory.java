package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class AclFilterBuilderFactory
    extends AbstractBuilderFactory
{
    public AclFilterBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public static Filter create( final PrincipalKeys principalsKeys )
    {
        if ( isSuperUser() )
        {
            return null;
        }

        if ( principalsKeys.isEmpty() )
        {
            return createNoPrincipalsFilter();
        }

        final ValueFilter.Builder valueFilterBuilder = ValueFilter.create().
            fieldName( NodeIndexPath.PERMISSIONS_READ.toString() );

        for ( final PrincipalKey principalKey : principalsKeys )
        {
            valueFilterBuilder.addValue( ValueFactory.newString( principalKey.toString() ) );
        }

        return valueFilterBuilder.build();
    }

    private static Filter createNoPrincipalsFilter()
    {
        return ValueFilter.create().
            fieldName( NodeIndexPath.PERMISSIONS_READ.toString() ).
            addValue( ValueFactory.newString( PrincipalKey.ofAnonymous().toString() ) ).
            build();
    }

    private static boolean isSuperUser()
    {
        final Context context = ContextAccessor.current();

        final AuthenticationInfo authInfo = context.getAuthInfo();

        return authInfo != null && authInfo.hasRole( RoleKeys.ADMIN );
    }
}
