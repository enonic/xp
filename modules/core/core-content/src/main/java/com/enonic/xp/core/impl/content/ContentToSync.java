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

    private final Context sourceCtx;

    private final Context targetCtx;

    public ContentToSync( final Builder builder )
    {
        this.sourceContent = builder.sourceContent;
        this.targetContent = builder.targetContent;
        this.sourceCtx = builder.sourceCtx;
        this.targetCtx = builder.targetCtx;
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

    public Context getSourceCtx()
    {
        return sourceCtx;
    }

    public Context getTargetCtx()
    {
        return targetCtx;
    }

    public static class Builder
    {
        private Content sourceContent;

        private Content targetContent;

        private Context sourceCtx;

        private Context targetCtx;

        private Builder()
        {

        }

        private Builder( final ContentToSync source )
        {
            this.sourceContent = source.sourceContent;
            this.targetContent = source.targetContent;
            this.sourceCtx = source.sourceCtx;
            this.targetCtx = source.targetCtx;
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

        public Builder sourceCtx( final Context sourceCtx )
        {
            this.sourceCtx = sourceCtx;
            return this;
        }

        public Builder targetCtx( final Context targetCtx )
        {
            this.targetCtx = targetCtx;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( sourceCtx, "sourceCtx is required" );
            Objects.requireNonNull( targetCtx, "targetCtx is required" );
            Preconditions.checkArgument( sourceContent != null || targetContent != null, "source or target content is required" );
        }

        public ContentToSync build()
        {
            validate();
            return new ContentToSync( this );
        }
    }
}
