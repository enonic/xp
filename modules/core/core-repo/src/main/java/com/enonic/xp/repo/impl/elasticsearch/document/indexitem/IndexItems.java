package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.repo.impl.index.StaticIndexValueType;

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

        public Builder add( final List<? extends IndexItem<?>> indexItems )
        {
            indexItems.stream()
                .filter( item -> !( item.valueType().equals( StaticIndexValueType.ORDERBY ) && this.values.containsKey( item.getPath() ) ) )
                .forEach( item -> this.values.put( item.getPath(), item.getValue() ) );

            return this;
        }

        public IndexItems build()
        {
            return new IndexItems( this );
        }
    }
}
