package com.enonic.xp.suggester;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public abstract class Suggestion<ENTRY extends SuggestionEntry>
{
    private String name;

    private List<ENTRY> entries;

    protected Suggestion( final Builder builder )
    {
        this.name = builder.name;
        this.entries = ImmutableList.copyOf( builder.suggestionEntries );
    }

    public String getName()
    {
        return name;
    }

    public List<ENTRY> getEntries()
    {
        return entries;
    }

    public static abstract class Builder<T extends Builder, ENTRY extends SuggestionEntry>
    {
        public Builder( final String name )
        {
            this.name = name;
        }

        private final String name;

        private final List<ENTRY> suggestionEntries = Lists.newArrayList();

        @SuppressWarnings("unchecked")
        public T addSuggestionEntry( final ENTRY suggestionEntry )
        {
            this.suggestionEntries.add( suggestionEntry );
            return (T) this;
        }

        public abstract Suggestion<ENTRY> build();
    }
}
