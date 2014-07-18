package com.enonic.wem.core.elasticsearch;

import java.util.Arrays;
import java.util.Set;

import org.elasticsearch.search.sort.SortBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;

public class QueryMetaData
{
    private final Index index;

    private final IndexType indexType;

    private final int from;

    private final int size;

    private final ImmutableSet<String> fields;

    private final ImmutableSet<SortBuilder> sortBuilders;

    private QueryMetaData( final Builder builder )
    {
        this.index = builder.index;
        this.indexType = builder.indexType;
        this.fields = ImmutableSet.copyOf( builder.fields );
        this.from = builder.from;
        this.size = builder.size;
        this.sortBuilders = ImmutableSet.copyOf( builder.sortBuilders );
    }

    public String getIndex()
    {
        return index.getName();
    }

    public String getIndexType()
    {
        return indexType.getName();
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

    public static Builder create( final Index index )
    {
        return new Builder( index );
    }


    public static class Builder
    {
        private Index index;

        private IndexType indexType;

        private int from = 0;

        private int size = 10;

        private Set<String> fields = Sets.newHashSet();

        private final Set<SortBuilder> sortBuilders = Sets.newHashSet();

        public Builder( final Index index )
        {
            this.index = index;
        }

        public Builder indexType( final IndexType indexType )
        {
            this.indexType = indexType;
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
