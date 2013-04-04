package com.enonic.wem.api.content;

import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

public final class Content
    implements IllegalEditAware<Content>
{
    private final String displayName;

    private final QualifiedContentTypeName type;

    private final ContentPath path;

    private final ContentId id;

    private final RootDataSet rootDataSet;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final UserKey owner;

    private final UserKey modifier;

    private final ContentVersionId versionId;

    private Content( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.type = builder.type;
        this.path = builder.path;
        this.id = builder.contentId;
        this.rootDataSet = builder.rootDataSet;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.owner = builder.owner;
        this.modifier = builder.modifier;
        this.versionId = builder.versionId;
    }

    public ContentPath getPath()
    {
        return path;
    }

    public boolean isEmbedded()
    {
        return path.isPathToEmbeddedContent();
    }

    public QualifiedContentTypeName getType()
    {
        return type;
    }

    public String getName()
    {
        if ( path.hasName() )
        {
            return path.getName();
        }
        else
        {
            return null;
        }
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public UserKey getOwner()
    {
        return owner;
    }

    public RootDataSet getRootDataSet()
    {
        return rootDataSet;
    }

    public ContentId getId()
    {
        return id;
    }

    public ContentVersionId getVersionId()
    {
        return versionId;
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "id", id );
        s.add( "path", path );
        s.add( "version", versionId );
        s.add( "displayName", displayName );
        s.add( "contentType", type );
        s.add( "created", createdTime );
        s.add( "modified", modifiedTime );
        s.add( "owner", owner );
        s.add( "modifier", modifier );
        return s.toString();
    }

    @Override
    public void checkIllegalEdit( final Content to )
        throws IllegalEditException
    {
        IllegalEdit.check( "id", this.getId(), to.getId(), Content.class );
        IllegalEdit.check( "versionId", this.getVersionId(), to.getVersionId(), Content.class );
        IllegalEdit.check( "path", this.getPath(), to.getPath(), Content.class );
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), Content.class );
        IllegalEdit.check( "owner", this.getOwner(), to.getOwner(), Content.class );
        IllegalEdit.check( "modifiedTime", this.getModifiedTime(), to.getModifiedTime(), Content.class );
        IllegalEdit.check( "modifier", this.getModifier(), to.getModifier(), Content.class );
    }

    public static Builder newContent()
    {
        return new Builder();
    }

    public static Builder newContent( final Content content )
    {
        return new Builder( content );
    }

    public static class Builder
    {
        private ContentPath path;

        private ContentId contentId;

        private QualifiedContentTypeName type;

        private RootDataSet rootDataSet;

        private String displayName;

        private UserKey owner;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private UserKey modifier;

        private ContentVersionId versionId;

        public Builder()
        {
            this.contentId = null;
            this.path = ContentPath.ROOT;
            this.type = null;
            this.rootDataSet = DataSet.newRootDataSet();
            this.displayName = null;
            this.owner = null;
            this.createdTime = null;
            this.modifiedTime = null;
            this.modifier = null;
            this.versionId = null;
        }

        public Builder( final Content content )
        {
            this.contentId = content.id;
            this.path = content.path;
            this.type = content.type;
            this.rootDataSet = content.rootDataSet; // TODO make DataSet immutable, or make copy
            this.displayName = content.displayName;
            this.owner = content.owner;
            this.createdTime = content.createdTime;
            this.modifiedTime = content.modifiedTime;
            this.modifier = content.modifier;
            this.versionId = content.versionId;
        }

        public Builder path( final ContentPath path )
        {
            this.path = path;
            return this;
        }

        public Builder name( final String name )
        {
            if ( this.path == null )
            {
                path = ContentPath.ROOT;
            }
            this.path = this.path.withName( name );
            return this;
        }

        public Builder type( final QualifiedContentTypeName type )
        {
            this.type = type;
            return this;
        }

        public Builder rootDataSet( final RootDataSet dataSet )
        {
            this.rootDataSet = dataSet;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder owner( final UserKey owner )
        {
            this.owner = owner;
            return this;
        }

        public Builder modifier( final UserKey modifier )
        {
            this.modifier = modifier;
            return this;
        }

        public Builder createdTime( final DateTime createdTime )
        {
            this.createdTime = createdTime;
            return this;
        }

        public Builder modifiedTime( final DateTime modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Builder id( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder version( final ContentVersionId versionId )
        {
            this.versionId = versionId;
            return this;
        }

        public Content build()
        {
            Preconditions.checkNotNull( path, "path is mandatory for a content" );

            if ( type == null )
            {
                type = QualifiedContentTypeName.unstructured();
            }
            if ( versionId == null )
            {
                versionId = ContentVersionId.initial();
            }
            return new Content( this );
        }
    }
}
