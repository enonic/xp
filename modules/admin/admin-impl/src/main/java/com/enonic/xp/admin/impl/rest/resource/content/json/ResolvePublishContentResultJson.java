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

    private final Boolean allPendingDelete;

    private final Boolean containsNotReady;

    private final List<ContentIdJson> invalidContents;

    private final List<ContentIdJson> notReadyContents;

    private ResolvePublishContentResultJson( Builder builder )
    {
        requestedContents = builder.requestedContents.stream().map( ContentIdJson::new ).collect( Collectors.toList() );
        dependentContents = builder.dependentContents.stream().map( ContentIdJson::new ).collect( Collectors.toList() );
        requiredContents = builder.requiredContents.stream().map( ContentIdJson::new ).collect( Collectors.toList() );
        containsInvalid = builder.containsInvalid;
        allPublishable = builder.allPublishable;
        allPendingDelete = builder.allPendingDelete;
        containsNotReady = builder.containsNotReady;
        invalidContents = builder.invalidContents.stream().map( ContentIdJson::new ).collect( Collectors.toList() );
        notReadyContents = builder.notReadyContents.stream().map( ContentIdJson::new ).collect( Collectors.toList() );
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

    public Boolean isAllPendingDelete()
    {
        return allPendingDelete;
    }

    public Boolean getContainsNotReady()
    {
        return containsNotReady;
    }

    public List<ContentIdJson> getInvalidContents()
    {
        return invalidContents;
    }

    public List<ContentIdJson> getNotReadyContents()
    {
        return notReadyContents;
    }

    public static final class Builder
    {

        private ContentIds requestedContents;

        private ContentIds dependentContents;

        private ContentIds requiredContents;

        private Boolean containsInvalid;

        private Boolean allPublishable;

        private Boolean allPendingDelete;

        private Boolean containsNotReady;

        private ContentIds invalidContents;

        private ContentIds notReadyContents;

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

        public Builder setAllPendingDelete( final Boolean allPendingDelete )
        {
            this.allPendingDelete = allPendingDelete;
            return this;
        }

        public Builder setContainsNotReady( final Boolean containsNotReady )
        {
            this.containsNotReady = containsNotReady;
            return this;
        }

        public Builder setInvalidContents( final ContentIds items )
        {
            this.invalidContents = items;
            return this;
        }

        public Builder setNotReadyContents( final ContentIds items )
        {
            this.notReadyContents = items;
            return this;
        }

        public ResolvePublishContentResultJson build()
        {
            return new ResolvePublishContentResultJson( this );
        }
    }
}
