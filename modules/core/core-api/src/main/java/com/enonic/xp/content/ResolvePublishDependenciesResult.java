package com.enonic.xp.content;

import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

@Beta
public class ResolvePublishDependenciesResult
{
    private ContentIds contentIds;

    private ResolvePublishDependenciesResult( final Builder builder )
    {
        this.contentIds = ContentIds.from( builder.contentIds );
    }

    public ContentIds contentIds()
    {
        return contentIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private List<ContentId> contentIds = Lists.newLinkedList();

        private Builder()
        {
        }

        public Builder add( final ContentId contentId )
        {
            this.contentIds.add( contentId );
            return this;
        }

        public Builder addAll( final ContentIds contentIds )
        {
            this.contentIds.addAll( contentIds.getSet() );
            return this;
        }

        public ResolvePublishDependenciesResult build()
        {
            return new ResolvePublishDependenciesResult( this );
        }
    }

}
