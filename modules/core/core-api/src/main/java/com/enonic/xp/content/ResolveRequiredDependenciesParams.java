package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public class ResolveRequiredDependenciesParams
{
    private final ContentIds contentIds;


    private ResolveRequiredDependenciesParams( Builder builder )
    {
        contentIds = builder.contentIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getTarget()
    {
        return ContentConstants.BRANCH_MASTER;
    }

    public static final class Builder
    {
        private ContentIds contentIds;


        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        @Deprecated
        public Builder target( Branch target )
        {
            return this;
        }

        public ResolveRequiredDependenciesParams build()
        {
            return new ResolveRequiredDependenciesParams( this );
        }
    }
}
