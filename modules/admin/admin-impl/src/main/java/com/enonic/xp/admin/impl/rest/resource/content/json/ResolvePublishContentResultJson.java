package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;

public class ResolvePublishContentResultJson
{
    private final Collection<RequestedContentJson> requestedContents;

    private final List<ContentIdJson> dependentContents;

    private final List<ContentIdJson> requiredContents;

    private final Boolean containsInvalid;

    private ResolvePublishContentResultJson( Builder builder )
    {
        requestedContents = builder.requestedContents;
        dependentContents = builder.dependentContents.stream().map( item -> new ContentIdJson( item ) ).collect( Collectors.toList() );
        requiredContents = builder.requiredContents.stream().map( item -> new ContentIdJson( item ) ).collect( Collectors.toList() );
        containsInvalid = builder.containsInvalid;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @SuppressWarnings("unused")
    public Collection<RequestedContentJson> getRequestedContents()
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

    public static final class Builder
    {

        private Collection<RequestedContentJson> requestedContents;

        private ContentIds dependentContents;

        private ContentIds requiredContents;

        private Boolean containsInvalid;

        private Builder()
        {
        }

        public Builder setRequestedContents( final Collection<RequestedContentJson> requestedContents )
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

        public ResolvePublishContentResultJson build()
        {
            return new ResolvePublishContentResultJson( this );
        }
    }

    public static class RequestedContentJson {

        private ContentIdJson id;

        private Boolean hasChildren;

        public RequestedContentJson( final ContentId id, final Boolean hasChildren) {
            this.id = new ContentIdJson(id);
            this.hasChildren = hasChildren;
        }

        public ContentIdJson getId()
        {
            return id;
        }

        public void setId( final ContentIdJson id )
        {
            this.id = id;
        }

        public Boolean getHasChildren()
        {
            return hasChildren;
        }

        public void setHasChildren( final Boolean hasChildren )
        {
            this.hasChildren = hasChildren;
        }
    }
}
