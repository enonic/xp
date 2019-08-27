package com.enonic.xp.admin.impl.json.content;

import java.time.Instant;

import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.GetPublishStatusResult;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.layer.ContentLayer;

@SuppressWarnings("UnusedDeclaration")
public class ContentInLayerJson
    extends ContentIdJson
{
    private final Content content;

    private final ContentLayer layer;

    private final CompareContentResult compareResult;

    private final GetPublishStatusResult publishResult;

    public ContentInLayerJson( final Builder builder )
    {
        super( builder.content.getId() );
        this.content = builder.content;
        this.layer = builder.layer;
        this.compareResult = builder.compareResult;
        this.publishResult = builder.publishResult;
    }

    public String getPath()
    {
        return content.getPath().toString();
    }

    public String getName()
    {
        return content.getName().toString();
    }

    public String getDisplayName()
    {
        return content.getDisplayName();
    }

    public String getLanguage()
    {
        return content.getLanguage() != null ? content.getLanguage().toString() : null;
    }

    //TODO: remove after 'icon' field will start to work
    public String getLayerLanguage()
    {
        return layer.getLanguage() != null ? layer.getLanguage().toString() : null;
    }

    public CompareContentResultJson getStatus()
    {
        return new CompareContentResultJson( compareResult, publishResult );
    }

    public Instant getPublishFirstTime()
    {
        return content.getPublishInfo() != null ? content.getPublishInfo().getFirst() : null;
    }

    public Boolean getInherited()
    {
        return content.getInherited();
    }

    public String getLayer()
    {
        return layer.getName().toString();
    }

    public String getParentLayer()
    {
        return layer.getParentName() != null ? layer.getParentName().toString() : null;
    }

    public String getLayerDisplayName()
    {
        return layer.getDisplayName();
    }

    public AttachmentJson getIcon()
    {
        return layer.getIcon() != null ? new AttachmentJson( layer.getIcon() ) : null;
    }

    public WorkflowInfo getWorkflowInfo() { return content.getWorkflowInfo(); }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Content content;

        private ContentLayer layer;

        private CompareContentResult compareResult;

        private GetPublishStatusResult publishResult;

        private Builder()
        {
        }

        public Builder content( final Content value )
        {
            this.content = value;
            return this;
        }

        public Builder layer( final ContentLayer value )
        {
            this.layer = value;
            return this;
        }

        public Builder compareResult( final CompareContentResult value )
        {
            this.compareResult = value;
            return this;
        }

        public Builder publishResult( final GetPublishStatusResult value )
        {
            this.publishResult = value;
            return this;
        }

        public ContentInLayerJson build()
        {
            return new ContentInLayerJson( this );
        }
    }

}
