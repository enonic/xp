package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.IndicesFilterBuilder;
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
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.IndicesFilter;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
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
            final FilterBuilder filterBuilder = doCreateFilterBuilder( filter );
            if ( filterBuilder != null )
            {
                filtersToApply.add( filterBuilder );
            }
        }
    }

    private FilterBuilder doCreateFilterBuilder( final Filter filter )
    {
        if ( filter instanceof ExistsFilter )
        {
            return createExistsFilter( (ExistsFilter) filter );
        }
        else if ( filter instanceof ValueFilter )
        {
            return createTermFilter( (ValueFilter) filter );
        }
        else if ( filter instanceof RangeFilter )
        {
            return createRangeFilter( (RangeFilter) filter );
        }
        else if ( filter instanceof BooleanFilter )
        {
            return createBooleanFilter( (BooleanFilter) filter );
        }
        else if ( filter instanceof IdFilter )
        {
            return createIdFilter( (IdFilter) filter );
        }
        else if ( filter instanceof IndicesFilter )
        {
            return createIndicesFilter( (IndicesFilter) filter );
        }
        else
        {
            throw new IllegalArgumentException( String.format( "Filter of type %s not supported", filter.getClass() ) );
        }
    }

    private FilterBuilder createIndicesFilter( final IndicesFilter indicesFilter )
    {
        final IndicesFilterBuilder builder =
            new IndicesFilterBuilder( doCreateFilterBuilder( indicesFilter.getFilter() ), indicesFilter.getIndices() );

        if ( indicesFilter.getNoMatchFilter() != null )
        {
            builder.noMatchFilter( doCreateFilterBuilder( indicesFilter.getNoMatchFilter() ) );
        }

        return builder;
    }

    private FilterBuilder createIdFilter( final IdFilter idFilter )
    {
        final String queryFieldName = IndexFieldNameNormalizer.normalize( idFilter.getFieldName() );

        final Set<Object> values = Sets.newHashSet();

        values.addAll( idFilter.getValues() );

        return new TermsFilterBuilder( queryFieldName, values );
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
