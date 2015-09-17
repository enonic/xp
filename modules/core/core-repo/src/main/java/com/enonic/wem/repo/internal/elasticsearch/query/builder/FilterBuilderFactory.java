package com.enonic.wem.repo.internal.elasticsearch.query.builder;

import java.util.List;
import java.util.Set;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;
import com.enonic.xp.data.Value;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;

public class FilterBuilderFactory
    extends AbstractQueryBuilderFactory
{
    public static FilterBuilder create( final Filters filters )
    {
        if ( filters == null || filters.isEmpty() )
        {
            return null;
        }

        return doCreate( ImmutableSet.copyOf( filters ) );
    }

    public static FilterBuilder create( final ImmutableSet<Filter> queryFilters )
    {
        if ( queryFilters == null || queryFilters.isEmpty() )
        {
            return null;
        }

        return doCreate( queryFilters );
    }

    private static FilterBuilder doCreate( final ImmutableSet<Filter> queryFilters )
    {
        List<FilterBuilder> filtersToApply = Lists.newArrayList();

        appendFilters( queryFilters, filtersToApply );

        if ( filtersToApply.size() > 1 )
        {
            return new BoolFilterBuilder().must( filtersToApply.toArray( new FilterBuilder[filtersToApply.size()] ) );
        }
        else if ( filtersToApply.size() == 1 )
        {
            return filtersToApply.get( 0 );
        }

        return null;
    }

    private static void appendFilters( final ImmutableSet<Filter> queryFilters, final List<FilterBuilder> filtersToApply )
    {
        for ( final Filter filter : queryFilters )
        {
            if ( filter instanceof ExistsFilter )
            {
                filtersToApply.add( createExistsFilter( (ExistsFilter) filter ) );
            }
            else if ( filter instanceof ValueFilter )
            {
                filtersToApply.add( createTermFilter( (ValueFilter) filter ) );
            }
            else if ( filter instanceof RangeFilter )
            {
                filtersToApply.add( createRangeFilter( (RangeFilter) filter ) );
            }
            else if ( filter instanceof BooleanFilter )
            {
                filtersToApply.add( createBooleanFilter( (BooleanFilter) filter ) );
            }
        }
    }


    private static FilterBuilder createBooleanFilter( final BooleanFilter booleanFilter )
    {
        final BoolFilterBuilder builder = new BoolFilterBuilder().
            must( createBooleanFilterChildren( booleanFilter.getMust() ) ).
            mustNot( createBooleanFilterChildren( booleanFilter.getMustNot() ) ).
            should( createBooleanFilterChildren( booleanFilter.getShould() ) );

        if ( booleanFilter.isCache() != null )
        {
            builder.cache( booleanFilter.isCache() );
        }

        return builder;
    }

    private static FilterBuilder[] createBooleanFilterChildren( final ImmutableSet<Filter> queryFilters )
    {
        List<FilterBuilder> filtersToApply = Lists.newArrayList();

        appendFilters( queryFilters, filtersToApply );

        return filtersToApply.toArray( new FilterBuilder[filtersToApply.size()] );
    }


    private static FilterBuilder createRangeFilter( final RangeFilter filter )
    {
        final Value from = filter.getFrom();
        final Value to = filter.getTo();

        if ( from == null && to == null )
        {
            return null;
        }

        final String queryFieldName =
            IndexQueryFieldNameResolver.createValueTypeAwareFieldName( filter.getFieldName(), from != null ? from : to );

        RangeFilterBuilder builder = new RangeFilterBuilder( queryFieldName ).
            from( from ).
            to( to ).
            includeLower( true ).
            includeUpper( true );

        if ( filter.isCache() != null )
        {
            builder.cache( filter.isCache() );
        }

        return builder;
    }

    private static FilterBuilder createTermFilter( final ValueFilter filter )
    {
        final String queryFieldName = IndexQueryFieldNameResolver.resolve( filter );

        final Set<Object> values = Sets.newHashSet();

        for ( final Value value : filter.getValues() )
        {
            values.add( getValueAsType( value ) );
        }

        final TermsFilterBuilder builder = new TermsFilterBuilder( queryFieldName, values );

        if ( filter.isCache() != null )
        {
            builder.cache( filter.isCache() );
        }

        return builder;
    }

    private static FilterBuilder createExistsFilter( final ExistsFilter filter )
    {
        final String resolvedQueryFieldName = IndexQueryFieldNameResolver.resolveStringFieldName( filter.getFieldName() );

        return new ExistsFilterBuilder( resolvedQueryFieldName );
    }
}
