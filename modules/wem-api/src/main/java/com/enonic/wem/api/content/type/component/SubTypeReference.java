package com.enonic.wem.api.content.type.component;


import com.google.common.base.Preconditions;

public class SubTypeReference
    extends HierarchicalComponent
{
    private SubTypeQualifiedName subTypeQualifiedName;

    private Class subTypeClass;

    private Occurrences occurrences;

    private boolean immutable;

    protected SubTypeReference()
    {
        super();
    }

    public SubTypeQualifiedName getSubTypeQualifiedName()
    {
        return subTypeQualifiedName;
    }

    public Class getSubTypeClass()
    {
        return subTypeClass;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static Builder newSubTypeReference()
    {
        return new Builder();
    }

    @Override
    public SubTypeReference copy()
    {
        SubTypeReference subTypeReference = (SubTypeReference) super.copy();
        subTypeReference.subTypeQualifiedName = this.subTypeQualifiedName;
        subTypeReference.subTypeClass = this.subTypeClass;
        return subTypeReference;
    }

    public static Builder newSubTypeReference( final SubType subType )
    {
        Builder builder = new Builder();
        builder.subTypeClass = subType.getType().getSimpleName();
        builder.subTypeQualifiedName = subType.getQualifiedName();
        return builder;
    }

    public static class Builder
    {
        private SubTypeQualifiedName subTypeQualifiedName;

        private String name;

        private String subTypeClass;

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder subType( String qualifiedName )
        {
            this.subTypeQualifiedName = new SubTypeQualifiedName( qualifiedName );
            return this;
        }

        public Builder subType( SubTypeQualifiedName qualifiedName )
        {
            this.subTypeQualifiedName = qualifiedName;
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

        public Builder typeComponentSet()
        {
            this.subTypeClass = ComponentSetSubType.class.getSimpleName();
            return this;
        }

        public SubTypeReference build()
        {
            Preconditions.checkNotNull( subTypeQualifiedName, "subTypeQualifiedName is required" );
            Preconditions.checkNotNull( subTypeClass, "subTypeClass is required" );
            Preconditions.checkNotNull( name, "name is required" );

            final SubTypeReference subTypeReference = new SubTypeReference();
            subTypeReference.setName( name );
            subTypeReference.subTypeQualifiedName = subTypeQualifiedName;
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
