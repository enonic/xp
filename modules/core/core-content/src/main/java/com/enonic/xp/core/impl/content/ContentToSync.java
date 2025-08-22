package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.context.Context;

public final class ContentToSync
{
    private final Content sourceContent;

    private final Content targetContent;

    private final Context sourceContext;

    private final Context targetContext;

    public ContentToSync( final Builder builder )
    {
        this.sourceContent = builder.sourceContent;
        this.targetContent = builder.targetContent;
        this.sourceContext = builder.sourceContext;
        this.targetContext = builder.targetContext;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final ContentToSync source )
    {
        return new Builder( source );
    }

    public ContentId getId()
    {
        return sourceContent != null ? sourceContent.getId() : targetContent.getId();
    }

    public Content getSourceContent()
    {
        return sourceContent;
    }

    public Content getTargetContent()
    {
        return targetContent;
    }

    public Context getSourceContext()
    {
        return sourceContext;
    }

    public Context getTargetContext()
    {
        return targetContext;
    }

    public static class Builder
    {
        private Content sourceContent;

        private Content targetContent;

        private Context sourceContext;

        private Context targetContext;

        private Builder()
        {

        }

        private Builder( final ContentToSync source )
        {
            this.sourceContent = source.sourceContent;
            this.targetContent = source.targetContent;
            this.sourceContext = source.sourceContext;
            this.targetContext = source.targetContext;
        }

        public Builder sourceContent( final Content sourceContent )
        {
            this.sourceContent = sourceContent;
            return this;
        }

        public Builder targetContent( final Content targetContent )
        {
            this.targetContent = targetContent;
            return this;
        }

        public Builder sourceContext( final Context sourceContext )
        {
            this.sourceContext = sourceContext;
            return this;
        }

        public Builder targetContext( final Context targetContext )
        {
            this.targetContext = targetContext;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( sourceContext, "sourceContext is required" );
            Objects.requireNonNull( targetContext, "targetContext is required" );
            Preconditions.checkArgument( sourceContent != null || targetContent != null, "source or target content is required" );
        }

        public ContentToSync build()
        {
            validate();
            return new ContentToSync( this );
        }
    }
}
