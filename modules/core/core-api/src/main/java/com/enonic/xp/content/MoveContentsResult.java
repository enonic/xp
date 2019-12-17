package com.enonic.xp.content;

import java.util.ArrayList;
import java.util.List;

import com.google.common.annotations.Beta;

@Beta
public class MoveContentsResult
{
    private final ContentIds movedContents;

    private final String contentName;

    private MoveContentsResult( Builder builder )
    {
        this.movedContents = ContentIds.from( builder.movedContents );
        this.contentName = builder.contentName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getMovedContents()
    {
        return movedContents;
    }

    public String getContentName()
    {
        return contentName;
    }

    public static final class Builder
    {
        private List<ContentId> movedContents = new ArrayList<>();

        private String contentName;

        private Builder()
        {
        }

        public Builder addMoved( final ContentId contentId )
        {
            this.movedContents.add( contentId );
            return this;
        }

        public Builder addMoved( final ContentIds contentIds )
        {
            this.movedContents.addAll( contentIds.getSet() );
            return this;
        }

        public Builder setContentName( final String contentName )
        {
            this.contentName = contentName;
            return this;
        }

        public MoveContentsResult build()
        {
            return new MoveContentsResult( this );
        }
    }
}
