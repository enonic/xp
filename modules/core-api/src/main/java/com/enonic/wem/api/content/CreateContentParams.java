package com.enonic.wem.api.content;

import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.attachment.CreateAttachments;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;

public final class CreateContentParams
{
    private PropertyTree data;

    private List<Metadata> metadata;

    private ContentTypeName type;

    private PrincipalKey owner;

    private String displayName;

    private ContentName name;

    private ContentPath parentContentPath;

    private boolean requireValid;

    private boolean valid;

    private CreateAttachments createAttachments = CreateAttachments.empty();

    private AccessControlList permissions;

    private boolean inheritPermissions;

    public CreateContentParams type( final ContentTypeName value )
    {
        this.type = value;
        return this;
    }

    public CreateContentParams parent( final ContentPath parentContentPath )
    {
        this.parentContentPath = parentContentPath;
        return this;
    }

    public CreateContentParams contentData( final PropertyTree value )
    {
        this.data = value;
        return this;
    }

    public CreateContentParams metadata( final List<Metadata> metadata )
    {
        this.metadata = metadata;
        return this;
    }

    public CreateContentParams createAttachments( final CreateAttachments value )
    {
        this.createAttachments = value;
        return this;
    }

    public CreateContentParams owner( final PrincipalKey owner )
    {
        this.owner = owner;
        return this;
    }

    public CreateContentParams displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateContentParams name( final String name )
    {
        this.name = ContentName.from( name );
        return this;
    }

    public CreateContentParams name( final ContentName name )
    {
        this.name = name;
        return this;
    }

    public CreateContentParams requireValid( final boolean value )
    {
        this.requireValid = value;
        return this;
    }

    public CreateContentParams permissions( final AccessControlList permissions )
    {
        this.permissions = permissions;
        return this;
    }

    public CreateContentParams setInheritPermissions( final boolean inheritPermissions )
    {
        this.inheritPermissions = inheritPermissions;
        return this;
    }

    public CreateContentParams valid( final boolean value )
    {
        this.valid = value;
        return this;
    }

    public ContentPath getParent()
    {
        return parentContentPath;
    }

    public ContentTypeName getType()
    {
        return type;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public List<Metadata> getMetadata()
    {
        return metadata;
    }

    public CreateAttachments getCreateAttachments()
    {
        return createAttachments;
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

    public boolean isRequireValid()
    {
        return requireValid;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
    }

    public boolean isValid()
    {
        return valid;
    }

    public void validate()
    {
        Preconditions.checkNotNull( parentContentPath, "parentContentPath cannot be null" );
        Preconditions.checkArgument( parentContentPath.isAbsolute(), "parentContentPath must be absolute: " + parentContentPath );
        Preconditions.checkNotNull( data, "data cannot be null" );
        Preconditions.checkArgument( requireValid || this.parentContentPath != null, "parentContentPath cannot be null" );
        Preconditions.checkNotNull( displayName, "displayName cannot be null" );
        Preconditions.checkNotNull( createAttachments, "createAttachments cannot be null" );
        Preconditions.checkNotNull( type, "type cannot be null" );
    }
}
