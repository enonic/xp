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

    @SuppressWarnings("unused")
    public List<ContentPublishItemJson> getRequestedContents()
    {
        return requestedContents;
    }

    @SuppressWarnings("unused")
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

        private List<ContentPublishItemJson> requestedContents = Lists.newLinkedList();

        private List<ContentPublishItemJson> dependentContents = Lists.newLinkedList();

        private Builder()
        {
        }

        public Builder setRequestedContents( final List<ContentPublishItemJson> requestedContents )
        {
            this.requestedContents = requestedContents;
            return this;
        }

        public Builder setDependentContents( final List<ContentPublishItemJson> dependentContents )
        {
            this.dependentContents = dependentContents;
            return this;
        }

        public ResolvePublishContentResultJson build()
        {
            return new ResolvePublishContentResultJson( this );
        }
    }
}
