package com.enonic.xp.content;

import java.time.Instant;
import java.util.Locale;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public class CreateContentTranslatorParams
{
    private final PropertyTree data;

    private final ExtraDatas extraDatas;

    private final ContentTypeName type;

    private final PrincipalKey owner;

    private final PrincipalKey creator;

    private final PrincipalKey modifier;

    private final Instant createdTime;

    private final Instant modifiedTime;

    private final String displayName;

    private final ContentName name;

    private final ContentPath parentContentPath;

    private final boolean valid;

    private final CreateAttachments createAttachments;

    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private final ChildOrder childOrder;

    private final ContentPublishInfo contentPublishInfo;

    private final Locale language;

    private final ContentIds processedIds;

    private final WorkflowInfo workflowInfo;

    private CreateContentTranslatorParams( Builder builder )
    {
        final Instant now = Instant.now();

        this.data = builder.data;
        this.extraDatas = builder.extraDatas;
        this.type = builder.type;
        this.owner = builder.owner;
        this.creator = builder.creator;
        this.modifier = builder.creator;
        this.createdTime = now;
        this.modifiedTime = now;
        this.displayName = builder.displayName;
        this.name = builder.name;
        this.parentContentPath = builder.parent;
        this.valid = builder.valid;
        this.permissions = builder.permissions;
        this.inheritPermissions = builder.inheritPermissions;
        this.createAttachments = builder.createAttachments;
        this.childOrder = builder.childOrder;
        this.language = builder.language;
        this.contentPublishInfo = builder.contentPublishInfo;
        this.processedIds = builder.processedIds;
        this.workflowInfo = builder.workflowInfo;
    }

    public static Builder create( final CreateContentParams source )
    {
        return new Builder( source );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PropertyTree getData()
    {
        return data;
    }

    public ExtraDatas getExtraDatas()
    {
        return extraDatas;
    }

    public ContentTypeName getType()
    {
        return type;
    }

    public PrincipalKey getOwner()
    {
        return owner;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public ContentName getName()
    {
        return name;
    }

    public ContentPath getParent()
    {
        return parentContentPath;
    }

    public boolean isValid()
    {
        return valid;
    }

    public CreateAttachments getCreateAttachments()
    {
        return createAttachments;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public Locale getLanguage()
    {
        return language;
    }

    public ContentPublishInfo getContentPublishInfo()
    {
        return contentPublishInfo;
    }

    public ContentIds getProcessedIds()
    {
        return processedIds;
    }

    public WorkflowInfo getWorkflowInfo()
    {
        return workflowInfo;
    }

    public static final class Builder
    {
        private PropertyTree data;

        private ExtraDatas extraDatas;

        private ContentTypeName type;

        private PrincipalKey owner;

        private PrincipalKey creator;

        private String displayName;

        private ContentName name;

        private ContentPath parent;

        private boolean valid;

        private AccessControlList permissions;

        private boolean inheritPermissions;

        private CreateAttachments createAttachments = CreateAttachments.empty();

        private ChildOrder childOrder;

        private ContentPublishInfo contentPublishInfo;

        private Locale language;

        private ContentIds processedIds;

        private WorkflowInfo workflowInfo;

        private Builder()
        {
        }

        private Builder( final CreateContentParams params )
        {
            this.data = params.getData();
            this.extraDatas = params.getExtraDatas();
            this.type = params.getType();
            this.owner = params.getOwner();
            this.displayName = params.getDisplayName();
            this.name = params.getName();
            this.parent = params.getParent();
            this.permissions = params.getPermissions();
            this.createAttachments = params.getCreateAttachments();
            this.inheritPermissions = params.isInheritPermissions();
            this.childOrder = params.getChildOrder();
            this.language = params.getLanguage();
            this.contentPublishInfo = params.getContentPublishInfo();
            this.processedIds = params.getProcessedIds();
            this.workflowInfo = params.getWorkflowInfo();
        }

        public Builder contentData( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public Builder extraDatas( final ExtraDatas extraDatas )
        {
            this.extraDatas = extraDatas;
            return this;
        }

        public Builder type( final ContentTypeName type )
        {
            this.type = type;
            return this;
        }

        public Builder owner( final PrincipalKey owner )
        {
            this.owner = owner;
            return this;
        }

        public Builder creator( final PrincipalKey creator )
        {
            this.creator = creator;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder name( final ContentName name )
        {
            this.name = name;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = ContentName.from( name );
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder parent( final ContentPath parentContentPath )
        {
            this.parent = parentContentPath;
            return this;
        }

        public Builder valid( final boolean valid )
        {
            this.valid = valid;
            return this;
        }

        public Builder permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        public Builder createAttachments( final CreateAttachments createAttachments )
        {
            this.createAttachments = createAttachments;
            return this;
        }

        public Builder language( final Locale language )
        {
            this.language = language;
            return this;
        }

        public Builder contentPublishInfo( final ContentPublishInfo info )
        {
            this.contentPublishInfo = info;
            return this;
        }

        public Builder processedIds( final ContentIds processedIds )
        {
            this.processedIds = processedIds;
            return this;
        }

        public Builder workflowInfo( final WorkflowInfo workflowInfo )
        {
            this.workflowInfo = workflowInfo;
            return this;
        }

        public PropertyTree getData()
        {
            return data;
        }

        public ContentTypeName getType()
        {
            return type;
        }

        public ContentName getName()
        {
            return name;
        }

        public String getDisplayName()
        {
            return displayName;
        }

        public ExtraDatas getExtraDatas()
        {
            return extraDatas;
        }

        private void validate()
        {
            Preconditions.checkNotNull( parent, "parentPath cannot be null" );
            Preconditions.checkArgument( parent.isAbsolute(), "parentPath must be absolute: " + parent );
            Preconditions.checkNotNull( data, "data cannot be null" );
            Preconditions.checkNotNull( displayName, "displayName cannot be null" );
            Preconditions.checkNotNull( createAttachments, "createAttachments cannot be null" );
            Preconditions.checkNotNull( type, "type cannot be null" );
            Preconditions.checkNotNull( creator, "creator cannot be null" );
            Preconditions.checkNotNull( name, "name cannot be null" );
            Preconditions.checkNotNull( childOrder, "childOrder cannot be null" );
        }

        public CreateContentTranslatorParams build()
        {
            this.validate();
            return new CreateContentTranslatorParams( this );
        }
    }


}
