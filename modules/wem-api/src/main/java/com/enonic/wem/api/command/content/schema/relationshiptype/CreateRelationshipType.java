package com.enonic.wem.api.command.content.schema.relationshiptype;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.module.ModuleName;

public final class CreateRelationshipType
    extends Command<QualifiedRelationshipTypeName>
{
    private String name;

    private String displayName;

    private ModuleName module;

    private String fromSemantic;

    private String toSemantic;

    private QualifiedContentTypeNames allowedFromTypes;

    private QualifiedContentTypeNames allowedToTypes;

    private Icon icon;

    public CreateRelationshipType name( final String name )
    {
        this.name = name;
        return this;
    }

    public CreateRelationshipType displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateRelationshipType module( final ModuleName module )
    {
        this.module = module;
        return this;
    }

    public CreateRelationshipType fromSemantic( final String fromSemantic )
    {
        this.fromSemantic = fromSemantic;
        return this;
    }

    public CreateRelationshipType toSemantic( final String toSemantic )
    {
        this.toSemantic = toSemantic;
        return this;
    }

    public CreateRelationshipType allowedFromTypes( final QualifiedContentTypeNames allowedFromTypes )
    {
        this.allowedFromTypes = allowedFromTypes;
        return this;
    }

    public CreateRelationshipType allowedToTypes( final QualifiedContentTypeNames allowedToTypes )
    {
        this.allowedToTypes = allowedToTypes;
        return this;
    }

    public CreateRelationshipType icon( final Icon icon )
    {
        this.icon = icon;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public ModuleName getModule()
    {
        return module;
    }

    public String getFromSemantic()
    {
        return fromSemantic;
    }

    public String getToSemantic()
    {
        return toSemantic;
    }

    public QualifiedContentTypeNames getAllowedFromTypes()
    {
        return allowedFromTypes;
    }

    public QualifiedContentTypeNames getAllowedToTypes()
    {
        return allowedToTypes;
    }

    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateRelationshipType ) )
        {
            return false;
        }

        final CreateRelationshipType that = (CreateRelationshipType) o;
        return Objects.equal( this.name, that.name ) &&
            Objects.equal( this.displayName, that.displayName ) &&
            Objects.equal( this.module, that.module ) &&
            Objects.equal( this.fromSemantic, that.fromSemantic ) &&
            Objects.equal( this.toSemantic, that.toSemantic ) &&
            Objects.equal( this.allowedFromTypes, that.allowedFromTypes ) &&
            Objects.equal( this.allowedToTypes, that.allowedToTypes ) &&
            Objects.equal( this.icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, displayName, module, fromSemantic, toSemantic, allowedFromTypes, allowedToTypes, icon );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( name, "name cannot be null" );
        Preconditions.checkNotNull( module, "module cannot be null" );
        Preconditions.checkNotNull( fromSemantic, "fromSemantic cannot be null" );
        Preconditions.checkNotNull( toSemantic, "toSemantic cannot be null" );
    }
}
