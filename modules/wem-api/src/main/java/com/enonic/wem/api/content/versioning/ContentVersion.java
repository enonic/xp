package com.enonic.wem.api.content.versioning;

import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ContentId;

public final class ContentVersion
{
    private final ContentId contentId;

    private final DateTime created;

    private final UserKey creator;

    private final ContentVersionId versionId;

    //private Set<ContentVersionLabel> labels;

    private ContentVersion( final Builder builder )
    {
        this.contentId = builder.contentId;
        this.created = builder.created;
        this.creator = builder.creator;
        this.versionId = builder.versionId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public DateTime getCreated()
    {
        return created;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public ContentVersionId getVersionId()
    {
        return versionId;
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
        return Objects.equal( this.contentId, that.contentId ) && Objects.equal( this.versionId, that.versionId ) &&
            Objects.equal( this.creator, that.creator ) && Objects.equal( this.created, that.created );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentId, this.created, this.creator, this.versionId );
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "contentId", contentId );
        s.add( "versionId", versionId );
        s.add( "created", created );
        s.add( "creator", creator );
        return s.toString();
    }

    public static Builder newContentVersion()
    {
        return new Builder();
    }

    public static Builder newContentVersion( final ContentVersion contentVersion )
    {
        return new Builder( contentVersion );
    }

    public static class Builder
    {
        private ContentId contentId;

        private DateTime created;

        private UserKey creator;

        private ContentVersionId versionId;

        public Builder()
        {
            this.versionId = null;
            this.contentId = null;
            this.created = null;
            this.creator = null;
        }

        public Builder( final ContentVersion contentVersion )
        {
            this.versionId = contentVersion.versionId;
            this.contentId = contentVersion.contentId;
            this.created = contentVersion.created;
            this.creator = contentVersion.creator;
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder createdTime( final DateTime created )
        {
            this.created = created;
            return this;
        }

        public Builder creator( final UserKey creator )
        {
            this.creator = creator;
            return this;
        }

        public Builder versionId( final ContentVersionId versionId )
        {
            this.versionId = versionId;
            return this;
        }

        public ContentVersion build()
        {
            Preconditions.checkNotNull( versionId, "versionId is mandatory for ContentVersion" );
            Preconditions.checkNotNull( contentId, "contentId is mandatory for ContentVersion" );
            Preconditions.checkNotNull( created, "created time is mandatory for ContentVersion" );
            Preconditions.checkNotNull( creator, "creator user is mandatory for ContentVersion" );
            return new ContentVersion( this );
        }
    }
}
