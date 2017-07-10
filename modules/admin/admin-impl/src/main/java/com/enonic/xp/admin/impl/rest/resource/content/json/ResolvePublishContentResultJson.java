package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.content.ContentIds;

public class ResolvePublishContentResultJson
{
    private final List<ContentIdJson> requestedContents;

    private final List<ContentIdJson> dependentContents;

    private final List<ContentIdJson> requiredContents;

    private final Boolean containsInvalid;

    private final Boolean allPublishable;

    private final Boolean anyPublishable;

    private ResolvePublishContentResultJson( Builder builder )
    {
        requestedContents = builder.requestedContents.stream().map( item -> new ContentIdJson( item ) ).collect( Collectors.toList() );
        dependentContents = builder.dependentContents.stream().map( item -> new ContentIdJson( item ) ).collect( Collectors.toList() );
        requiredContents = builder.requiredContents.stream().map( item -> new ContentIdJson( item ) ).collect( Collectors.toList() );
        containsInvalid = builder.containsInvalid;
        allPublishable = builder.allPublishable;
        anyPublishable = builder.anyPublishable;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @SuppressWarnings("unused")
    public List<ContentIdJson> getRequestedContents()
    {
        return requestedContents;
    }

    @SuppressWarnings("unused")
    public List<ContentIdJson> getDependentContents()
    {
        return dependentContents;
    }

    public List<ContentIdJson> getRequiredContents()
    {
        return requiredContents;
    }

    @SuppressWarnings("unused")
    public Boolean getContainsInvalid()
    {
        return containsInvalid;
    }

    public Boolean isAllPublishable()
    {
        return allPublishable;
    }

    public Boolean isAnyPublishable()
    {
        return anyPublishable;
    }

    public static final class Builder
    {

        private ContentIds requestedContents;

        private ContentIds dependentContents;

        private ContentIds requiredContents;

        private Boolean containsInvalid;

        private Boolean allPublishable;

        private Boolean anyPublishable;

        private Builder()
        {
        }

        public Builder setRequestedContents( final ContentIds requestedContents )
        {
            this.requestedContents = requestedContents;
            return this;
        }

        public Builder setDependentContents( final ContentIds dependentContents )
        {
            this.dependentContents = dependentContents;
            return this;
        }

        public Builder setRequiredContents( final ContentIds requiredContents )
        {
            this.requiredContents = requiredContents;
            return this;
        }

        public Builder setContainsInvalid( final Boolean containsInvalid )
        {
            this.containsInvalid = containsInvalid;
            return this;
        }

        public Builder setAllPublishable( final Boolean allPublishable )
        {
            this.allPublishable = allPublishable;
            return this;
        }

        public Builder setAnyPublishable( final Boolean anyPublishable )
        {
            this.anyPublishable = anyPublishable;
            return this;
        }

        public ResolvePublishContentResultJson build()
        {
            return new ResolvePublishContentResultJson( this );
        }
    }
}
