package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItems
{
    private final Map<String, Collection<Object>> values;

    private IndexItems( Builder builder )
    {
        this.values = ImmutableMultimap.copyOf( builder.values ).asMap();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Map<String, Collection<Object>> asValuesMap()
    {
        return values;
    }

    public static final class Builder
    {
        private final Multimap<String, Object> values = ArrayListMultimap.create();

        private Builder()
        {
        }

        public Builder add( final IndexPath indexPath, final Value value, final IndexConfigDocument indexConfigDocument )
        {
            Objects.requireNonNull( indexPath );
            Objects.requireNonNull( value );

            add( IndexItemFactory.create( indexPath, value, indexConfigDocument ) );

            return this;
        }

        public Builder add( final List<IndexItem> indexItems )
        {
            indexItems.stream()
                .filter( this::singleOrderByValueOnlyFilter )
                .forEach( ( item ) -> this.values.put( item.getPath(), item.getValue().getValue() ) );

            return this;
        }

        private boolean singleOrderByValueOnlyFilter( final IndexItem item )
        {
            final boolean isOrderByItem = item.valueType().equals( IndexValueType.ORDERBY );

            return !isOrderByItem || !this.values.containsKey( item.getPath() );
        }

        public IndexItems build()
        {
            return new IndexItems( this );
        }
    }
}
