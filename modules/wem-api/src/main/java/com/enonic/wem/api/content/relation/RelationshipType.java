package com.enonic.wem.api.content.relation;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.module.ModuleName;

public class RelationshipType
{
    private final ModuleName module;

    private final String name;

    private final QualifiedRelationshipTypeName qualifiedRelationshipTypeName;

    private final String fromSemantic;

    private final String toSemantic;

    private final QualifiedContentTypeNames allowedFromTypes;

    private final QualifiedContentTypeNames allowedToTypes;

    private RelationshipType( final ModuleName module, final String name, final String fromSemantic, final String toSemantic,
                              final QualifiedContentTypeNames allowedFromTypes, final QualifiedContentTypeNames allowedToTypes )
    {
        this.module = module;
        this.name = name;
        this.qualifiedRelationshipTypeName = new QualifiedRelationshipTypeName( module, name );
        this.fromSemantic = fromSemantic;
        this.toSemantic = toSemantic;
        this.allowedFromTypes = allowedFromTypes;
        this.allowedToTypes = allowedToTypes;
    }

    public ModuleName getModule()
    {
        return module;
    }

    public String getName()
    {
        return name;
    }

    public QualifiedRelationshipTypeName getQualifiedRelationshipTypeName()
    {
        return qualifiedRelationshipTypeName;
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

    public static Builder newRelationType()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ModuleName module;

        private String name;

        private String fromSemantic;

        private String toSemantic;

        private List<QualifiedContentTypeName> allowedFromTypes = Lists.newArrayList();

        private List<QualifiedContentTypeName> allowedToTypes = Lists.newArrayList();

        public Builder module( ModuleName value )
        {
            this.module = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder fromSemantic( String value )
        {
            this.fromSemantic = value;
            return this;
        }

        public Builder toSemantic( String value )
        {
            this.toSemantic = value;
            return this;
        }

        public Builder addAllowedFromType( QualifiedContentTypeName value )
        {
            allowedFromTypes.add( value );
            return this;
        }

        public Builder addAllowedToType( QualifiedContentTypeName value )
        {
            allowedToTypes.add( value );
            return this;
        }

        public RelationshipType build()
        {
            return new RelationshipType( module, name, fromSemantic, toSemantic, QualifiedContentTypeNames.from( allowedFromTypes ),
                                         QualifiedContentTypeNames.from( allowedToTypes ) );
        }
    }
}
