package com.enonic.xp.repo.impl.index.document;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItems
{
    private final Multimap<String, IndexValue> indexItemsMap = ArrayListMultimap.create();

    public void add( final Property property, final IndexConfig indexConfig )
    {
        final List<IndexItem> indexItems = IndexItemFactory.create( property, indexConfig );

        add( indexItems );
    }

    public void add( final String propertyName, final Value value, final IndexConfig indexConfig )
    {
        final List<IndexItem> indexItems = IndexItemFactory.create( propertyName, value, indexConfig );

        add( indexItems );
    }

    private void add( final List<IndexItem> indexItems )
    {
        indexItems.stream().
            filter( this::singleOrderByValueOnlyFilter ).
            forEach( ( item ) -> this.indexItemsMap.put( item.getKey(), item.getValue() ) );
    }

    private boolean singleOrderByValueOnlyFilter( final IndexItem item )
    {
        final boolean isOrderByItem = item.valueType().equals( IndexValueType.ORDERBY );

        return !isOrderByItem || !this.indexItemsMap.containsKey( item.getKey() );
    }

    public Collection<IndexValue> get( final String key )
    {
        return this.indexItemsMap.get( key );
    }

}
