package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.DslSortBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.DynamicSortBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

public class SortQueryBuilderFactory
    extends AbstractBuilderFactory
{
    private static final String UNMAPPED_TYPE = "long";

    public SortQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public List<SortBuilder> create( final Collection<OrderExpr> orderExpressions )
    {
        return doCreate( orderExpressions );
    }

    private List<SortBuilder> doCreate( final Collection<OrderExpr> orderExpressions )
    {
        if ( orderExpressions.isEmpty() )
        {
            return new ArrayList<>();
        }

        List<SortBuilder> sortBuilders = new ArrayList<>();

        for ( final OrderExpr orderExpr : orderExpressions )
        {
            if ( orderExpr instanceof FieldOrderExpr )
            {
                final FieldOrderExpr fieldOrderExpr = (FieldOrderExpr) orderExpr;
                sortBuilders.addAll( createFieldSortBuilders( fieldNameResolver, fieldOrderExpr.getField().getIndexPath(),
                                                              fieldOrderExpr.getDirection(), fieldOrderExpr.getLanguage() ) );
            }
            else if ( orderExpr instanceof DynamicOrderExpr )
            {
                sortBuilders.add( new DynamicSortBuilderFactory( fieldNameResolver ).create( (DynamicOrderExpr) orderExpr ) );
            }
            else if ( orderExpr instanceof DslOrderExpr )
            {
                sortBuilders.addAll( new DslSortBuilderFactory( fieldNameResolver ).create( (DslOrderExpr) orderExpr ) );
            }
        }

        return sortBuilders;
    }

    /**
     * Creates a sort on the order-by value of a field. When a language is given, documents are sorted by the collation of that
     * language. Documents without the language-specific value (indexed without that language) fall back to binary order.
     */
    public static List<SortBuilder> createFieldSortBuilders( final QueryFieldNameResolver fieldNameResolver, final IndexPath field,
                                                             final OrderExpr.@Nullable Direction direction, final @Nullable Locale language )
    {
        final String fieldName = fieldNameResolver.resolveOrderByFieldName( field, language );
        if ( language == null )
        {
            return List.of( createFieldSortBuilder( fieldName, direction ) );
        }

        final String fallbackFieldName = fieldNameResolver.resolveOrderByFieldName( field, null );
        if ( fallbackFieldName.equals( fieldName ) )
        {
            return List.of( createFieldSortBuilder( fieldName, direction ) );
        }
        return List.of( createFieldSortBuilder( fieldName, direction ), createFieldSortBuilder( fallbackFieldName, direction ) );
    }

    private static FieldSortBuilder createFieldSortBuilder( final String fieldName, final OrderExpr.@Nullable Direction direction )
    {
        final FieldSortBuilder fieldSortBuilder = new FieldSortBuilder( fieldName );
        if ( direction != null )
        {
            fieldSortBuilder.order( SortOrder.valueOf( direction.name() ) );
        }
        fieldSortBuilder.unmappedType( UNMAPPED_TYPE );
        return fieldSortBuilder;
    }

}
