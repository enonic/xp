package com.enonic.wem.api.content;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.support.ChangeTraceable;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

@SuppressWarnings("UnusedDeclaration")
public class Content
    implements IllegalEditAware<Content>, ChangeTraceable, Renderable
{
    private final boolean draft;

    private final String displayName;

    private final ContentTypeName type;

    private final ContentPath parentPath;

    private final ContentName name;

    private final ContentPath path;

    private final ContentId id;

    private final Form form;

    private final PropertyTree data;

    private final Attachments attachments;

    private final ImmutableList<Metadata> metadata;

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

    protected Content( final BaseBuilder builder )
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

        this.draft = builder.draft;
        this.displayName = builder.displayName;
        this.type = builder.type;
        this.name = builder.name;
        this.parentPath = builder.parentPath;
        this.path = ContentPath.from( builder.parentPath, builder.name.toString() );
        this.id = builder.contentId;
        this.form = builder.form;
        this.data = builder.data;
        this.attachments = builder.attachments;
        this.metadata = ImmutableList.copyOf( builder.metadata );
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
    }

    public static Builder newContent()
    {
        return new Builder();
    }

    public static Builder newContent( final Content content )
    {
        return new Builder( content );
    }

    public static EditBuilder editContent( final Content content )
    {
        return new EditBuilder( content );
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

    public boolean isDraft()
    {
        return draft;
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
        return modifier;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public PrincipalKey getOwner()
    {
        return owner;
    }

    public Form getForm()
    {
        return form;
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

    public boolean hasMetadata( final MetadataSchemaName name )
    {
        return getMetadata( name ) != null;
    }

    public PropertyTree getMetadata( final String name )
    {
        return getMetadata( MetadataSchemaName.from( name ) );
    }

    public PropertyTree getMetadata( final MetadataSchemaName name )
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

    public ImmutableList<Metadata> getAllMetadata()
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

    @Override
    public void checkIllegalEdit( final Content to )
        throws IllegalEditException
    {
        IllegalEdit.check( "id", this.getId(), to.getId(), Content.class );
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
        s.add( "draft", draft );
        s.add( "path", path );
        s.add( "displayName", displayName );
        s.add( "contentType", type );
        s.add( "createdTime", createdTime );
        s.add( "modifiedTime", modifiedTime );
        s.add( "creator", creator );
        s.add( "modifier", modifier );
        s.add( "owner", owner );
        s.add( "permissions", permissions );
        s.add( "inheritPermissions", inheritPermissions );
        return s.toString();
    }

    static abstract class BaseBuilder
    {
        protected ContentId contentId;

        protected PropertyTree data;

        protected Page page;

        boolean draft;

        ContentPath parentPath;

        ContentName name;

        ContentTypeName type;

        Form form;

        Attachments attachments;

        List<Metadata> metadata;

        String displayName;

        PrincipalKey owner;

        Instant createdTime;

        Instant modifiedTime;

        PrincipalKey creator;

        PrincipalKey modifier;

        Thumbnail thumbnail;

        boolean hasChildren;

        ChildOrder childOrder;

        AccessControlList permissions;

        boolean inheritPermissions;

        BaseBuilder()
        {
            this.data = new PropertyTree();
            this.attachments = Attachments.empty();
            this.metadata = new ArrayList<>();
            this.inheritPermissions = true;
        }

        BaseBuilder( final Content content )
        {
            this.contentId = content.id;
            this.draft = content.draft;
            this.parentPath = content.parentPath;
            this.name = content.name;
            this.type = content.type;
            this.form = content.form; // TODO make DataSet immutable, or make copy
            this.data = content.data; // TODO make DataSet immutable, or make copy
            this.attachments = content.attachments; // TODO make DataSet immutable, or make copy
            this.metadata = content.metadata;
            this.displayName = content.displayName;
            this.owner = content.owner;
            this.createdTime = content.createdTime;
            this.modifiedTime = content.modifiedTime;
            this.creator = content.creator;
            this.modifier = content.modifier;
            this.hasChildren = content.hasChildren;
            this.page = content.page;
            this.thumbnail = content.thumbnail;
            this.childOrder = content.childOrder;
            this.permissions = content.permissions;
            this.inheritPermissions = content.inheritPermissions;
        }
    }

    public static class EditBuilder
        extends BaseBuilder
    {
        private final Content original;

        private final Changes.Builder changes = new Changes.Builder();

        public EditBuilder( final Content original )
        {
            super( original );
            this.original = original;
        }

        public EditBuilder type( final ContentTypeName type )
        {
            changes.recordChange( newPossibleChange( "type" ).from( this.original.getType() ).to( type ).build() );
            this.type = type;
            return this;
        }

        public EditBuilder draft( boolean draft )
        {
            this.draft = draft;
            return this;
        }

        public EditBuilder form( final Form form )
        {
            changes.recordChange( newPossibleChange( "form" ).from( this.original.getForm() ).to( form ).build() );
            this.form = form;
            return this;
        }

        public EditBuilder data( final PropertyTree contentData )
        {
            changes.recordChange( newPossibleChange( "data" ).from( this.original.getData() ).to( contentData ).build() );
            this.data = contentData;
            return this;
        }

        public EditBuilder metadata( final List<Metadata> metadata )
        {
            changes.recordChange( newPossibleChange( "metadata" ).from( this.original.metadata ).to( metadata ).build() );
            this.metadata = metadata;
            return this;
        }

        public EditBuilder displayName( final String displayName )
        {
            changes.recordChange( newPossibleChange( "displayName" ).from( this.original.getDisplayName() ).to( displayName ).build() );
            this.displayName = displayName;
            return this;
        }

        public EditBuilder page( final Page page )
        {
            changes.recordChange( newPossibleChange( "page" ).from( this.original.getPage() ).to( page ).build() );
            this.page = page;
            return this;
        }

        public EditBuilder thumbnail( final Thumbnail thumbnail )
        {
            changes.recordChange( newPossibleChange( "thumbnail" ).from( this.original.getThumbnail() ).to( thumbnail ).build() );
            this.thumbnail = thumbnail;
            return this;
        }

        public EditBuilder permissions( final AccessControlList permissions )
        {
            changes.recordChange( newPossibleChange( "permissions" ).from( this.original.permissions ).to( this.permissions ).build() );
            this.permissions = permissions;
            return this;
        }

        public EditBuilder inheritPermissions( final boolean inheritPermissions )
        {
            changes.recordChange(
                newPossibleChange( "inheritPermissions" ).from( this.original.inheritPermissions ).to( this.inheritPermissions ).build() );
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        public boolean isChanges()
        {
            return this.changes.isChanges();
        }

        public EditBuilder childOrder( final ChildOrder childOrder )
        {
            changes.recordChange( newPossibleChange( "childOrder" ).from( this.childOrder ).to( childOrder ).build() );
            this.childOrder = childOrder;
            return this;
        }

        public Content build()
        {
            return new Content( this );
        }
    }

    public static class Builder<BUILDER extends Builder, C extends Content>
        extends BaseBuilder
    {
        public Builder()
        {
            super();
        }

        public Builder( final Content content )
        {
            super( content );
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
            this.parentPath = path.getParentPath();
            Preconditions.checkArgument( path.elementCount() > 0, "No content can be \"root content\": " + path.toString() );
            this.name = ContentName.from( path.getElement( path.elementCount() - 1 ) );
            return this;
        }

        public Builder<BUILDER, C> draft( final boolean draft )
        {
            this.draft = draft;
            return this;
        }

        public Builder<BUILDER, C> type( final ContentTypeName type )
        {
            this.type = type;
            return this;
        }

        public Builder<BUILDER, C> form( final Form form )
        {
            this.form = form;
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
            return this;
        }

        public Builder<BUILDER, C> addMetadata( final Metadata metadata )
        {
            if ( this.metadata == null )
            {
                this.metadata = new ArrayList<>();
            }
            this.metadata.add( metadata );
            return this;
        }

        public Builder<BUILDER, C> metadata( final List<Metadata> metadata )
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
            this.contentId = contentId;
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

        public C build()
        {
            return (C) new Content( this );
        }
    }
}
