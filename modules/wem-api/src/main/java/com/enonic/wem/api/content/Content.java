package com.enonic.wem.api.content;

import java.util.List;

import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemTranslatable;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

public final class Content
    implements IllegalEditAware<Content>, ItemTranslatable<Content>
{
    private final String displayName;

    private final QualifiedContentTypeName type;

    private final ContentPath path;

    private final ContentId id;

    private final ContentData contentData;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final UserKey owner;

    private final UserKey modifier;

    private final ContentVersionId versionId;

    private final ImmutableList<ContentId> childrenIds;

    private Content( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.type = builder.type;
        this.path = builder.path;
        this.id = builder.contentId;
        this.contentData = builder.contentData;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.owner = builder.owner;
        this.modifier = builder.modifier;
        this.versionId = builder.versionId;
        this.childrenIds = builder.childrenIdsBuilder.build();
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

    public UserKey getModifier()
    {
        return modifier;
    }

    public UserKey getOwner()
    {
        return owner;
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

    public boolean hasChildren() {
        return !childrenIds.isEmpty();
    }

    public Item toItem()
    {
        final Item item = new Item();
        if ( this.id != null )
        {
            item.setProperty( "id", new Value.ContentId( this.id ) );
        }
        if ( this.getName() != null )
        {
            item.setProperty( "name", new Value.Text( this.getName() ) );
        }
        if ( this.path != null )
        {
            item.setProperty( "path", new Value.Text( this.path.toString() ) );
        }
        if ( this.displayName != null )
        {
            item.setProperty( "displayName", new Value.Text( this.displayName ) );
        }
        if ( this.createdTime != null )
        {
            item.setProperty( "createdTime", new Value.DateTime( this.createdTime ) );
        }
        if ( this.modifiedTime != null )
        {
            item.setProperty( "modifiedTime", new Value.DateTime( this.modifiedTime ) );
        }
        if ( this.owner != null )
        {
            item.setProperty( "owner", new Value.Text( this.owner.toString() ) );
        }
        if ( this.modifier != null )
        {
            item.setProperty( "modifier", new Value.Text( this.modifier.toString() ) );
        }
        if ( this.type != null )
        {
            item.setProperty( "type", new Value.Text( this.type.toString() ) );
        }
        if ( this.contentData != null )
        {
            item.add( this.contentData.toDataSet( "data" ) );
        }
        return item;
    }

    @Override
    public Content toObject( final Item item )
    {
        return newContent().
            id( item.getProperty( "id" ).getContentId() ).
            name( item.getProperty( "name" ).getString() ).
            displayName( item.getProperty( "displayName" ).getString() ).
            createdTime( item.getProperty( "createdTime" ).getDateTime() ).
            modifiedTime( item.getProperty( "modifiedTime" ).getDateTime() ).
            owner( AccountKey.from( item.getProperty( "owner" ).getString() ).asUser() ).
            modifier( AccountKey.from( item.getProperty( "modifier" ).getString() ).asUser() ).
            type( QualifiedContentTypeName.from( item.getProperty( "type" ).getString() ) ).
            contentData( new ContentData( item.getDataSet( "data" ).toRootDataSet() ) ).
            build();
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

        private ContentData contentData;

        private String displayName;

        private UserKey owner;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private UserKey modifier;

        private ContentVersionId versionId;

        private ImmutableList.Builder<ContentId> childrenIdsBuilder;

        public Builder()
        {
            this.contentId = null;
            this.path = ContentPath.ROOT;
            this.type = null;
            this.contentData = new ContentData();
            this.displayName = null;
            this.owner = null;
            this.createdTime = null;
            this.modifiedTime = null;
            this.modifier = null;
            this.versionId = null;
            this.childrenIdsBuilder = ImmutableList.builder();
        }

        public Builder( final Content content )
        {
            this.contentId = content.id;
            this.path = content.path;
            this.type = content.type;
            this.contentData = content.contentData; // TODO make DataSet immutable, or make copy
            this.displayName = content.displayName;
            this.owner = content.owner;
            this.createdTime = content.createdTime;
            this.modifiedTime = content.modifiedTime;
            this.modifier = content.modifier;
            this.versionId = content.versionId;
            this.childrenIdsBuilder = ImmutableList.builder();
            this.childrenIdsBuilder.addAll( content.childrenIds );
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

        public Builder addChildId( final ContentId childId ) {
            this.childrenIdsBuilder.add( childId );
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
