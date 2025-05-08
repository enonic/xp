package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.IndicesQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

import com.google.common.collect.ImmutableSet;

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

    public QueryBuilder create( final Filters filters )
    {
        if ( filters == null || filters.isEmpty() )
        {
            return null;
        }

        return doCreate( ImmutableSet.copyOf( filters ) );
    }

    private QueryBuilder doCreate( final ImmutableSet<Filter> queryFilters )
    {
        List<QueryBuilder> filtersToApply = new ArrayList<>();

        appendFilters( queryFilters, filtersToApply );

        if ( filtersToApply.size() > 1 )
        {
            final BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
            for ( QueryBuilder filter : filtersToApply )
            {
                queryBuilder.must( filter );
            }
            return queryBuilder;
        }
        else if ( filtersToApply.size() == 1 )
        {
            return filtersToApply.get( 0 );
        }

        return null;
    }

    private void appendFilters( final Collection<Filter> queryFilters, final List<QueryBuilder> filtersToApply )
    {
        for ( final Filter filter : queryFilters )
        {
            final QueryBuilder filterBuilder = doCreateFilterBuilder( filter );
            if ( filterBuilder != null )
            {
                filtersToApply.add( filterBuilder );
            }
        }
    }

    private QueryBuilder doCreateFilterBuilder( final Filter filter )
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

    private QueryBuilder createIndicesFilter( final IndicesFilter indicesFilter )
    {
        final IndicesQueryBuilder builder =
            new IndicesQueryBuilder( doCreateFilterBuilder( indicesFilter.getFilter() ), indicesFilter.getIndices() );

        if ( indicesFilter.getNoMatchFilter() != null )
        {
            builder.noMatchQuery( doCreateFilterBuilder( indicesFilter.getNoMatchFilter() ) );
        }

        return builder;
    }

    private QueryBuilder createIdFilter( final IdFilter idFilter )
    {
        final String queryFieldName = IndexFieldNameNormalizer.normalize( idFilter.getFieldName() );

        return new TermsQueryBuilder( queryFieldName, idFilter.getValues() );
    }

    private QueryBuilder createBooleanFilter( final BooleanFilter booleanFilter )
    {
        final BoolQueryBuilder builder = new BoolQueryBuilder();
        Arrays.stream( createBooleanFilterChildren( booleanFilter.getMust() ) ).forEach( builder::must );
        Arrays.stream( createBooleanFilterChildren( booleanFilter.getMustNot() ) ).forEach( builder::mustNot );
        Arrays.stream( createBooleanFilterChildren( booleanFilter.getShould() ) ).forEach( builder::should );

        return builder;
    }

    private QueryBuilder[] createBooleanFilterChildren( final Collection<Filter> queryFilters )
    {
        List<QueryBuilder> filtersToApply = new ArrayList<>();

        appendFilters( queryFilters, filtersToApply );

        return filtersToApply.toArray( new QueryBuilder[0] );
    }


    private QueryBuilder createRangeFilter( final RangeFilter filter )
    {
        final Value from = filter.getFrom();
        final Value to = filter.getTo();

        if ( from == null && to == null )
        {
            return null;
        }

        final String queryFieldName = this.fieldNameResolver.resolve( filter.getFieldName(), from != null ? from : to );

        return new RangeQueryBuilder( queryFieldName ).
            from( from ).
            to( to ).
            includeLower( filter.isIncludeLower() ).
            includeUpper( filter.isIncludeUpper() );
    }

    private QueryBuilder createTermFilter( final ValueFilter filter )
    {
        if ( filter.getValues().isEmpty() )
        {
            return null;
        }

        final String queryFieldName = this.fieldNameResolver.resolve( filter );

        final List<Object> values = filter.getValues().stream().map( ValueHelper::getValueAsType ).collect( Collectors.toList() );

        return new TermsQueryBuilder( queryFieldName, values );
    }

    private QueryBuilder createExistsFilter( final ExistsFilter filter )
    {
        final String resolvedQueryFieldName = this.fieldNameResolver.resolve( filter.getFieldName(), IndexValueType.STRING );

        return new ExistsQueryBuilder( resolvedQueryFieldName );
    }
}
