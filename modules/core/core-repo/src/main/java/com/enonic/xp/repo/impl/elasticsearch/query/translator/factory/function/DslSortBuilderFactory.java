package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.search.sort.SortBuilder;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.SortQueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

public class DslSortBuilderFactory
    extends AbstractBuilderFactory
{
    public DslSortBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public SortBuilder create( final DslOrderExpr orderExpr )
    {
        final String type = orderExpr.getType();

        if ( type != null && !"geoDistance".equals( type ) )
        {
            throw new IllegalArgumentException( "Not valid sort function: '" + type + "'" );
        }

        if ( "geoDistance".equals( type ) || orderExpr.getLat() != null )
        {
            return GeoDistanceSortFunction.create( orderExpr );
        }
        else
        {
            return SortQueryBuilderFactory.createFieldSortBuilder( fieldNameResolver, IndexPath.from( orderExpr.getField() ),
                                                                   orderExpr.getDirection(), orderExpr.getLanguage() );
        }
    }

    public @Nullable SortBuilder createFallback( final DslOrderExpr orderExpr )
    {
        if ( "geoDistance".equals( orderExpr.getType() ) || orderExpr.getLat() != null )
        {
            return null;
        }
        return SortQueryBuilderFactory.createFallbackSortBuilder( fieldNameResolver, IndexPath.from( orderExpr.getField() ),
                                                                  orderExpr.getDirection(), orderExpr.getLanguage() );
    }
}
