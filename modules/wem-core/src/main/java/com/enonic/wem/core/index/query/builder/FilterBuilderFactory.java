package com.enonic.wem.core.index.query.builder;

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

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.query.filter.BooleanFilter;
import com.enonic.wem.api.query.filter.ExistsFilter;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.api.query.filter.RangeFilter;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.core.index.query.IndexQueryFieldNameResolver;

public class FilterBuilderFactory
    extends AbstractBuilderFactory
{
    public FilterBuilder create( final Filter queryFilter )
    {
        if ( queryFilter == null )
        {
            return null;
        }

        return doCreate( ImmutableSet.<Filter>of( queryFilter ) );
    }

    public FilterBuilder create( final ImmutableSet<Filter> queryFilters )
    {
        if ( queryFilters == null || queryFilters.isEmpty() )
        {
            return null;
        }

        return doCreate( queryFilters );
    }

    private FilterBuilder doCreate( final ImmutableSet<Filter> queryFilters )
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

    private void appendFilters( final ImmutableSet<Filter> queryFilters, final List<FilterBuilder> filtersToApply )
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


    private FilterBuilder createBooleanFilter( final BooleanFilter booleanFilter )
    {
        final BoolFilterBuilder builder = new BoolFilterBuilder();

        builder.must( createBooleanFilterChildren( booleanFilter.getMust() ) );
        builder.mustNot( createBooleanFilterChildren( booleanFilter.getMustNot() ) );
        builder.should( createBooleanFilterChildren( booleanFilter.getShould() ) );

        return builder;
    }

    private FilterBuilder[] createBooleanFilterChildren( final ImmutableSet<Filter> queryFilters )
    {
        List<FilterBuilder> filtersToApply = Lists.newArrayList();

        appendFilters( queryFilters, filtersToApply );

        return filtersToApply.toArray( new FilterBuilder[filtersToApply.size()] );
    }


    private FilterBuilder createRangeFilter( final RangeFilter filter )
    {
        final Value from = filter.getFrom();
        final Value to = filter.getTo();

        if ( from == null & to == null )
        {
            return null;
        }

        final String queryFieldName =
            IndexQueryFieldNameResolver.createValueTypeAwareFieldName( filter.getFieldName(), from != null ? from : to );

        RangeFilterBuilder builder = new RangeFilterBuilder( queryFieldName );
        builder.from( from );
        builder.to( to );
        builder.includeLower( true );
        builder.includeUpper( true );

        return builder;
    }

    private FilterBuilder createTermFilter( final ValueFilter filter )
    {
        final String queryFieldName = IndexQueryFieldNameResolver.resolve( filter );

        final Set<Object> values = Sets.newHashSet();

        for ( final Value value : filter.getValues() )
        {
            values.add( getValueAsType( value ) );
        }

        return new TermsFilterBuilder( queryFieldName, values );
    }

    private FilterBuilder createExistsFilter( final ExistsFilter filter )
    {
        final String resolvedQueryFieldName = IndexQueryFieldNameResolver.resolveStringFieldName( filter.getFieldName() );

        return new ExistsFilterBuilder( resolvedQueryFieldName );
    }
}
