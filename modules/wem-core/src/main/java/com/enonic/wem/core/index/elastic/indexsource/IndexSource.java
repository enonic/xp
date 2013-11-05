package com.enonic.wem.core.index.elastic.indexsource;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Deprecated
public class IndexSource
{
    private final ImmutableSet<IndexSourceItem> indexSourceItems;

    private IndexSource( final Builder builder )
    {
        this.indexSourceItems = ImmutableSet.copyOf( builder.indexSourceItems );
    }

    public Set<IndexSourceItem> indexSourceItems()
    {
        return indexSourceItems;
    }

    public static Builder newIndexSource()
    {
        return new Builder();
    }

    public List<IndexSourceItem> getIndexSourceEntryWithName( final String name )
    {
        List<IndexSourceItem> foundItems = Lists.newArrayList();

        for ( IndexSourceItem indexSourceItem : indexSourceItems )
        {
            if ( indexSourceItem.getKey().equals( name ) )
            {
                foundItems.add( indexSourceItem );
            }
        }

        return foundItems;
    }

    public static class Builder
    {
        private final Set<IndexSourceItem> indexSourceItems = Sets.newHashSet();

        public Builder addItem( final IndexSourceItem item )
        {
            this.indexSourceItems.add( item );
            return this;
        }


        public Builder addItems( final Collection<IndexSourceItem> items )
        {
            this.indexSourceItems.addAll( items );
            return this;
        }

        public IndexSource build()
        {
            return new IndexSource( this );
        }
    }

}
