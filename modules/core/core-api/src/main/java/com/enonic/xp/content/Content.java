package com.enonic.xp.content;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;

@PublicApi
public class Content
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

    private final ExtraDatas extraDatas;

    private final Instant createdTime;

    private final Instant modifiedTime;

    private final ContentPublishInfo publishInfo;

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

    private final ContentIds processedReferences;

    private final WorkflowInfo workflowInfo;

    protected Content( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name is required for a Content" );
        Preconditions.checkNotNull( builder.parentPath, "parentPath is required for a Content" );
        Preconditions.checkNotNull( builder.data, "data is required for a Content" );

        if ( builder.page != null )
        {
            Preconditions.checkArgument( !( builder.page.getDescriptor() != null && builder.page.getTemplate() != null ),
                                         "A Page cannot have both have a descriptor and a template set" );
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
        this.extraDatas = builder.extraDatas;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.publishInfo = builder.publishInfo;
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
        this.processedReferences = builder.processedReferences.build();
        this.workflowInfo = builder.workflowInfo == null ? WorkflowInfo.ready() : builder.workflowInfo;
    }

    public static Builder create( final ContentTypeName type )
    {
        if ( type.isPageTemplate() )
        {
            final PageTemplate.Builder builder = PageTemplate.newPageTemplate();
            builder.type( type );
            return builder;
        }
        else if ( type.isSite() )
        {
            Site.Builder builder = Site.create();
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
            Builder builder = Content.create();
            builder.type( type );
            return builder;
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Content source )
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

    public ContentPublishInfo getPublishInfo()
    {
        return publishInfo;
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

    public boolean hasExtraData()
    {
        return !this.extraDatas.isEmpty();
    }

    public ExtraDatas getAllExtraData()
    {
        return this.extraDatas;
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

    public ContentIds getProcessedReferences()
    {
        return processedReferences;
    }

    public WorkflowInfo getWorkflowInfo()
    {
        return workflowInfo;
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
            Objects.equals( extraDatas, other.extraDatas ) &&
            Objects.equals( page, other.page ) &&
            Objects.equals( language, other.language ) &&
            Objects.equals( contentState, other.contentState ) && Objects.equals( publishInfo, other.publishInfo ) &&
            Objects.equals( processedReferences, other.processedReferences ) &&
            Objects.equals( workflowInfo, other.workflowInfo );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, name, parentPath, displayName, type, valid, modifier, creator, owner, createdTime, modifiedTime,
                             hasChildren, inheritPermissions, childOrder, thumbnail, permissions, attachments, data, extraDatas, page,
                             language, contentState, publishInfo, processedReferences, workflowInfo );
    }

    public static class Builder<BUILDER extends Builder>
    {
        protected ContentId id;

        protected PropertyTree data;

        protected Page page;

        protected boolean valid;

        protected ContentPath parentPath;

        protected ContentName name;

        protected ContentTypeName type;

        protected Attachments attachments;

        protected ExtraDatas extraDatas;

        protected String displayName;

        protected PrincipalKey owner;

        protected Instant createdTime;

        protected Instant modifiedTime;

        protected ContentPublishInfo publishInfo;

        protected PrincipalKey creator;

        protected PrincipalKey modifier;

        protected Thumbnail thumbnail;

        protected boolean hasChildren;

        protected ChildOrder childOrder;

        protected AccessControlList permissions;

        protected boolean inheritPermissions;

        protected Locale language;

        protected ContentState contentState;

        protected ContentIds.Builder processedReferences;

        protected WorkflowInfo workflowInfo;

        protected Builder()
        {
            this.data = new PropertyTree();
            this.attachments = Attachments.empty();
            this.extraDatas = ExtraDatas.empty();
            this.inheritPermissions = true;
            this.processedReferences = ContentIds.create();
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
            this.extraDatas = source.extraDatas != null ? source.extraDatas.copy() : null;
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
            this.publishInfo = source.publishInfo;
            this.processedReferences = ContentIds.create().addAll( source.processedReferences );
            this.workflowInfo = source.workflowInfo;
        }

        public BUILDER parentPath( final ContentPath path )
        {
            this.parentPath = path;
            return (BUILDER) this;
        }

        public BUILDER name( final String name )
        {
            this.name = ContentName.from( name );
            return (BUILDER) this;
        }

        public BUILDER name( final ContentName name )
        {
            this.name = name;
            return (BUILDER) this;
        }

        public BUILDER path( final String path )
        {
            return path( ContentPath.from( path ) );
        }

        public BUILDER path( final ContentPath path )
        {
            this.parentPath = path.getParentPath() != null ? path.getParentPath().asAbsolute() : null;
            Preconditions.checkArgument( path.elementCount() > 0, "No content can be \"root content\": " + path.toString() );
            this.name = ContentName.from( path.getElement( path.elementCount() - 1 ) );
            return (BUILDER) this;
        }

        public BUILDER valid( final boolean valid )
        {
            this.valid = valid;
            return (BUILDER) this;
        }

        public BUILDER type( final ContentTypeName type )
        {
            if ( type.isDescendantOfMedia() && !( this instanceof Media.Builder ) )
            {
                throw new IllegalArgumentException( "Please create Builder via Media when creating a Media" );
            }
            this.type = type;
            return (BUILDER) this;
        }

        public BUILDER data( final PropertyTree data )
        {
            this.data = data;
            return (BUILDER) this;
        }

        public BUILDER attachments( final Attachments attachments )
        {
            this.attachments = attachments;

            final Attachment thumbnailAttachment = attachments.byName( AttachmentNames.THUMBNAIL );
            if ( thumbnailAttachment != null )
            {
                thumbnail( Thumbnail.from( thumbnailAttachment.getBinaryReference(), thumbnailAttachment.getMimeType(),
                                           thumbnailAttachment.getSize() ) );
            }
            return (BUILDER) this;
        }

        public BUILDER addExtraData( final ExtraData extraData )
        {
            if ( this.extraDatas == null )
            {
                this.extraDatas = ExtraDatas.empty();
            }
            this.extraDatas = ExtraDatas.from( this.extraDatas, extraData );
            return (BUILDER) this;
        }

        public BUILDER extraDatas( final ExtraDatas extraDatas )
        {
            this.extraDatas = extraDatas;
            return (BUILDER) this;
        }

        public BUILDER displayName( final String displayName )
        {
            this.displayName = displayName;
            return (BUILDER) this;
        }

        public BUILDER owner( final PrincipalKey owner )
        {
            this.owner = owner;
            return (BUILDER) this;
        }

        public BUILDER creator( final PrincipalKey modifier )
        {
            this.creator = modifier;
            return (BUILDER) this;
        }

        public BUILDER modifier( final PrincipalKey modifier )
        {
            this.modifier = modifier;
            return (BUILDER) this;
        }

        public BUILDER createdTime( final Instant createdTime )
        {
            this.createdTime = createdTime;
            return (BUILDER) this;
        }

        public BUILDER modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return (BUILDER) this;
        }


        public BUILDER publishInfo( final ContentPublishInfo publishInfo )
        {
            this.publishInfo = publishInfo;
            return (BUILDER) this;
        }

        public BUILDER id( final ContentId contentId )
        {
            this.id = contentId;
            return (BUILDER) this;
        }

        public BUILDER hasChildren( final boolean hasChildren )
        {
            this.hasChildren = hasChildren;
            return (BUILDER) this;
        }

        public BUILDER childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return (BUILDER) this;
        }

        public BUILDER page( final Page page )
        {
            this.page = page;
            return (BUILDER) this;
        }

        public BUILDER thumbnail( final Thumbnail thumbnail )
        {
            this.thumbnail = thumbnail;
            return (BUILDER) this;
        }

        public BUILDER permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return (BUILDER) this;
        }

        public BUILDER inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            return (BUILDER) this;
        }

        public BUILDER language( final Locale language )
        {
            this.language = language;
            return (BUILDER) this;
        }

        public BUILDER contentState( final ContentState contentState )
        {
            this.contentState = contentState;
            return (BUILDER) this;
        }

        public BUILDER processedReferences( final ContentIds references )
        {
            this.processedReferences = ContentIds.create().addAll( references );
            return (BUILDER) this;
        }

        public BUILDER addProcessedReference( final ContentId reference )
        {
            this.processedReferences.add( reference );
            return (BUILDER) this;
        }

        public BUILDER workflowInfo( final WorkflowInfo workflowInfo )
        {
            this.workflowInfo = workflowInfo;
            return (BUILDER) this;
        }

        @SuppressWarnings("unchecked")
        public Content build()
        {
            return new Content( this );
        }
    }
}
