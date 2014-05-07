package com.enonic.wem.core.elastic;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;

class NodeStorageDocument
{
    private final ImmutableSet<NodeStorageDocumentEntry> entries;

    private final Index index;

    private final IndexType indexType;

    private final String id;

    private NodeStorageDocument( final Builder builder )
    {
        this.id = builder.id;
        this.index = builder.index;
        this.indexType = builder.indexType;
        this.entries = ImmutableSet.copyOf( builder.entries );
    }

    Index getIndex()
    {
        return index;
    }

    IndexType getIndexType()
    {
        return indexType;
    }

    ImmutableSet<NodeStorageDocumentEntry> getEntries()
    {
        return entries;
    }

    String getId()
    {
        return id;
    }

    static Builder newDocument()
    {
        return new Builder();
    }

    static class Builder
    {
        private Set<NodeStorageDocumentEntry> entries = Sets.newHashSet();

        private Index index;

        private IndexType indexType;

        private String id;

        public Builder add( final String fieldName, final Object value )
        {
            entries.add( new NodeStorageDocumentEntry( fieldName, value ) );
            return this;
        }

        public Builder index( final Index index )
        {
            this.index = index;
            return this;
        }

        public Builder indexType( final IndexType indexType )
        {
            this.indexType = indexType;
            return this;
        }

        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        public NodeStorageDocument build()
        {
            return new NodeStorageDocument( this );
        }

    }


}
