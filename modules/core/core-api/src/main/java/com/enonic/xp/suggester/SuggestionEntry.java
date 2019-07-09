package com.enonic.xp.suggester;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public abstract class SuggestionEntry<OPTION extends SuggestionOption>
{
    private String text;

    private Integer offset;

    private Integer length;

    private List<OPTION> options;

    protected SuggestionEntry( final Builder builder )
    {
        this.text = builder.text;
        this.offset = builder.offset;
        this.length = builder.length;
        this.options = ImmutableList.copyOf( builder.suggestionOptions );
    }

    public String getText()
    {
        return text;
    }

    public Integer getOffset()
    {
        return offset;
    }

    public Integer getLength()
    {
        return length;
    }

    public List<OPTION> getOptions()
    {
        return options;
    }

    public static Builder create()
    {
        throw new NotImplementedException( "Must be implemented in inheritors" );
    }

    public static abstract class Builder<T extends Builder, OPTION extends SuggestionOption>
    {
        private String text;

        private Integer offset;

        private Integer length;

        private List<OPTION> suggestionOptions = Lists.newArrayList();

        @SuppressWarnings("unchecked")
        public T text( final String text )
        {
            this.text = text;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T offset( final Integer offset )
        {
            this.offset = offset;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T length( final Integer length )
        {
            this.length = length;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addSuggestionOption( final OPTION suggestionOption )
        {
            this.suggestionOptions.add( suggestionOption );
            return (T) this;
        }

        public abstract SuggestionEntry<OPTION> build();
    }
}
