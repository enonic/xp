package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

public class SubTypeReference
    extends HierarchicalFormItem
{
    private QualifiedSubTypeName qualifiedSubTypeName;

    private Class subTypeClass;

    private Occurrences occurrences;

    private boolean immutable;

    protected SubTypeReference()
    {
        super();
    }

    public QualifiedSubTypeName getQualifiedSubTypeName()
    {
        return qualifiedSubTypeName;
    }

    public Class getSubTypeClass()
    {
        return subTypeClass;
    }

    public static Builder newSubTypeReference()
    {
        return new Builder();
    }

    @Override
    public SubTypeReference copy()
    {
        SubTypeReference subTypeReference = (SubTypeReference) super.copy();
        subTypeReference.qualifiedSubTypeName = this.qualifiedSubTypeName;
        subTypeReference.subTypeClass = this.subTypeClass;
        return subTypeReference;
    }

    public static Builder newSubTypeReference( final SubType subType )
    {
        Builder builder = new Builder();
        builder.subTypeClass = subType.getType().getSimpleName();
        builder.qualifiedSubTypeName = subType.getQualifiedName();
        return builder;
    }

    public static class Builder
    {
        private QualifiedSubTypeName qualifiedSubTypeName;

        private String name;

        private String subTypeClass;

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder subType( final SubType subType )
        {
            this.qualifiedSubTypeName = subType.getQualifiedName();
            this.subTypeClass = subType.getType().getSimpleName();
            return this;
        }

        public Builder subType( String qualifiedName )
        {
            this.qualifiedSubTypeName = new QualifiedSubTypeName( qualifiedName );
            return this;
        }

        public Builder subType( QualifiedSubTypeName qualifiedName )
        {
            this.qualifiedSubTypeName = qualifiedName;
            return this;
        }

        public Builder type( String value )
        {
            this.subTypeClass = value;
            return this;
        }

        public Builder type( Class value )
        {
            this.subTypeClass = value.getSimpleName();
            return this;
        }

        public Builder typeInput()
        {
            this.subTypeClass = InputSubType.class.getSimpleName();
            return this;
        }

        public Builder typeFormItemSet()
        {
            this.subTypeClass = FormItemSetSubType.class.getSimpleName();
            return this;
        }

        public SubTypeReference build()
        {
            Preconditions.checkNotNull( qualifiedSubTypeName, "qualifiedSubTypeName is required" );
            Preconditions.checkNotNull( subTypeClass, "subTypeClass is required" );
            Preconditions.checkNotNull( name, "name is required" );

            final SubTypeReference subTypeReference = new SubTypeReference();
            subTypeReference.setName( name );
            subTypeReference.qualifiedSubTypeName = qualifiedSubTypeName;
            try

            {
                final String packageName = this.getClass().getPackage().getName();
                subTypeReference.subTypeClass = Class.forName( packageName + "." + subTypeClass );
            }
            catch ( ClassNotFoundException e )
            {
                e.printStackTrace();
            }
            return subTypeReference;
        }
    }
}
