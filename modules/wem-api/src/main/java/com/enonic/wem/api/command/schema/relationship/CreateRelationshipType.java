package com.enonic.wem.api.command.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class CreateRelationshipType
    extends Command<RelationshipTypeName>
{
    private RelationshipTypeName name;

    private String displayName;

    private String description;

    private String fromSemantic;

    private String toSemantic;

    private ContentTypeNames allowedFromTypes;

    private ContentTypeNames allowedToTypes;

    private SchemaIcon schemaIcon;

    public CreateRelationshipType name( final String name )
    {
        this.name = RelationshipTypeName.from( name );
        return this;
    }

    public CreateRelationshipType name( final RelationshipTypeName name )
    {
        this.name = name;
        return this;
    }

    public CreateRelationshipType displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateRelationshipType description( final String description )
    {
        this.description = description;
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

    public CreateRelationshipType allowedFromTypes( final ContentTypeNames allowedFromTypes )
    {
        this.allowedFromTypes = allowedFromTypes;
        return this;
    }

    public CreateRelationshipType allowedToTypes( final ContentTypeNames allowedToTypes )
    {
        this.allowedToTypes = allowedToTypes;
        return this;
    }

    public CreateRelationshipType schemaIcon( final SchemaIcon schemaIcon )
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

    public SchemaIcon getSchemaIcon()
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

        if ( !( o instanceof CreateRelationshipType ) )
        {
            return false;
        }

        final CreateRelationshipType that = (CreateRelationshipType) o;
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

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( name, "name cannot be null" );
        Preconditions.checkNotNull( fromSemantic, "fromSemantic cannot be null" );
        Preconditions.checkNotNull( toSemantic, "toSemantic cannot be null" );
    }
}
