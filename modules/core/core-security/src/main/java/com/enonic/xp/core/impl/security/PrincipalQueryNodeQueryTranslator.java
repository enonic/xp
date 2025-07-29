package com.enonic.xp.core.impl.security;

import java.util.Collections;
import java.util.Set;

import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalType;

import static com.enonic.xp.core.impl.security.PrincipalPropertyNames.DISPLAY_NAME_KEY;
import static com.enonic.xp.core.impl.security.PrincipalPropertyNames.EMAIL_KEY;
import static com.enonic.xp.core.impl.security.PrincipalPropertyNames.ID_PROVIDER_KEY;
import static com.enonic.xp.core.impl.security.PrincipalPropertyNames.NAME_KEY;
import static com.enonic.xp.core.impl.security.PrincipalPropertyNames.PRINCIPAL_TYPE_KEY;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.stream.Collectors.toList;

final class PrincipalQueryNodeQueryTranslator
{
    private static final String ALL_TEXT_FIELD_NAME = NodeIndexPath.ALL_TEXT.getPath();

    public static NodeQuery translate( final PrincipalQuery principalQuery )
    {
        final NodeQuery.Builder nodeQueryBuilder = NodeQuery.create().
            from( principalQuery.getFrom() ).
            size( principalQuery.getSize() );

        final IdProviderKeys idProviders = principalQuery.getIdProviders();
        if ( !idProviders.isEmpty() )
        {
            nodeQueryBuilder.addQueryFilter( ValueFilter.create().
                fieldName( ID_PROVIDER_KEY ).
                addValues( idProviders.stream().map( IdProviderKey::toString ).collect( toList() ) ).
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
        if ( !nullToEmpty( searchText ).isBlank() )
        {
            nodeQueryBuilder.query( getQueryExpression( searchText ) );
        }

        final String email = principalQuery.getEmail();
        if ( !nullToEmpty( email ).isBlank() )
        {
            nodeQueryBuilder.addQueryFilter( ValueFilter.create().
                fieldName( EMAIL_KEY ).
                addValues( email ).
                build() );
        }

        final String name = principalQuery.getName();
        if ( !nullToEmpty( name ).isBlank() )
        {
            nodeQueryBuilder.addQueryFilter( ValueFilter.create().
                fieldName( NAME_KEY ).
                addValues( name ).
                build() );
        }

        final String displayName = principalQuery.getDisplayName();
        if ( !nullToEmpty( displayName ).isBlank() )
        {
            nodeQueryBuilder.addQueryFilter( ValueFilter.create().
                fieldName( DISPLAY_NAME_KEY ).
                addValues( displayName ).
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
