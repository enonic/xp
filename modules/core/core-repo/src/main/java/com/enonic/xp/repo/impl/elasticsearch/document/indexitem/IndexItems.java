package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItems
    implements Iterable<String>
{
    private final ImmutableMultimap<String, IndexValue> indexItemsMap;

    private IndexItems( Builder builder )
    {
        indexItemsMap = ImmutableMultimap.copyOf( builder.indexItemsMap );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Iterator<String> iterator()
    {
        return this.indexItemsMap.keySet().iterator();
    }

    public Collection<IndexValue> get( final String key )
    {
        return this.indexItemsMap.get( key );
    }

    public static final class Builder
    {
        private final Multimap<String, IndexValue> indexItemsMap = ArrayListMultimap.create();

        private Builder()
        {
        }

        public Builder add( final Property property, final IndexConfigDocument indexConfigDocument )
        {
            if ( property.getValue() == null )
            {
                return this;
            }

            final List<IndexItem> indexItems = IndexItemFactory.create( property, indexConfigDocument );

            add( indexItems );

            return this;
        }

        public Builder add( final IndexPath indexPath, final Value value, final IndexConfigDocument indexConfigDocument )
        {
            return doAdd( indexPath, value, indexConfigDocument );
        }

        public Builder add( final List<IndexItem> indexItems )
        {
            return doAdd( indexItems );
        }

        private Builder doAdd( final IndexPath indexPath, final Value value, final IndexConfigDocument indexConfigDocument )
        {
            if ( value == null )
            {
                return this;
            }

            final List<IndexItem> indexItems = IndexItemFactory.create( indexPath, value, indexConfigDocument );

            add( indexItems );

            return this;
        }

        private Builder doAdd( final List<IndexItem> indexItems )
        {
            indexItems.stream().
                filter( this::singleOrderByValueOnlyFilter ).
                forEach( ( item ) -> this.indexItemsMap.put( item.getPath(), item.getValue() ) );

            return this;
        }

        private boolean singleOrderByValueOnlyFilter( final IndexItem item )
        {
            final boolean isOrderByItem = item.valueType().equals( IndexValueType.ORDERBY );

            return !isOrderByItem || !this.indexItemsMap.containsKey( item.getPath() );
        }

        public IndexItems build()
        {
            return new IndexItems( this );
        }
    }
}
