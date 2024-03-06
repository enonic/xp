package com.enonic.xp.content;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public final class ContentVersion
    implements Comparable<ContentVersion>
{
    private final ContentVersionId id;

    private final PrincipalKey modifier;

    private final String displayName;

    private final ContentPath path;

    private final Instant modified;

    private final Instant timestamp;

    private final ChildOrder childOrder;

    private final String comment;

    private final ContentVersionPublishInfo publishInfo;

    private final WorkflowInfo workflowInfo;

    private final AccessControlList permissions;

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
        this.workflowInfo = builder.workflowInfo;
        this.permissions = builder.permissions;
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

    public ContentVersionPublishInfo getPublishInfo()
    {
        return publishInfo;
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

    @Deprecated
    public boolean isInheritPermissions()
    {
        return false;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Deprecated
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
        return Objects.equals( id, that.id ) && Objects.equals( modifier, that.modifier ) &&
            Objects.equals( displayName, that.displayName ) && Objects.equals( modified, that.modified ) &&
            Objects.equals( timestamp, that.timestamp ) && Objects.equals( childOrder, that.childOrder ) &&
            Objects.equals( comment, that.comment ) && Objects.equals( publishInfo, that.publishInfo ) &&
            Objects.equals( workflowInfo, that.workflowInfo ) && Objects.equals( permissions, that.permissions ) &&
            Objects.equals( path, that.path );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, modifier, displayName, modified, timestamp, childOrder, comment, publishInfo, workflowInfo, path,
                             permissions );
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

        private ContentVersionPublishInfo publishInfo;

        private WorkflowInfo workflowInfo;

        private AccessControlList permissions;

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

        public Builder path( ContentPath path )
        {
            this.path = path;
            return this;
        }

        public Builder modified( Instant modified )
        {
            this.modified = modified;
            return this;
        }

        public Builder timestamp( Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder childOrder( ChildOrder childOrder )
        {
            this.childOrder = childOrder;
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

        public Builder permissions( AccessControlList permissions )
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
