package com.enonic.xp.suggester;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

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

    public abstract static class Builder<T extends Builder, ENTRY extends SuggestionEntry>
    {
        private final String name;

        private final List<ENTRY> suggestionEntries = new ArrayList<>();

        public Builder( final String name )
        {
            this.name = name;
        }

        @SuppressWarnings("unchecked")
        public T addSuggestionEntry( final ENTRY suggestionEntry )
        {
            this.suggestionEntries.add( suggestionEntry );
            return (T) this;
        }

        public abstract Suggestion<ENTRY> build();
    }
}
