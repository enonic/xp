package com.enonic.wem.api.command.content.relation;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.module.ModuleName;

public final class CreateRelationshipType
    extends Command<QualifiedRelationshipTypeName>
{
    private String name;

    private ModuleName module;

    private String fromSemantic;

    private String toSemantic;

    private QualifiedContentTypeNames allowedFromTypes;

    private QualifiedContentTypeNames allowedToTypes;

    public CreateRelationshipType relationshipType( final RelationshipType relationshipType )
    {
        this.name = relationshipType.getName();
        this.module = relationshipType.getModuleName();
        this.fromSemantic = relationshipType.getFromSemantic();
        this.toSemantic = relationshipType.getToSemantic();
        this.allowedFromTypes = relationshipType.getAllowedFromTypes();
        this.allowedToTypes = relationshipType.getAllowedToTypes();
        return this;
    }

    public String getName()
    {
        return name;
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
            Objects.equal( this.module, that.module ) &&
            Objects.equal( this.fromSemantic, that.fromSemantic ) &&
            Objects.equal( this.toSemantic, that.toSemantic ) &&
            Objects.equal( this.allowedFromTypes, that.allowedFromTypes ) &&
            Objects.equal( this.allowedToTypes, that.allowedToTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, module, fromSemantic, toSemantic, allowedFromTypes, allowedToTypes );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
    }
}
