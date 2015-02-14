package com.enonic.xp.security.impl;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.query.expr.DynamicConstraintExpr;
import com.enonic.wem.api.query.expr.FunctionExpr;
import com.enonic.wem.api.query.expr.LogicalExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.expr.ValueExpr;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.security.PrincipalQuery;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStoreKeys;

import static com.enonic.xp.security.impl.PrincipalPropertyNames.DISPLAY_NAME_KEY;
import static com.enonic.xp.security.impl.PrincipalPropertyNames.EMAIL_KEY;
import static com.enonic.xp.security.impl.PrincipalPropertyNames.PRINCIPAL_TYPE_KEY;
import static com.enonic.xp.security.impl.PrincipalPropertyNames.USER_STORE_KEY;
import static java.util.stream.Collectors.toList;

final class PrincipalQueryNodeQueryTranslator
{
    private static final String ALL_TEXT_FIELD_NAME = NodeIndexPath.ALL_TEXT.getPath();

    public static NodeQuery translate( final PrincipalQuery principalQuery )
    {
        final NodeQuery.Builder nodeQueryBuilder = NodeQuery.create().
            from( principalQuery.getFrom() ).
            size( principalQuery.getSize() );

        final UserStoreKeys userStores = principalQuery.getUserStores();
        if ( userStores.isNotEmpty() )
        {
            nodeQueryBuilder.addQueryFilter( ValueFilter.create().
                fieldName( USER_STORE_KEY ).
                addValues( userStores.stream().map( UserStoreKey::toString ).collect( toList() ) ).
                build() );
        }
        final Set<PrincipalType> types = principalQuery.getPrincipalTypes();
        if ( !types.isEmpty() )
        {
            nodeQueryBuilder.addQueryFilter( ValueFilter.create().
                fieldName( PRINCIPAL_TYPE_KEY ).
                addValues( types.stream().map( Object::toString ).collect( toList() ) ).
                build() );
        }

        final String searchText = principalQuery.getSearchText();
        if ( StringUtils.isNotBlank( searchText ) )
        {
            nodeQueryBuilder.query( getQueryExpression( searchText ) );
        }

        final String email = principalQuery.getEmail();
        if ( StringUtils.isNotBlank( email ) )
        {
            nodeQueryBuilder.addQueryFilter( ValueFilter.create().
                fieldName( EMAIL_KEY ).
                addValues( email ).
                build() );
        }

        return nodeQueryBuilder.build();
    }

    private static QueryExpr getQueryExpression( final String query )
    {
        final String fields = String.join( ",", DISPLAY_NAME_KEY, ALL_TEXT_FIELD_NAME );
        final FunctionExpr fullText =
            FunctionExpr.from( "fulltext", ValueExpr.string( fields ), ValueExpr.string( query ), ValueExpr.string( "AND" ) );
        final FunctionExpr nGram =
            FunctionExpr.from( "ngram", ValueExpr.string( fields ), ValueExpr.string( query ), ValueExpr.string( "AND" ) );
        final LogicalExpr fullTextOrNgram = LogicalExpr.or( new DynamicConstraintExpr( fullText ), new DynamicConstraintExpr( nGram ) );
        return new QueryExpr( fullTextOrNgram, Collections.emptySet() );
    }

}
