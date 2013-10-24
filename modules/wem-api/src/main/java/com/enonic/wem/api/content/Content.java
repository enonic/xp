package com.enonic.wem.api.content;

import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeTranslatable;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.support.ChangeTraceable;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

public final class Content
    implements IllegalEditAware<Content>, NodeTranslatable, ChangeTraceable
{
    private final String displayName;

    private final QualifiedContentTypeName type;

    private final ContentPath path;

    private final ContentId id;

    private final Form form;

    private final ContentData contentData;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final UserKey creator;

    private final UserKey owner;

    private final UserKey modifier;

    private final ContentVersionId versionId;

    private final ImmutableList<ContentId> childrenIds;

    private final Page page;

    private Content( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.type = builder.type;
        this.path = builder.path;
        this.id = builder.contentId;
        this.form = builder.form;
        this.contentData = builder.contentData;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.owner = builder.owner;
        this.versionId = builder.versionId;
        this.childrenIds = builder.childrenIdsBuilder.build();
        this.page = builder.page;
    }

    public boolean isTemporary()
    {
        return getPath().getSpace().isTemporary();
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

    public UserKey getCreator()
    {
        return modifier;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public UserKey getOwner()
    {
        return owner;
    }

    public Form getForm()
    {
        return form;
    }

    public ContentData getContentData()
    {
        return contentData;
    }

    public ContentId getId()
    {
        return id;
    }

    public ContentVersionId getVersionId()
    {
        return versionId;
    }

    public boolean hasChildren()
    {
        return !childrenIds.isEmpty();
    }

    public Node toNode( final NodePath parent )
    {
        final Node.Builder builder = Node.newNode( new EntityId( this.id.toString() ), this.getName() );
        builder.parent( parent );
        builder.createdTime( this.createdTime );
        builder.modifiedTime( this.modifiedTime );
        builder.creator( this.creator );
        builder.modifier( this.modifier );
        builder.property( "displayName", this.displayName );
        builder.property( "owner", this.owner != null ? this.owner.toString() : null );
        builder.property( "type", this.type != null ? this.type.toString() : null );
        builder.addDataSet( this.contentData != null ? this.contentData.toDataSet( "data" ) : null );

        return builder.build();
    }

    @Override
    public void checkIllegalEdit( final Content to )
        throws IllegalEditException
    {
        IllegalEdit.check( "id", this.getId(), to.getId(), Content.class );
        IllegalEdit.check( "versionId", this.getVersionId(), to.getVersionId(), Content.class );
        IllegalEdit.check( "path", this.getPath(), to.getPath(), Content.class );
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), Content.class );
        IllegalEdit.check( "creator", this.getCreator(), to.getCreator(), Content.class );
        IllegalEdit.check( "modifiedTime", this.getModifiedTime(), to.getModifiedTime(), Content.class );
        IllegalEdit.check( "modifier", this.getModifier(), to.getModifier(), Content.class );
        IllegalEdit.check( "owner", this.getOwner(), to.getOwner(), Content.class );
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
        s.add( "createdTime", createdTime );
        s.add( "modifiedTime", modifiedTime );
        s.add( "creator", creator );
        s.add( "modifier", modifier );
        s.add( "owner", owner );
        return s.toString();
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

        private Form form;

        private ContentData contentData;

        private String displayName;

        private UserKey owner;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private UserKey creator;

        private UserKey modifier;

        private ContentVersionId versionId;

        private ImmutableList.Builder<ContentId> childrenIdsBuilder;

        private Page page;

        public Builder()
        {
            this.path = ContentPath.ROOT;
            this.contentData = new ContentData();
            this.childrenIdsBuilder = ImmutableList.builder();
        }

        public Builder( final Content content )
        {
            this.contentId = content.id;
            this.path = content.path;
            this.type = content.type;
            this.form = content.form; // TODO make DataSet immutable, or make copy
            this.contentData = content.contentData; // TODO make DataSet immutable, or make copy
            this.displayName = content.displayName;
            this.owner = content.owner;
            this.createdTime = content.createdTime;
            this.modifiedTime = content.modifiedTime;
            this.creator = content.creator;
            this.modifier = content.modifier;
            this.versionId = content.versionId;
            this.childrenIdsBuilder = ImmutableList.builder();
            this.childrenIdsBuilder.addAll( content.childrenIds );
            this.page = content.page;
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

        public Builder form( final Form form )
        {
            this.contentData = contentData;
            return this;
        }

        public Builder contentData( final ContentData contentData )
        {
            this.contentData = contentData;
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

        public Builder creator( final UserKey modifier )
        {
            this.creator = modifier;
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

        public Builder addChildId( final ContentId childId )
        {
            this.childrenIdsBuilder.add( childId );
            return this;
        }

        public Builder page( final Page page )
        {
            this.page = page;
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
