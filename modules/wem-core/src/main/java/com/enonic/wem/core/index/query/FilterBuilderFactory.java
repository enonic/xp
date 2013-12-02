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
import com.enonic.wem.query.queryfilter.ExistsQueryFilter;
import com.enonic.wem.query.queryfilter.QueryFilter;
import com.enonic.wem.query.queryfilter.ValueQueryFilter;

public class FilterBuilderFactory
    extends AbstractBuilderFactory
{
    public FilterBuilder create( final QueryFilter queryFilter )
    {
        if ( queryFilter == null )
        {
            return null;
        }

        return doCreate( ImmutableSet.<QueryFilter>of( queryFilter ) );
    }

    public FilterBuilder create( final ImmutableSet<QueryFilter> queryFilters )
    {
        if ( queryFilters == null || queryFilters.isEmpty() )
        {
            return null;
        }

        return doCreate( queryFilters );
    }

    private FilterBuilder doCreate( final ImmutableSet<QueryFilter> queryFilters )
    {
        List<FilterBuilder> filtersToApply = Lists.newArrayList();

        for ( final QueryFilter filter : queryFilters )
        {
            if ( filter instanceof ExistsQueryFilter )
            {
                filtersToApply.add( createExistsFilter( (ExistsQueryFilter) filter ) );
            }
            else if ( filter instanceof ValueQueryFilter )
            {
                filtersToApply.add( createTermFilter( (ValueQueryFilter) filter ) );
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


    private FilterBuilder createTermFilter( final ValueQueryFilter filter )
    {
        final String queryFieldName = IndexQueryFieldNameResolver.resolve( filter );

        final Set<Object> values = Sets.newHashSet();

        for ( final Value value : filter.getValues() )
        {
            values.add( getValueAsType( value ) );
        }

        return new TermsFilterBuilder( queryFieldName, values );
    }

    private FilterBuilder createExistsFilter( final ExistsQueryFilter filter )
    {
        final String resolvedQueryFieldName = IndexQueryFieldNameResolver.resolveStringFieldName( filter.getFieldName() );

        return new ExistsFilterBuilder( resolvedQueryFieldName );
    }
}
