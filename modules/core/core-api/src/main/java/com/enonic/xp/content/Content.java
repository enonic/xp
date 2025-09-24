package com.enonic.xp.content;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;

import static java.util.Objects.requireNonNullElse;

@PublicApi
public class Content
{
    private final boolean valid;

    private final ValidationErrors validationErrors;

    private final String displayName;

    private final ContentTypeName type;

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

    private final ContentId variantOf;

    private final PrincipalKey modifier;

    private final Page page;

    private final boolean hasChildren;

    private final ChildOrder childOrder;

    private final AccessControlList permissions;

    private final Set<ContentInheritType> inherit;

    private final ProjectName originProject;

    private final Locale language;

    private final ContentIds processedReferences;

    private final WorkflowInfo workflowInfo;

    private final Long manualOrderValue;

    private final ContentPath originalParentPath;

    private final ContentName originalName;

    private final Instant archivedTime;

    private final PrincipalKey archivedBy;

    protected Content( final Builder<? extends Builder> builder )
    {
        this.valid = builder.valid;
        this.validationErrors = builder.validationErrors;
        this.displayName = builder.displayName;
        this.type = builder.type;
        this.path = builder.root ? ContentPath.ROOT : ContentPath.from( builder.parentPath, builder.name );
        this.id = builder.id;
        this.data = builder.data;
        this.attachments = requireNonNullElse( builder.attachments, Attachments.empty() );
        this.extraDatas = Objects.requireNonNull( builder.extraDatas );
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.publishInfo = builder.publishInfo;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.owner = builder.owner;
        this.page = builder.page;
        this.hasChildren = builder.hasChildren;
        this.inherit = Sets.immutableEnumSet( builder.inherit );
        this.originProject = builder.originProject;
        this.childOrder = builder.childOrder;
        this.permissions = requireNonNullElse( builder.permissions, AccessControlList.empty() );
        this.language = builder.language;
        this.processedReferences = builder.processedReferences.build();
        this.workflowInfo = requireNonNullElse(builder.workflowInfo, WorkflowInfo.ready() );
        this.manualOrderValue = builder.manualOrderValue;
        this.originalName = builder.originalName;
        this.originalParentPath = builder.originalParentPath;
        this.archivedTime = builder.archivedTime;
        this.archivedBy = builder.archivedBy;
        this.variantOf = builder.variantOf;
    }

    public static Builder create( final ContentTypeName type )
    {
        if ( type.isPageTemplate() )
        {
            return PageTemplate.newPageTemplate();
        }
        else if ( type.isSite() )
        {
            return Site.create();
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
        return path.getParentPath();
    }

    public ContentPath getPath()
    {
        return path;
    }

    public ContentTypeName getType()
    {
        return type;
    }

    public ContentName getName()
    {
        return this.path.getName();
    }

    public boolean isValid()
    {
        return valid;
    }

    public ValidationErrors getValidationErrors()
    {
        return validationErrors;
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

    public Set<ContentInheritType> getInherit()
    {
        return inherit;
    }

    public ProjectName getOriginProject()
    {
        return originProject;
    }

    public boolean isSite()
    {
        return this instanceof Site;
    }

    public boolean isPageTemplate()
    {
        return this instanceof PageTemplate;
    }

    public Page getPage()
    {
        return page;
    }

    public ChildOrder getChildOrder()
        {
            return childOrder;
        }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public Locale getLanguage()
    {
        return language;
    }

    public ContentIds getProcessedReferences()
    {
        return processedReferences;
    }

    public WorkflowInfo getWorkflowInfo()
    {
        return workflowInfo;
    }

    public Long getManualOrderValue()
    {
        return manualOrderValue;
    }

    public ContentPath getOriginalParentPath()
    {
        return originalParentPath;
    }

    public ContentName getOriginalName()
    {
        return originalName;
    }

    public Instant getArchivedTime()
    {
        return archivedTime;
    }

    public PrincipalKey getArchivedBy()
    {
        return archivedBy;
    }

    public ContentId getVariantOf()
    {
        return variantOf;
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

        return Objects.equals( id, other.id ) && Objects.equals( path, other.path ) &&
            Objects.equals( displayName, other.displayName ) && Objects.equals( type, other.type ) && valid == other.valid &&
            Objects.equals( modifier, other.modifier ) && Objects.equals( validationErrors, other.validationErrors ) &&
            Objects.equals( creator, other.creator ) && Objects.equals( owner, other.owner ) &&
            Objects.equals( createdTime, other.createdTime ) && Objects.equals( modifiedTime, other.modifiedTime ) &&
            hasChildren == other.hasChildren && Objects.equals( inherit, other.inherit ) &&
            Objects.equals( originProject, other.originProject ) && Objects.equals( childOrder, other.childOrder ) &&
            Objects.equals( permissions, other.permissions ) &&
            Objects.equals( attachments, other.attachments ) && Objects.equals( data, other.data ) &&
            Objects.equals( extraDatas, other.extraDatas ) && Objects.equals( page, other.page ) &&
            Objects.equals( language, other.language ) && Objects.equals( publishInfo, other.publishInfo ) &&
            Objects.equals( processedReferences, other.processedReferences ) && Objects.equals( workflowInfo, other.workflowInfo ) &&
            Objects.equals( manualOrderValue, other.manualOrderValue ) && Objects.equals( originalName, other.originalName ) &&
            Objects.equals( originalParentPath, other.originalParentPath ) && Objects.equals( archivedTime, other.archivedTime ) &&
            Objects.equals( archivedBy, other.archivedBy ) && Objects.equals( variantOf, other.variantOf );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, path, displayName, type, valid, modifier, creator, owner, createdTime, modifiedTime,
                             hasChildren, inherit, originProject, childOrder, permissions, attachments, data,
                             extraDatas, page, language, publishInfo, processedReferences, workflowInfo, manualOrderValue, originalName,
                             originalParentPath, archivedTime, archivedBy, variantOf );
    }

    public static class Builder<BUILDER extends Builder>
    {
        protected ContentId id;

        protected PropertyTree data;

        protected Page page;

        protected boolean valid;

        protected ValidationErrors validationErrors;

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

        protected boolean hasChildren;

        protected EnumSet<ContentInheritType> inherit = EnumSet.noneOf( ContentInheritType.class );

        protected ProjectName originProject;

        protected ChildOrder childOrder;

        protected AccessControlList permissions;

        protected Locale language;

        protected ContentIds.Builder processedReferences;

        protected WorkflowInfo workflowInfo;

        protected Long manualOrderValue;

        protected ContentPath originalParentPath;

        protected ContentName originalName;

        protected Instant archivedTime;

        protected PrincipalKey archivedBy;

        protected boolean root;

        protected ContentId variantOf;

        protected Builder()
        {
            this.type = ContentTypeName.unstructured();
            this.data = new PropertyTree();
            this.extraDatas = ExtraDatas.empty();
            this.processedReferences = ContentIds.create();
        }

        protected Builder( final Content source )
        {
            this.id = source.id;
            this.valid = source.valid;
            this.validationErrors = source.validationErrors;
            this.parentPath = source.path.getParentPath();
            this.name = source.path.getName();
            this.type = source.type;
            this.data = source.data.copy();
            this.attachments = source.attachments;
            this.extraDatas = source.extraDatas.copy();
            this.displayName = source.displayName;
            this.owner = source.owner;
            this.createdTime = source.createdTime;
            this.modifiedTime = source.modifiedTime;
            this.creator = source.creator;
            this.modifier = source.modifier;
            this.hasChildren = source.hasChildren;
            this.inherit.addAll( source.inherit );
            this.originProject = source.originProject;
            this.page = source.page != null ? source.page.copy() : null;
            this.childOrder = source.childOrder;
            this.permissions = source.permissions;
            this.language = source.language;
            this.publishInfo = source.publishInfo;
            this.processedReferences = ContentIds.create().addAll( source.processedReferences );
            this.workflowInfo = source.workflowInfo;
            this.manualOrderValue = source.manualOrderValue;
            this.originalName = source.originalName;
            this.originalParentPath = source.originalParentPath;
            this.archivedTime = source.archivedTime;
            this.root = source.path.getName() == null;
            this.variantOf = source.variantOf;
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
            this.parentPath = path.getParentPath();
            this.name = path.getName();
            return (BUILDER) this;
        }

        public BUILDER root()
        {
            this.root = true;
            return (BUILDER) this;
        }

        public BUILDER valid( final boolean valid )
        {
            this.valid = valid;
            return (BUILDER) this;
        }

        public BUILDER validationErrors( final ValidationErrors validationErrors )
        {
            this.validationErrors = validationErrors;
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
            this.data = Objects.requireNonNull( data );
            return (BUILDER) this;
        }

        public BUILDER attachments( final Attachments attachments )
        {
            this.attachments = attachments;
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

        public BUILDER setInherit( final Set<ContentInheritType> inherit )
        {
            if ( inherit != null )
            {
                this.inherit = inherit.isEmpty() ? EnumSet.noneOf( ContentInheritType.class ) : EnumSet.copyOf( inherit );
            }
            return (BUILDER) this;
        }

        public BUILDER originProject( final ProjectName originProject )
        {
            this.originProject = originProject;
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

        public BUILDER permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return (BUILDER) this;
        }

        public BUILDER language( final Locale language )
        {
            this.language = language;
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

        public BUILDER manualOrderValue( final Long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            return (BUILDER) this;
        }

        public BUILDER originalName( final ContentName name )
        {
            this.originalName = name;
            return (BUILDER) this;
        }

        public BUILDER originalParentPath( final ContentPath path )
        {
            this.originalParentPath = path;
            return (BUILDER) this;
        }

        public BUILDER archivedTime( final Instant archivedTime )
        {
            this.archivedTime = archivedTime;
            return (BUILDER) this;
        }

        public BUILDER archivedBy( final PrincipalKey archivedBy )
        {
            this.archivedBy = archivedBy;
            return (BUILDER) this;
        }

        public BUILDER variantOf( final ContentId variantOf )
        {
            this.variantOf = variantOf;
            return (BUILDER) this;
        }

        private void validate()
        {
            if ( !root )
            {
                Objects.requireNonNull( parentPath, "parentPath is required for a Content" );
                Objects.requireNonNull( name, "name is required for a Content" );
            }

            Objects.requireNonNull( data, "data is required for a Content" );

            if ( page != null )
            {
                Preconditions.checkArgument( !( page.getDescriptor() != null && page.getTemplate() != null ),
                                             "A Page cannot have both have a descriptor and a template set" );
            }
            Objects.requireNonNull( type, "type is required for a Content" );
        }

        public Content build()
        {
            validate();
            return new Content( this );
        }
    }
}
