package com.enonic.wem.core.elasticsearch;

import java.util.Arrays;
import java.util.Set;

import org.elasticsearch.search.sort.SortBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class QueryMetaData
{
    private final String indexName;

    private final String indexTypeName;

    private final int from;

    private final int size;

    private final ImmutableSet<String> fields;

    private final ImmutableSet<SortBuilder> sortBuilders;

    private QueryMetaData( final Builder builder )
    {
        this.indexName = builder.indexName;
        this.indexTypeName = builder.indexTypeName;
        this.fields = ImmutableSet.copyOf( builder.fields );
        this.from = builder.from;
        this.size = builder.size;
        this.sortBuilders = ImmutableSet.copyOf( builder.sortBuilders );
    }

    public String getIndexName()
    {
        return indexName;
    }

    public String getIndexTypeName()
    {
        return indexTypeName;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public ImmutableSet<SortBuilder> getSortBuilders()
    {
        return sortBuilders;
    }

    public boolean hasFields()
    {
        return fields.size() > 0;
    }

    public String[] getFields()
    {
        return fields.toArray( new String[fields.size()] );
    }

    public static Builder create( final String indexName )
    {
        return new Builder( indexName );
    }

    public static class Builder
    {
        private String indexName;

        private String indexTypeName;

        private int from = 0;

        private int size = 10;

        private Set<String> fields = Sets.newHashSet();

        private final Set<SortBuilder> sortBuilders = Sets.newHashSet();

        public Builder( final String indexName )
        {
            this.indexName = indexName;
        }

        public Builder indexTypeName( final String indexTypeName )
        {
            this.indexTypeName = indexTypeName;
            return this;
        }

        public Builder from( final int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        public Builder addSort( final SortBuilder sortBuilder )
        {
            this.sortBuilders.add( sortBuilder );
            return this;
        }

        public Builder addField( final String fieldName )
        {
            this.fields.add( fieldName );
            return this;
        }

        public Builder addFields( final Set<String> fields )
        {
            this.fields.addAll( fields );
            return this;
        }

        public Builder addFields( final String... fields )
        {
            this.fields.addAll( Arrays.asList( fields ) );
            return this;
        }


        public QueryMetaData build()
        {
            return new QueryMetaData( this );
        }
    }


}
