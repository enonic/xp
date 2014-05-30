package com.enonic.wem.api.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public final class CreateRelationshipTypeParams
{
    private RelationshipTypeName name;

    private String displayName;

    private String description;

    private String fromSemantic;

    private String toSemantic;

    private ContentTypeNames allowedFromTypes;

    private ContentTypeNames allowedToTypes;

    private Icon schemaIcon;

    public CreateRelationshipTypeParams name( final String name )
    {
        this.name = RelationshipTypeName.from( name );
        return this;
    }

    public CreateRelationshipTypeParams name( final RelationshipTypeName name )
    {
        this.name = name;
        return this;
    }

    public CreateRelationshipTypeParams displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateRelationshipTypeParams description( final String description )
    {
        this.description = description;
        return this;
    }

    public CreateRelationshipTypeParams fromSemantic( final String fromSemantic )
    {
        this.fromSemantic = fromSemantic;
        return this;
    }

    public CreateRelationshipTypeParams toSemantic( final String toSemantic )
    {
        this.toSemantic = toSemantic;
        return this;
    }

    public CreateRelationshipTypeParams allowedFromTypes( final ContentTypeNames allowedFromTypes )
    {
        this.allowedFromTypes = allowedFromTypes;
        return this;
    }

    public CreateRelationshipTypeParams allowedToTypes( final ContentTypeNames allowedToTypes )
    {
        this.allowedToTypes = allowedToTypes;
        return this;
    }

    public CreateRelationshipTypeParams schemaIcon( final Icon schemaIcon )
    {
        this.schemaIcon = schemaIcon;
        return this;
    }

    public RelationshipTypeName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public String getFromSemantic()
    {
        return fromSemantic;
    }

    public String getToSemantic()
    {
        return toSemantic;
    }

    public ContentTypeNames getAllowedFromTypes()
    {
        return allowedFromTypes;
    }

    public ContentTypeNames getAllowedToTypes()
    {
        return allowedToTypes;
    }

    public Icon getSchemaIcon()
    {
        return schemaIcon;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateRelationshipTypeParams ) )
        {
            return false;
        }

        final CreateRelationshipTypeParams that = (CreateRelationshipTypeParams) o;
        return Objects.equal( this.name, that.name ) &&
            Objects.equal( this.displayName, that.displayName ) &&
            Objects.equal( this.description, that.description ) &&
            Objects.equal( this.fromSemantic, that.fromSemantic ) &&
            Objects.equal( this.toSemantic, that.toSemantic ) &&
            Objects.equal( this.allowedFromTypes, that.allowedFromTypes ) &&
            Objects.equal( this.allowedToTypes, that.allowedToTypes ) &&
            Objects.equal( this.schemaIcon, that.schemaIcon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, displayName, description, fromSemantic, toSemantic, allowedFromTypes, allowedToTypes, schemaIcon );
    }

    public void validate()
    {
        Preconditions.checkNotNull( name, "name cannot be null" );
        Preconditions.checkNotNull( fromSemantic, "fromSemantic cannot be null" );
        Preconditions.checkNotNull( toSemantic, "toSemantic cannot be null" );
    }
}
