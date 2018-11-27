package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class ApplyContentPermissionsResult
{
    private final ContentIds succeedContents;

    private final ContentIds skippedContents;

    private ApplyContentPermissionsResult( Builder builder )
    {
        this.succeedContents = builder.succeedContents;
        this.skippedContents = builder.skippedContents;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getSucceedContents()
    {
        return succeedContents;
    }

    public ContentIds getSkippedContents()
    {
        return skippedContents;
    }

    public static final class Builder
    {
        private ContentIds succeedContents = ContentIds.empty();

        private ContentIds skippedContents = ContentIds.empty();

        private Builder()
        {
        }

        public Builder setSucceedContents( final ContentIds succeedContents )
        {
            this.succeedContents = succeedContents;
            return this;
        }

        public Builder setSkippedContents( final ContentIds skippedContents )
        {
            this.skippedContents = skippedContents;
            return this;
        }

        public ApplyContentPermissionsResult build()
        {
            return new ApplyContentPermissionsResult( this );
        }
    }
}
