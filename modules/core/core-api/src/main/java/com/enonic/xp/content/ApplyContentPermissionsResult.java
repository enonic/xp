package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class ApplyContentPermissionsResult
{
    private final ContentPaths succeedContents;

    private final ContentPaths skippedContents;

    private ApplyContentPermissionsResult( Builder builder )
    {
        this.succeedContents = builder.succeedContents;
        this.skippedContents = builder.skippedContents;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentPaths getSucceedContents()
    {
        return succeedContents;
    }

    public ContentPaths getSkippedContents()
    {
        return skippedContents;
    }

    public static final class Builder
    {
        private ContentPaths succeedContents = ContentPaths.empty();

        private ContentPaths skippedContents = ContentPaths.empty();

        private Builder()
        {
        }

        public Builder setSucceedContents( final ContentPaths succeedContents )
        {
            this.succeedContents = succeedContents;
            return this;
        }

        public Builder setSkippedContents( final ContentPaths skippedContents )
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
