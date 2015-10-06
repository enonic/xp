package com.enonic.xp.content;

import java.util.Locale;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;

@Beta
public final class CreateContentParams
{
    private final PropertyTree data;

    private final ExtraDatas extraDatas;

    private final ContentTypeName type;

    private final PrincipalKey owner;

    private final String displayName;

    private final ContentName name;

    private final ContentPath parentContentPath;

    private final boolean requireValid;

    private final CreateAttachments createAttachments;

    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private final ChildOrder childOrder;

    private final Locale language;

    private CreateContentParams( Builder builder )
    {
        this.data = builder.data;
        this.extraDatas = builder.extraDatas;
        this.type = builder.type;
        this.owner = builder.owner;
        this.displayName = builder.displayName;
        this.name = builder.name;
        this.parentContentPath = builder.parentPath;
        this.requireValid = builder.requireValid;
        this.permissions = builder.permissions;
        this.inheritPermissions = builder.inheritPermissions;
        this.createAttachments = builder.createAttachments;
        this.childOrder = builder.childOrder;
        this.language = builder.language;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final CreateContentParams source )
    {
        return new Builder( source );
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

    public boolean isRequireValid()
    {
        return requireValid;
    }

    public CreateAttachments getCreateAttachments()
    {
        return createAttachments;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
    }

    public Locale getLanguage()
    {
        return language;
    }

    public static final class Builder
    {
        private PropertyTree data;

        private ExtraDatas extraDatas;

        private ContentTypeName type;

        private PrincipalKey owner;

        private String displayName;

        private ContentName name;

        private ContentPath parentPath;

        private boolean requireValid;

        private AccessControlList permissions;

        private boolean inheritPermissions = true;

        private CreateAttachments createAttachments = CreateAttachments.empty();

        private ChildOrder childOrder;

        private Locale language;

        private Builder()
        {
        }

        private Builder( final CreateContentParams source )
        {
            this.data = source.data;
            this.extraDatas = source.extraDatas;
            this.type = source.type;
            this.owner = source.owner;
            this.displayName = source.displayName;
            this.name = source.name;
            this.parentPath = source.parentContentPath;
            this.requireValid = source.requireValid;
            this.permissions = source.permissions;
            this.inheritPermissions = source.inheritPermissions;
            this.createAttachments = source.createAttachments;
            this.childOrder = source.childOrder;
            this.language = source.language;
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


        public Builder parent( final ContentPath parentContentPath )
        {
            this.parentPath = parentContentPath;
            return this;
        }

        public Builder requireValid( final boolean requireValid )
        {
            this.requireValid = requireValid;
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

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder language( final Locale language )
        {
            this.language = language;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( parentPath, "parentContentPath cannot be null" );
            Preconditions.checkArgument( parentPath.isAbsolute(), "parentContentPath must be absolute: " + parentPath );
            Preconditions.checkNotNull( data, "data cannot be null" );
            Preconditions.checkNotNull( createAttachments, "createAttachments cannot be null" );
            Preconditions.checkNotNull( type, "type cannot be null" );
        }

        public CreateContentParams build()
        {
            this.validate();
            return new CreateContentParams( this );
        }
    }
}
