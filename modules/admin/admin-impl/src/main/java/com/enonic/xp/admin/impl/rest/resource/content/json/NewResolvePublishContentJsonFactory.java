package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.enonic.xp.admin.impl.rest.resource.schema.IconUrlResolver;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Contents;

public class NewResolvePublishContentJsonFactory
{
    private final CompareContentResults compareContentResults;

    private final Contents requestedContents;

    private final Contents dependants;

    private final IconUrlResolver iconUrlResolver;

    private NewResolvePublishContentJsonFactory( Builder builder )
    {
        compareContentResults = builder.compareContentResults;
        requestedContents = builder.requestedContents;
        dependants = builder.dependants;
        iconUrlResolver = builder.iconUrlResolver;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private CompareContentResults compareContentResults;

        private Contents requestedContents;

        private Contents dependants;

        private IconUrlResolver iconUrlResolver;

        private Builder()
        {
        }

        public Builder compareContentResults( CompareContentResults compareContentResults )
        {
            this.compareContentResults = compareContentResults;
            return this;
        }

        public Builder requestedContents( Contents requestedContents )
        {
            this.requestedContents = requestedContents;
            return this;
        }

        public Builder dependants( Contents dependants )
        {
            this.dependants = dependants;
            return this;
        }

        public Builder iconUrlResolver( IconUrlResolver iconUrlResolver )
        {
            this.iconUrlResolver = iconUrlResolver;
            return this;
        }

        public NewResolvePublishContentJsonFactory build()
        {
            return new NewResolvePublishContentJsonFactory( this );
        }
    }
}
