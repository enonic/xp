package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

    private static final String NUMBER_UNMAPPED_TYPE = "double";

    public SortQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    /**
     * The sorts for the order expressions, and the positions of the sorts added on top of what was asked for: the numeric sort in front
     * of a field and the fallback behind a language sort. Hits leave the values of those positions out, so their sort values still hold
     * one value per order expression.
     */
    public record Sorts(List<SortBuilder> sortBuilders, Set<Integer> hiddenPositions)
    {
    }

    public Sorts createSorts( final Collection<OrderExpr> orderExpressions )
    {
        final List<SortBuilder> sortBuilders = new ArrayList<>();
        final Set<Integer> hiddenPositions = new HashSet<>();

        for ( final OrderExpr orderExpr : orderExpressions )
        {
            if ( orderExpr instanceof FieldOrderExpr )
            {
                final FieldOrderExpr fieldOrderExpr = (FieldOrderExpr) orderExpr;
                final IndexPath field = fieldOrderExpr.getField().getIndexPath();
                addHidden( sortBuilders, hiddenPositions,
                           createNumberSortBuilder( fieldNameResolver, field, fieldOrderExpr.getDirection() ) );
                sortBuilders.add(
                    createFieldSortBuilder( fieldNameResolver, field, fieldOrderExpr.getDirection(), fieldOrderExpr.getLanguage() ) );
                addHidden( sortBuilders, hiddenPositions,
                           createFallbackSortBuilder( fieldNameResolver, field, fieldOrderExpr.getDirection(), fieldOrderExpr.getLanguage() ) );
            }
            else if ( orderExpr instanceof DynamicOrderExpr )
            {
                sortBuilders.add( new DynamicSortBuilderFactory( fieldNameResolver ).create( (DynamicOrderExpr) orderExpr ) );
            }
            else if ( orderExpr instanceof DslOrderExpr )
            {
                final DslSortBuilderFactory dslSortBuilderFactory = new DslSortBuilderFactory( fieldNameResolver );
                addHidden( sortBuilders, hiddenPositions, dslSortBuilderFactory.createNumberSort( (DslOrderExpr) orderExpr ) );
                sortBuilders.add( dslSortBuilderFactory.create( (DslOrderExpr) orderExpr ) );
                addHidden( sortBuilders, hiddenPositions, dslSortBuilderFactory.createFallback( (DslOrderExpr) orderExpr ) );
            }
        }

        return new Sorts( sortBuilders, Set.copyOf( hiddenPositions ) );
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

    /**
     * Creates the numeric sort that goes before the order-by value of a field. The order-by value encodes the type before the value,
     * which puts every Double before every Long; the numeric field holds both as a double. Documents without a number there, such as
     * strings, sort after the numbers and are then ordered by the order-by value.
     */
    public static @Nullable SortBuilder createNumberSortBuilder( final QueryFieldNameResolver fieldNameResolver, final IndexPath field,
                                                                 final OrderExpr.@Nullable Direction direction )
    {
        final String fieldName = fieldNameResolver.resolveNumberOrderByFieldName( field );
        if ( fieldName == null )
        {
            return null;
        }
        final FieldSortBuilder fieldSortBuilder = new FieldSortBuilder( fieldName );
        if ( direction != null )
        {
            fieldSortBuilder.order( SortOrder.valueOf( direction.name() ) );
        }
        return fieldSortBuilder.missing( "_last" ).unmappedType( NUMBER_UNMAPPED_TYPE );
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

    private static void addHidden( final List<SortBuilder> sortBuilders, final Set<Integer> hiddenPositions,
                                   final @Nullable SortBuilder sortBuilder )
    {
        if ( sortBuilder != null )
        {
            hiddenPositions.add( sortBuilders.size() );
            sortBuilders.add( sortBuilder );
        }
    }
}
