package com.enonic.wem.query.facet;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class TermsFacetQuery
    extends FacetQuery
{
    public enum TermFacetOrderBy
    {
        COUNT,
        TERM,
        REVERSE_COUNT,
        REVERSE_TERM
    }

    public static final int DEFAULT_TERM_FACET_SIZE = 10;

    private final ImmutableSet<String> fields;

    private final ImmutableSet<String> excludes;

    private final TermFacetOrderBy orderby;

    private final String regex;

    private final ImmutableSet<RegExpFlag> regExpFlags;

    private final Boolean allTerms;

    private final Integer size;

    private TermsFacetQuery( final Builder builder )
    {
        super( builder.name );
        this.fields = ImmutableSet.copyOf( builder.fields );
        this.allTerms = builder.allTerms;
        this.regex = builder.regex;
        this.regExpFlags = ImmutableSet.copyOf( builder.regExpFlags );
        this.excludes = ImmutableSet.copyOf( builder.excludes );
        this.size = builder.size;
        this.orderby = builder.orderBy;
    }

    public ImmutableSet<String> getFields()
    {
        return fields;
    }

    public ImmutableSet<String> getExcludes()
    {
        return excludes;
    }

    public String getRegex()
    {
        return regex;
    }

    public ImmutableSet<RegExpFlag> getRegExpFlags()
    {
        return regExpFlags;
    }

    public Boolean getAllTerms()
    {
        return allTerms;
    }

    public TermFacetOrderBy getOrderby()
    {
        return orderby;
    }

    public static class Builder
        extends FacetQuery.Builder
    {
        private Collection<String> fields = Sets.newHashSet();

        private Collection<String> excludes = Sets.newHashSet();

        private String regex;

        private Collection<RegExpFlag> regExpFlags = Sets.newHashSet();

        private boolean allTerms = false;

        private int size = DEFAULT_TERM_FACET_SIZE;

        private TermFacetOrderBy orderBy = TermFacetOrderBy.TERM;

        public Builder( final String name )
        {
            super( name );
        }

        public Builder fields( final Collection<String> fields )
        {
            this.fields = fields;
            return this;
        }

        public Builder exclude( final Collection<String> excludes )
        {
            this.excludes = excludes;
            return this;
        }

        public Builder regex( final String regex )
        {
            this.regex = regex;
            return this;
        }

        public Builder regexFlags( final Collection<RegExpFlag> regexFlags )
        {
            this.regExpFlags = regexFlags;
            return this;
        }

        public Builder allTerms( final Boolean allTerms )
        {
            this.allTerms = allTerms;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        public Builder orderBy( final TermFacetOrderBy orderBy )
        {
            this.orderBy = orderBy;
            return this;
        }

        public TermsFacetQuery build()
        {
            return new TermsFacetQuery( this );
        }
    }

}
