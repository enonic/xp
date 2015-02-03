package com.enonic.wem.repo.internal.elasticsearch.query.builder;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.repo.internal.entity.NodeConstants;

public class AclFilterBuilderFactory
{
    public static Filter create( final PrincipalKeys principalsKeys )
    {
        if ( isNodeSuperUser() )
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
            valueFilterBuilder.addValue( Value.newString( principalKey.toString() ) );
        }

        return valueFilterBuilder.build();
    }

    private static Filter createNoPrincipalsFilter()
    {
        return ValueFilter.create().
            fieldName( NodeIndexPath.PERMISSIONS_READ.toString() ).
            addValue( Value.newString( PrincipalKey.ofAnonymous().toString() ) ).
            build();
    }

    private static boolean isNodeSuperUser()
    {
        final Context context = ContextAccessor.current();

        final AuthenticationInfo authInfo = context.getAuthInfo();

        return authInfo != null && authInfo.getUser() != null && authInfo.getUser().getKey().equals( NodeConstants.NODE_SUPER_USER_KEY );
    }

}
