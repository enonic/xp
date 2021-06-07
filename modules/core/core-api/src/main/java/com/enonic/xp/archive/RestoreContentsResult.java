package com.enonic.xp.archive;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;

@PublicApi
public class RestoreContentsResult
{
    private final ContentIds restoredContents;

    private RestoreContentsResult( Builder builder )
    {
        this.restoredContents = ContentIds.from( builder.restoredContents.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getRestoredContents()
    {
        return restoredContents;
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentId> restoredContents = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder addRestored( final ContentId contentId )
        {
            this.restoredContents.add( contentId );
            return this;
        }

        public Builder addRestored( final ContentIds contentIds )
        {
            this.restoredContents.addAll( contentIds.getSet() );
            return this;
        }

        public RestoreContentsResult build()
        {
            return new RestoreContentsResult( this );
        }
    }
}
