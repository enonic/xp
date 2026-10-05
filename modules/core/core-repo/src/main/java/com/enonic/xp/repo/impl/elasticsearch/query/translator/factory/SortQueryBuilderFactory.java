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
                final IndexPath field = fieldOrderExpr.getField().getIndexPath();
                sortBuilders.add(
                    createFieldSortBuilder( fieldNameResolver, field, fieldOrderExpr.getDirection(), fieldOrderExpr.getLanguage() ) );
                addIfNotNull( sortBuilders, createFallbackSortBuilder( fieldNameResolver, field, fieldOrderExpr.getDirection(),
                                                                       fieldOrderExpr.getLanguage() ) );
            }
            else if ( orderExpr instanceof DynamicOrderExpr )
            {
                sortBuilders.add( new DynamicSortBuilderFactory( fieldNameResolver ).create( (DynamicOrderExpr) orderExpr ) );
            }
            else if ( orderExpr instanceof DslOrderExpr )
            {
                final DslSortBuilderFactory dslSortBuilderFactory = new DslSortBuilderFactory( fieldNameResolver );
                sortBuilders.add( dslSortBuilderFactory.create( (DslOrderExpr) orderExpr ) );
                addIfNotNull( sortBuilders, dslSortBuilderFactory.createFallback( (DslOrderExpr) orderExpr ) );
            }
        }

        return sortBuilders;
    }

    /**
     * Creates a sort on the order-by value of a field. When a language is given, documents are sorted by the collation of that
     * language.
     */
    public static SortBuilder createFieldSortBuilder( final QueryFieldNameResolver fieldNameResolver, final IndexPath field,
                                                      final OrderExpr.@Nullable Direction direction, final @Nullable Locale language )
    {
        return createFieldSortBuilder( fieldNameResolver.resolveOrderByFieldName( field, language ), direction );
    }

    /**
     * Creates the binary order fallback for a sort with a language. Documents indexed without that language have no
     * language-specific order-by value, and would otherwise be returned in arbitrary order.
     * <p>
     * The fallback goes right after its language sort, so documents without the language are still ordered by the requested field
     * before any following sort, the same way they would be ordered without a language.
     */
    public static @Nullable SortBuilder createFallbackSortBuilder( final QueryFieldNameResolver fieldNameResolver, final IndexPath field,
                                                                   final OrderExpr.@Nullable Direction direction,
                                                                   final @Nullable Locale language )
    {
        if ( language == null )
        {
            return null;
        }
        final String fieldName = fieldNameResolver.resolveOrderByFieldName( field, language );
        final String fallbackFieldName = fieldNameResolver.resolveOrderByFieldName( field, null );
        return fallbackFieldName.equals( fieldName ) ? null : createFieldSortBuilder( fallbackFieldName, direction );
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

    private static void addIfNotNull( final List<SortBuilder> sortBuilders, final @Nullable SortBuilder sortBuilder )
    {
        if ( sortBuilder != null )
        {
            sortBuilders.add( sortBuilder );
        }
    }
}
