package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.context.Context;

public final class ContentEventSyncCommandParams
{
    private final Content sourceContent;

    private final Content targetContent;

    private final Context sourceContext;

    private final Context targetContext;

    public ContentEventSyncCommandParams( Builder builder )
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

    public static final class Builder
    {
        private Content sourceContent;

        private Content targetContent;

        private Context sourceContext;

        private Context targetContext;

        public Builder sourceContent( Content sourceContent )
        {
            this.sourceContent = sourceContent;
            return this;
        }

        public Builder targetContent( Content targetContent )
        {
            this.targetContent = targetContent;
            return this;
        }

        public Builder sourceContext( Context sourceContext )
        {
            this.sourceContext = sourceContext;
            return this;
        }

        public Builder targetContext( Context targetContext )
        {
            this.targetContext = targetContext;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( sourceContext, "sourceContext must be set." );
            Preconditions.checkNotNull( targetContext, "targetContext must be set." );
            Preconditions.checkArgument( sourceContent != null || targetContent != null,
                                         "either sourceContent or targetContent must be set." );
        }

        public ContentEventSyncCommandParams build()
        {
            validate();
            return new ContentEventSyncCommandParams( this );
        }

    }
}
