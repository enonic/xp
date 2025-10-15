package com.enonic.xp.content;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.util.Attributes;

@PublicApi
public final class ContentVersion
{
    private final ContentVersionId id;

    private final PrincipalKey modifier;

    private final String displayName;

    private final ContentPath path;

    private final Instant modified;

    private final Instant timestamp;

    private final ChildOrder childOrder;

    private final String comment;

    private final ContentVersionCommitInfo commitInfo;

    private final ContentPublishInfo publishInfo;

    private final WorkflowInfo workflowInfo;

    private final AccessControlList permissions;

    private final Attributes attributes;

    private ContentVersion( Builder builder )
    {
        this.modifier = builder.modifier;
        this.displayName = builder.displayName;
        this.path = builder.path;
        this.modified = builder.modified;
        this.comment = builder.comment;
        this.timestamp = builder.timestamp;
        this.childOrder = builder.childOrder;
        this.id = builder.id;
        this.publishInfo = builder.publishInfo;
        this.commitInfo = builder.commitInfo;
        this.workflowInfo = builder.workflowInfo;
        this.permissions = builder.permissions;
        this.attributes = builder.attributes;
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

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public ContentVersionId getId()
    {
        return id;
    }

    public ContentPublishInfo getPublishInfo()
    {
        return publishInfo;
    }

    public ContentVersionCommitInfo getCommitInfo()
    {
        return commitInfo;
    }

    public WorkflowInfo getWorkflowInfo()
    {
        return workflowInfo;
    }

    public ContentPath getPath()
    {
        return path;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public Attributes getAttributes()
    {
        return attributes;
    }

    public static Builder create()
    {
        return new Builder();
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
        return Objects.equals( id, that.id ) && Objects.equals( modifier, that.modifier ) &&
            Objects.equals( displayName, that.displayName ) && Objects.equals( modified, that.modified ) &&
            Objects.equals( timestamp, that.timestamp ) && Objects.equals( childOrder, that.childOrder ) &&
            Objects.equals( comment, that.comment ) && Objects.equals( publishInfo, that.publishInfo ) &&
            Objects.equals( commitInfo, that.commitInfo ) && Objects.equals( workflowInfo, that.workflowInfo ) &&
            Objects.equals( permissions, that.permissions ) && Objects.equals( path, that.path ) &&
            Objects.equals( attributes, that.attributes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, modifier, displayName, modified, timestamp, childOrder, comment, publishInfo, commitInfo, workflowInfo,
                             path, permissions, attributes );
    }

    public static final class Builder
    {
        private PrincipalKey modifier;

        private String displayName;

        private ContentPath path;

        private Instant modified;

        private Instant timestamp;

        private ChildOrder childOrder;

        private String comment;

        private ContentVersionId id;

        private ContentPublishInfo publishInfo;

        private ContentVersionCommitInfo commitInfo;

        private WorkflowInfo workflowInfo;

        private AccessControlList permissions;

        private Attributes attributes;

        private Builder()
        {
        }

        public Builder id( final ContentVersionId id )
        {
            this.id = id;
            return this;
        }

        public Builder modifier( final PrincipalKey modifier )
        {
            this.modifier = modifier;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder path( final ContentPath path )
        {
            this.path = path;
            return this;
        }

        public Builder modified( final Instant modified )
        {
            this.modified = modified;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder comment( final String comment )
        {
            this.comment = comment;
            return this;
        }

        public Builder publishInfo( final ContentPublishInfo publishInfo )
        {
            this.publishInfo = publishInfo;
            return this;
        }

        public Builder commitInfo( final ContentVersionCommitInfo commitInfo )
        {
            this.commitInfo = commitInfo;
            return this;
        }

        public Builder attributes( final Attributes attributes )
        {
            this.attributes = attributes;
            return this;
        }

        public Builder workflowInfo( final WorkflowInfo workflowInfo )
        {
            this.workflowInfo = workflowInfo;
            return this;
        }

        public Builder permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public ContentVersion build()
        {
            return new ContentVersion( this );
        }
    }
}
