package com.enonic.xp.query.highlight;

import java.util.Objects;

import com.enonic.xp.query.highlight.constants.Encoder;
import com.enonic.xp.query.highlight.constants.TagsSchema;

public final class HighlightQuerySettings
    extends HighlightPropertySettings
{
    private final Encoder encoder;

    private final TagsSchema tagsSchema;

    private HighlightQuerySettings( final Builder builder )
    {
        super( builder );
        this.encoder = builder.encoder;
        this.tagsSchema = builder.tagsSchema;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public Encoder getEncoder()
    {
        return encoder;
    }

    public TagsSchema getTagsSchema()
    {
        return tagsSchema;
    }

    public static HighlightQuerySettings empty()
    {
        return create().build();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final HighlightQuerySettings that = (HighlightQuerySettings) o;
        return encoder == that.encoder && tagsSchema == that.tagsSchema;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), encoder, tagsSchema );
    }

    public static final class Builder
        extends HighlightPropertySettings.Builder<Builder>
    {
        private Encoder encoder;

        private TagsSchema tagsSchema;

        public Builder encoder( final Encoder encoder )
        {
            this.encoder = encoder;
            return this;
        }

        public Builder tagsSchema( final TagsSchema tagsSchema )
        {
            this.tagsSchema = tagsSchema;
            return this;
        }

        @Override
        public HighlightQuerySettings build()
        {
            return new HighlightQuerySettings( this );
        }
    }
}
