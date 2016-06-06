package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.admin.impl.json.content.ContentListMetaDataJson;
import com.enonic.xp.content.ContentListMetaData;

public class ResolvePublishContentResultJson
{
    private final List<ContentPublishItemJson> requestedContents;

    private final List<ContentPublishItemJson> dependentContents;

    private final ContentListMetaDataJson metadata;

    private final Boolean containsRemovable;

    private ResolvePublishContentResultJson( Builder builder )
    {
        requestedContents = builder.requestedContents;
        dependentContents = builder.dependentContents;
        metadata = builder.metadata;
        containsRemovable = builder.containsRemovable;
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

    @SuppressWarnings("unused")
    public ContentListMetaDataJson getMetadata()
    {
        return metadata;
    }

    public Boolean getContainsRemovable()
    {
        return containsRemovable;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private List<ContentPublishItemJson> requestedContents = Lists.newLinkedList();

        private List<ContentPublishItemJson> dependentContents = Lists.newLinkedList();

        private ContentListMetaDataJson metadata;

        private Boolean containsRemovable;

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

        public Builder setMetadata( final ContentListMetaData metadata )
        {
            this.metadata = new ContentListMetaDataJson( metadata );
            return this;
        }

        public Builder setContainsRemovable( final Boolean containsRemovable )
        {
            this.containsRemovable = containsRemovable;
            return this;
        }

        public ResolvePublishContentResultJson build()
        {
            return new ResolvePublishContentResultJson( this );
        }
    }
}
