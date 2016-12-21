package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.content.ContentIds;

public class ResolvePublishContentResultJson
{
    private final List<ContentIdJson> requestedContents;

    private final List<ContentIdJson> dependentContents;

    private final Boolean containsRemovable;

    private final Boolean allContentsAreValid;

    private ResolvePublishContentResultJson( Builder builder )
    {
        requestedContents = builder.requestedContents.stream().map( item -> new ContentIdJson( item ) ).collect( Collectors.toList() );
        dependentContents = builder.dependentContents.stream().map( item -> new ContentIdJson( item ) ).collect( Collectors.toList() );
        containsRemovable = builder.containsRemovable;
        allContentsAreValid = builder.allContentsAreValid;
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

    @SuppressWarnings("unused")
    public Boolean getContainsRemovable()
    {
        return containsRemovable;
    }

    @SuppressWarnings("unused")
    public Boolean getAllContentsAreValid()
    {
        return allContentsAreValid;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private ContentIds requestedContents;

        private ContentIds dependentContents;

        private Boolean containsRemovable;

        private Boolean allContentsAreValid;

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

        public Builder setContainsRemovable( final Boolean containsRemovable )
        {
            this.containsRemovable = containsRemovable;
            return this;
        }

        public Builder setAllContentsAreValid( final Boolean allContentsAreValid )
        {
            this.allContentsAreValid = allContentsAreValid;
            return this;
        }

        public ResolvePublishContentResultJson build()
        {
            return new ResolvePublishContentResultJson( this );
        }
    }
}
