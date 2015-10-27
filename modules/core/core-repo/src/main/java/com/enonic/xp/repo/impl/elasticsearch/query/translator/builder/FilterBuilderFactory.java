package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.ValueHelper;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class FilterBuilderFactory
    extends AbstractBuilderFactory
{
    public FilterBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public FilterBuilder create( final Filters filters )
    {
        if ( filters == null || filters.isEmpty() )
        {
            return null;
        }

        return doCreate( ImmutableSet.copyOf( filters ) );
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
                final FilterBuilder termFilter = createTermFilter( (ValueFilter) filter );

                if ( termFilter != null )
                {
                    filtersToApply.add( termFilter );
                }
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

        if ( from == null && to == null )
        {
            return null;
        }

        final String queryFieldName = this.fieldNameResolver.resolve( filter.getFieldName(), from != null ? from : to );

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

    private FilterBuilder createTermFilter( final ValueFilter filter )
    {
        if ( filter.getValues().isEmpty() )
        {
            return null;
        }

        final String queryFieldName = this.fieldNameResolver.resolve( filter );

        final Set<Object> values = Sets.newHashSet();

        values.addAll( filter.getValues().stream().map( ValueHelper::getValueAsType ).collect( Collectors.toList() ) );

        final TermsFilterBuilder builder = new TermsFilterBuilder( queryFieldName, values );

        if ( filter.isCache() != null )
        {
            builder.cache( filter.isCache() );
        }

        return builder;
    }

    private FilterBuilder createExistsFilter( final ExistsFilter filter )
    {
        final String resolvedQueryFieldName = this.fieldNameResolver.resolve( filter.getFieldName(), IndexValueType.STRING );

        return new ExistsFilterBuilder( resolvedQueryFieldName );
    }
}
