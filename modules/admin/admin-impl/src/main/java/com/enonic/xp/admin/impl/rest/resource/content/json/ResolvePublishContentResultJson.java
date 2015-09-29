package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.google.common.collect.Lists;

public class ResolvePublishContentResultJson
{
    private final List<ContentPublishItemJson> requestedContents;

    private final List<ContentPublishItemJson> dependentContents;

    private ResolvePublishContentResultJson( Builder builder )
    {
        requestedContents = builder.requestedContents;
        dependentContents = builder.dependentContents;
    }

    public List<ContentPublishItemJson> getRequestedContents()
    {
        return requestedContents;
    }

    public List<ContentPublishItemJson> getDependentContents()
    {
        return dependentContents;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private final List<ContentPublishItemJson> requestedContents = Lists.newLinkedList();

        private final List<ContentPublishItemJson> dependentContents = Lists.newLinkedList();

        private Builder()
        {
        }

        public Builder addRequested( final ContentPublishItemJson requested )
        {
            this.requestedContents.add( requested );
            return this;
        }

        public Builder addDependent( final ContentPublishItemJson dependent )
        {
            this.dependentContents.add( dependent );
            return this;
        }

        public ResolvePublishContentResultJson build()
        {
            return new ResolvePublishContentResultJson( this );
        }
    }
}
