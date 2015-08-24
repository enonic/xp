package com.enonic.wem.repo.internal.storage;

import java.util.Set;

import org.elasticsearch.search.sort.SortBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class GetQuery
{
    private final String indexName;

    private final String indexTypeName;

    private final ReturnFields returnFields;

    private final ImmutableSet<SortBuilder> sortBuilders;

    private final String id;

    private final String routing;

    private GetQuery( final Builder builder )
    {
        this.indexName = builder.indexName;
        this.indexTypeName = builder.indexTypeName;
        this.returnFields = builder.returnFields;
        this.sortBuilders = ImmutableSet.copyOf( builder.sortBuilders );
        this.id = builder.id;
        this.routing = builder.routing;
    }

    public String getIndexName()
    {
        return indexName;
    }

    public String getIndexTypeName()
    {
        return indexTypeName;
    }

    public ReturnFields getReturnFields()
    {
        return returnFields;
    }

    public ImmutableSet<SortBuilder> getSortBuilders()
    {
        return sortBuilders;
    }

    public String getId()
    {
        return id;
    }

    public String getRouting()
    {
        return routing;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String indexName;

        private String indexTypeName;

        private String id;

        private ReturnFields returnFields = ReturnFields.empty();

        private Set<SortBuilder> sortBuilders = Sets.newLinkedHashSet();

        private String routing;

        private Builder()
        {
        }

        public Builder indexName( final String indexName )
        {
            this.indexName = indexName;
            return this;
        }

        public Builder indexTypeName( final String indexTypeName )
        {
            this.indexTypeName = indexTypeName;
            return this;
        }

        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        public Builder setSort( final Set<SortBuilder> sortBuilders )
        {
            this.sortBuilders = sortBuilders;
            return this;
        }

        public Builder addSort( final SortBuilder sortBuilder )
        {
            this.sortBuilders.add( sortBuilder );
            return this;
        }

        public Builder returnFields( final ReturnFields returnFields )
        {
            this.returnFields = returnFields;
            return this;
        }

        public Builder routing( final String routing )
        {
            this.routing = routing;
            return this;
        }

        public GetQuery build()
        {
            return new GetQuery( this );
        }
    }

}
