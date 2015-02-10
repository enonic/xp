package com.enonic.wem.api.content;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentNames;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.thumb.Thumbnail;

@SuppressWarnings("UnusedDeclaration")
public class Content
    implements Renderable
{
    private final boolean valid;

    private final String displayName;

    private final ContentTypeName type;

    private final ContentPath parentPath;

    private final ContentName name;

    private final ContentPath path;

    private final ContentId id;

    private final PropertyTree data;

    private final Attachments attachments;

    private final Metadatas metadata;

    private final Instant createdTime;

    private final Instant modifiedTime;

    private final PrincipalKey creator;

    private final PrincipalKey owner;

    private final PrincipalKey modifier;

    private final Page page;

    private final boolean hasChildren;

    private final Thumbnail thumbnail;

    private final ChildOrder childOrder;

    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private final Locale language;

    private final ContentState contentState;

    protected Content( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name is required for a Content" );
        Preconditions.checkNotNull( builder.parentPath, "parentPath is required for a Content" );
        Preconditions.checkNotNull( builder.data, "data is required for a Content" );

        if ( builder.page != null )
        {
            Preconditions.checkArgument( !( builder.page.getController() != null && builder.page.getTemplate() != null ),
                                         "A Page cannot have both have a controller and a template set" );
        }

        if ( builder.type == null )
        {
            builder.type = ContentTypeName.unstructured();
        }

        this.valid = builder.valid;
        this.displayName = builder.displayName;
        this.type = builder.type;
        this.name = builder.name;
        this.parentPath = builder.parentPath;
        this.path = ContentPath.from( builder.parentPath, builder.name.toString() );
        this.id = builder.id;
        this.data = builder.data;
        this.attachments = builder.attachments;
        this.metadata = builder.metadata;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.owner = builder.owner;
        this.page = builder.page;
        this.thumbnail = builder.thumbnail;
        this.hasChildren = builder.hasChildren;
        this.childOrder = builder.childOrder;
        this.permissions = builder.permissions == null ? AccessControlList.empty() : builder.permissions;
        this.inheritPermissions = builder.inheritPermissions;
        this.language = builder.language;
        this.contentState = builder.contentState == null ? ContentState.DEFAULT : builder.contentState;
    }

    public ContentPath getParentPath()
    {
        return parentPath;
    }

    public ContentPath getPath()
    {
        return path;
    }

    public boolean isRoot()
    {
        return this.path.elementCount() == 1;
    }

    public ContentTypeName getType()
    {
        return type;
    }

    public ContentName getName()
    {
        return this.name;
    }

    public boolean isValid()
    {
        return valid;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public PrincipalKey getOwner()
    {
        return owner;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public Attachments getAttachments()
    {
        return attachments;
    }

    public boolean hasMetadata( final String name )
    {
        return getMetadata( name ) != null;
    }

    public boolean hasMetadata( final MixinName name )
    {
        return getMetadata( name ) != null;
    }

    public PropertyTree getMetadata( final String name )
    {
        return getMetadata( MixinName.from( name ) );
    }

    public PropertyTree getMetadata( final MixinName name )
    {
        for ( Metadata item : this.metadata )
        {
            if ( item.getName().equals( name ) )
            {
                return item.getData();
            }
        }

        return null;
    }

    public boolean hasMetadata()
    {
        return !this.metadata.isEmpty();
    }

    public Metadatas getAllMetadata()
    {
        return this.metadata;
    }

    public ContentId getId()
    {
        return id;
    }

    public boolean hasChildren()
    {
        return this.hasChildren;
    }

    public boolean isSite()
    {
        return this instanceof Site;
    }

    public boolean isPageTemplate()
    {
        return this instanceof PageTemplate;
    }

    public boolean hasPage()
    {
        return page != null;
    }

    public Page getPage()
    {
        return page;
    }

    public boolean hasThumbnail()
    {
        return this.thumbnail != null;
    }

    public Thumbnail getThumbnail()
    {
        return thumbnail;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public boolean inheritsPermissions()
    {
        return inheritPermissions;
    }

    public Locale getLanguage()
    {
        return language;
    }

    public ContentState getContentState()
    {
        return contentState;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Content ) )
        {
            return false;
        }

        final Content other = (Content) o;

        return Objects.equals( id, other.id ) &&
            Objects.equals( name, other.name ) &&
            Objects.equals( parentPath, other.parentPath ) &&
            Objects.equals( displayName, other.displayName ) &&
            Objects.equals( type, other.type ) &&
            Objects.equals( valid, other.valid ) &&
            Objects.equals( modifier, other.modifier ) &&
            Objects.equals( creator, other.creator ) &&
            Objects.equals( owner, other.owner ) &&
            Objects.equals( createdTime, other.createdTime ) &&
            Objects.equals( modifiedTime, other.modifiedTime ) &&
            Objects.equals( hasChildren, other.hasChildren ) &&
            Objects.equals( inheritPermissions, other.inheritPermissions ) &&
            Objects.equals( childOrder, other.childOrder ) &&
            Objects.equals( thumbnail, other.thumbnail ) &&
            Objects.equals( permissions, other.permissions ) &&
            Objects.equals( attachments, other.attachments ) &&
            Objects.equals( data, other.data ) &&
            Objects.equals( metadata, other.metadata ) &&
            Objects.equals( page, other.page ) &&
            Objects.equals( language, other.language ) &&
            Objects.equals( contentState, other.contentState );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, name, parentPath, displayName, type, valid, modifier, creator, owner, createdTime, modifiedTime,
                             hasChildren, inheritPermissions, childOrder, thumbnail, permissions, attachments, data, metadata, page,
                             language, contentState );
    }

    public static Builder newContent( final ContentTypeName type )
    {
        if ( type.isPageTemplate() )
        {
            final PageTemplate.Builder builder = PageTemplate.newPageTemplate();
            builder.type( type );
            return builder;
        }
        else if ( type.isSite() )
        {
            Site.Builder builder = Site.newSite();
            builder.type( type );
            return builder;
        }
        else if ( type.isDescendantOfMedia() )
        {
            Media.Builder builder = Media.create();
            builder.type( type );
            return builder;
        }
        else
        {
            Builder builder = Content.newContent();
            builder.type( type );
            return builder;
        }
    }

    public static Builder newContent()
    {
        return new Builder();
    }

    public static Builder newContent( final Content source )
    {
        if ( source instanceof PageTemplate )
        {
            return new PageTemplate.Builder( (PageTemplate) source );
        }
        else if ( source instanceof Site )
        {
            return new Site.Builder( (Site) source );
        }
        else if ( source instanceof Media )
        {
            return new Media.Builder( (Media) source );
        }
        else
        {
            return new Builder( source );
        }
    }

    public static class Builder<BUILDER extends Builder, C extends Content>
    {
        protected ContentId id;

        protected PropertyTree data;

        protected Page page;

        protected boolean valid;

        protected ContentPath parentPath;

        protected ContentName name;

        protected ContentTypeName type;

        protected Form form;

        protected Attachments attachments;

        protected Metadatas metadata;

        protected String displayName;

        protected PrincipalKey owner;

        protected Instant createdTime;

        protected Instant modifiedTime;

        protected PrincipalKey creator;

        protected PrincipalKey modifier;

        protected Thumbnail thumbnail;

        protected boolean hasChildren;

        protected ChildOrder childOrder;

        protected AccessControlList permissions;

        protected boolean inheritPermissions;

        protected Locale language;

        protected ContentState contentState;

        protected Builder()
        {
            this.data = new PropertyTree();
            this.attachments = Attachments.empty();
            this.metadata = Metadatas.empty();
            this.inheritPermissions = true;
        }

        protected Builder( final Content source )
        {

            this.id = source.id;
            this.valid = source.valid;
            this.parentPath = source.parentPath;
            this.name = source.name;
            this.type = source.type;
            this.data = source.data != null ? source.data.copy() : null;
            this.attachments = source.attachments;
            this.metadata = source.metadata != null ? source.metadata.copy() : null;
            this.displayName = source.displayName;
            this.owner = source.owner;
            this.createdTime = source.createdTime;
            this.modifiedTime = source.modifiedTime;
            this.creator = source.creator;
            this.modifier = source.modifier;
            this.hasChildren = source.hasChildren;
            this.page = source.page != null ? source.page.copy() : null;
            this.thumbnail = source.thumbnail;
            this.childOrder = source.childOrder;
            this.permissions = source.permissions;
            this.inheritPermissions = source.inheritPermissions;
            this.language = source.language;
            this.contentState = source.contentState;
        }

        public Builder<BUILDER, C> parentPath( final ContentPath path )
        {
            this.parentPath = path;
            return this;
        }

        public Builder<BUILDER, C> name( final String name )
        {
            this.name = ContentName.from( name );
            return this;
        }

        public Builder<BUILDER, C> name( final ContentName name )
        {
            this.name = name;
            return this;
        }

        public Builder<BUILDER, C> path( final String path )
        {
            return path( ContentPath.from( path ) );
        }

        public Builder<BUILDER, C> path( final ContentPath path )
        {
            this.parentPath = path.getParentPath() != null ? path.getParentPath().asAbsolute() : null;
            Preconditions.checkArgument( path.elementCount() > 0, "No content can be \"root content\": " + path.toString() );
            this.name = ContentName.from( path.getElement( path.elementCount() - 1 ) );
            return this;
        }

        public Builder<BUILDER, C> valid( final boolean validated )
        {
            this.valid = validated;
            return this;
        }

        public Builder<BUILDER, C> type( final ContentTypeName type )
        {
            if ( type.isDescendantOfMedia() && !( this instanceof Media.Builder ) )
            {
                throw new IllegalArgumentException( "Please create Builder via Media when creating a Media" );
            }
            this.type = type;
            return this;
        }

        public Builder<BUILDER, C> data( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public Builder<BUILDER, C> attachments( final Attachments attachments )
        {
            this.attachments = attachments;

            final Attachment thumbnailAttachment = attachments.byName( AttachmentNames.THUMBNAIL );
            if ( thumbnailAttachment != null )
            {
                thumbnail( Thumbnail.from( thumbnailAttachment.getBinaryReference(), thumbnailAttachment.getMimeType(),
                                           thumbnailAttachment.getSize() ) );
            }
            return this;
        }

        public Builder<BUILDER, C> addMetadata( final Metadata metadata )
        {
            if ( this.metadata == null )
            {
                this.metadata = Metadatas.empty();
            }
            this.metadata = Metadatas.from( this.metadata, metadata );
            return this;
        }

        public Builder<BUILDER, C> metadata( final Metadatas metadata )
        {
            this.metadata = metadata;
            return this;
        }

        public Builder<BUILDER, C> displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder<BUILDER, C> owner( final PrincipalKey owner )
        {
            this.owner = owner;
            return this;
        }

        public Builder<BUILDER, C> creator( final PrincipalKey modifier )
        {
            this.creator = modifier;
            return this;
        }

        public Builder<BUILDER, C> modifier( final PrincipalKey modifier )
        {
            this.modifier = modifier;
            return this;
        }

        public Builder<BUILDER, C> createdTime( final Instant createdTime )
        {
            this.createdTime = createdTime;
            return this;
        }

        public Builder<BUILDER, C> modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Builder<BUILDER, C> id( final ContentId contentId )
        {
            this.id = contentId;
            return this;
        }

        public Builder<BUILDER, C> hasChildren( final boolean hasChildren )
        {
            this.hasChildren = hasChildren;
            return this;
        }

        public Builder<BUILDER, C> childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder<BUILDER, C> page( final Page page )
        {
            this.page = page;
            return this;
        }

        public Builder<BUILDER, C> thumbnail( final Thumbnail thumbnail )
        {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder<BUILDER, C> permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder<BUILDER, C> inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        public Builder<BUILDER, C> language( final Locale language )
        {
            this.language = language;
            return this;
        }

        public Builder<BUILDER, C> contentState( final ContentState contentState )
        {
            this.contentState = contentState;
            return this;
        }

        @SuppressWarnings("unchecked")
        public C build()
        {
            return (C) new Content( this );
        }
    }
}
