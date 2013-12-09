package com.enonic.wem.core.index.query;

import java.util.List;
import java.util.Set;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.query.filter.ExistsFilter;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.api.query.filter.ValueFilter;

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
        }

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
