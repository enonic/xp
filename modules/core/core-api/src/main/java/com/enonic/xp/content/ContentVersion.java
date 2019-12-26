package com.enonic.xp.content;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public class ContentVersion
    implements Comparable<ContentVersion>
{
    private final ContentVersionId id;

    private final PrincipalKey modifier;

    private final String displayName;

    private final Instant modified;

    private final String comment;

    private final ContentVersionPublishInfo publishInfo;

    private final WorkflowInfo workflowInfo;

    private ContentVersion( Builder builder )
    {
        this.modifier = builder.modifier;
        this.displayName = builder.displayName;
        this.modified = builder.modified;
        this.comment = builder.comment;
        this.id = builder.id;
        this.publishInfo = builder.publishInfo;
        this.workflowInfo = builder.workflowInfo;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Instant getModified()
    {
        return modified;
    }

    public String getComment()
    {
        return comment;
    }

    public ContentVersionId getId()
    {
        return id;
    }

    public ContentVersionPublishInfo getPublishInfo()
    {
        return publishInfo;
    }

    public WorkflowInfo getWorkflowInfo()
    {
        return workflowInfo;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public int compareTo( final ContentVersion o )
    {
        if ( Objects.equals( this.modified, o.modified ) )
        {
            return 0;
        }

        if ( this.modified.isBefore( o.modified ) )
        {
            return 1;
        }

        return -1;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentVersion ) )
        {
            return false;
        }

        final ContentVersion that = (ContentVersion) o;

        if ( !Objects.equals( comment, that.comment ) )
        {
            return false;
        }
        if ( !Objects.equals( displayName, that.displayName ) )
        {
            return false;
        }
        if ( !Objects.equals( modified, that.modified ) )
        {
            return false;
        }
        if ( !Objects.equals( modifier, that.modifier ) )
        {
            return false;
        }
        if ( !Objects.equals( publishInfo, that.publishInfo ) )
        {
            return false;
        }

        return Objects.equals( workflowInfo, that.workflowInfo );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( modifier, displayName, modified, comment, publishInfo, workflowInfo );
    }

    public static final class Builder
    {
        private PrincipalKey modifier;

        private String displayName;

        private Instant modified;

        private String comment;

        private ContentVersionId id;

        private ContentVersionPublishInfo publishInfo;

        private WorkflowInfo workflowInfo;

        private Builder()
        {
        }

        public Builder id( final ContentVersionId id )
        {
            this.id = id;
            return this;
        }

        public Builder modifier( PrincipalKey modifier )
        {
            this.modifier = modifier;
            return this;
        }

        public Builder displayName( String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder modified( Instant modified )
        {
            this.modified = modified;
            return this;
        }

        public Builder comment( String comment )
        {
            this.comment = comment;
            return this;
        }

        public Builder publishInfo( ContentVersionPublishInfo publishInfo )
        {
            this.publishInfo = publishInfo;
            return this;
        }

        public Builder workflowInfo( WorkflowInfo workflowInfo )
        {
            this.workflowInfo = workflowInfo;
            return this;
        }

        public ContentVersion build()
        {
            return new ContentVersion( this );
        }
    }
}
