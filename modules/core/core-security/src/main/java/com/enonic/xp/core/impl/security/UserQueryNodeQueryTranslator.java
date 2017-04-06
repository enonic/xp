package com.enonic.xp.core.impl.security;

import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.UserQuery;

import static com.enonic.xp.core.impl.security.PrincipalPropertyNames.PRINCIPAL_TYPE_KEY;

final class UserQueryNodeQueryTranslator
{
    static NodeQuery translate( final UserQuery userQuery )
    {
        final NodeQuery.Builder nodeQueryBuilder = NodeQuery.create().
            query( userQuery.getQueryExpr() ).
            from( userQuery.getFrom() ).
            size( userQuery.getSize() );

        nodeQueryBuilder.addQueryFilter( ValueFilter.create().
            fieldName( PRINCIPAL_TYPE_KEY ).
            addValues( PrincipalType.USER.toString() ).
            build() );

        return nodeQueryBuilder.build();
    }
}
