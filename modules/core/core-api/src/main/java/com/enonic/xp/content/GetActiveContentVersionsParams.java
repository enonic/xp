package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branches;

@PublicApi
public class GetActiveContentVersionsParams
{
    private final ContentId contentId;

    private final Branches branches;

    private GetActiveContentVersionsParams( Builder builder )
    {
        contentId = builder.contentId;
        branches = builder.branches;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Branches getBranches()
    {
        return branches;
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
        final GetActiveContentVersionsParams that = (GetActiveContentVersionsParams) o;
        return Objects.equals( contentId, that.contentId ) && Objects.equals( branches, that.branches );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( contentId, branches );
    }

    public static final class Builder
    {
        private ContentId contentId;

        private Branches branches;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder branches( Branches branches )
        {
            this.branches = branches;
            return this;
        }

        public GetActiveContentVersionsParams build()
        {
            return new GetActiveContentVersionsParams( this );
        }
    }
}
