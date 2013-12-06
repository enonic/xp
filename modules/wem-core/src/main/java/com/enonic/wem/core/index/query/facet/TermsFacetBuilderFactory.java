package com.enonic.wem.core.index.query.facet;

import java.util.Set;

import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.core.index.query.AbstractBuilderFactory;
import com.enonic.wem.core.index.query.IndexQueryFieldNameResolver;
import com.enonic.wem.query.facet.RegExpFlag;
import com.enonic.wem.query.facet.TermsFacetQuery;

public class TermsFacetBuilderFactory
    extends AbstractBuilderFactory
{
    public TermsFacetBuilder create( final TermsFacetQuery termsFacetQuery )
    {
        Preconditions.checkArgument( termsFacetQuery.getFields() != null && !termsFacetQuery.getFields().isEmpty(),
                                     "missing value 'fields' in facet: '" + termsFacetQuery.getName() + "'" );

        final TermsFacetBuilder termsFacetBuilder = new TermsFacetBuilder( termsFacetQuery.getName() );

        setFields( termsFacetQuery, termsFacetBuilder );
        termsFacetBuilder.allTerms( termsFacetQuery.getAllTerms() );
        setExclude( termsFacetQuery, termsFacetBuilder );
        setRegexp( termsFacetQuery, termsFacetBuilder );
        setOrderBy( termsFacetQuery, termsFacetBuilder );

        return termsFacetBuilder;
    }

    private void setOrderBy( final TermsFacetQuery termsFacetQuery, final TermsFacetBuilder termsFacetBuilder )
    {
        termsFacetBuilder.order( TermsFacet.ComparatorType.valueOf( termsFacetQuery.getOrderby().name() ) );
    }

    private void setFields( final TermsFacetQuery termsFacetQuery, final TermsFacetBuilder termsFacetBuilder )
    {
        final ImmutableSet<String> fields = termsFacetQuery.getFields();

        final Set<String> indexQueryFileNames = Sets.newHashSet();

        for ( final String field : fields )
        {
            indexQueryFileNames.add( IndexQueryFieldNameResolver.resolveStringFieldName( field ) );
        }

        termsFacetBuilder.fields( indexQueryFileNames.toArray( new String[indexQueryFileNames.size()] ) );
    }

    private void setExclude( final TermsFacetQuery termsFacetQuery, final TermsFacetBuilder termsFacetBuilder )
    {
        final ImmutableSet<String> excludes = termsFacetQuery.getExcludes();

        final Set<String> indexQueryExcludeFieldNames = Sets.newHashSet();

        if ( excludes != null && !excludes.isEmpty() )
        {
            for ( final String exclude : excludes )
            {
                indexQueryExcludeFieldNames.add( IndexQueryFieldNameResolver.resolveStringFieldName( exclude ) );
            }

            termsFacetBuilder.exclude( indexQueryExcludeFieldNames.toArray( new String[indexQueryExcludeFieldNames.size()] ) );
        }
    }

    private void setRegexp( final TermsFacetQuery termsFacetQuery, final TermsFacetBuilder builder )
    {
        if ( !Strings.isNullOrEmpty( termsFacetQuery.getRegex() ) )
        {
            if ( termsFacetQuery.getRegExpFlags() != null && !termsFacetQuery.getRegExpFlags().isEmpty() )
            {
                builder.regex( termsFacetQuery.getRegex(), getRegexFlagValue( termsFacetQuery.getRegExpFlags() ) );
            }
            else
            {
                builder.regex( termsFacetQuery.getRegex() );
            }
        }
    }

    private int getRegexFlagValue( final Set<RegExpFlag> regExpFlags )
    {
        int flagValue = 0;

        for ( final RegExpFlag flag : regExpFlags )
        {
            flagValue += flag.getValue();
        }

        return flagValue;
    }

}
