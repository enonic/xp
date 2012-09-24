package com.enonic.wem.api.content.type.formitem;


import com.google.common.base.Preconditions;

public class TemplateReference
    extends HierarchicalFormItem
{
    private TemplateQualifiedName templateQualifiedName;

    private Class templateType;

    private Occurrences occurrences;

    private boolean immutable;

    protected TemplateReference()
    {
        super();
    }

    public TemplateQualifiedName getTemplateQualifiedName()
    {
        return templateQualifiedName;
    }

    public Class getTemplateType()
    {
        return templateType;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static Builder newTemplateReference()
    {
        return new Builder();
    }

    @Override
    public TemplateReference copy()
    {
        TemplateReference templateReference = (TemplateReference) super.copy();
        templateReference.templateQualifiedName = this.templateQualifiedName;
        templateReference.templateType = this.templateType;
        return templateReference;
    }

    public static Builder newTemplateReference( final Template template )
    {
        Builder builder = new Builder();
        builder.templateType = template.getType().getSimpleName();
        builder.templateQualifiedName = template.getQualifiedName();
        return builder;
    }

    public static class Builder
    {
        private TemplateQualifiedName templateQualifiedName;

        private String name;

        private String templateType;

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder template( String templateQualifiedName )
        {
            this.templateQualifiedName = new TemplateQualifiedName( templateQualifiedName );
            return this;
        }

        public Builder template( TemplateQualifiedName templateQualifiedName )
        {
            this.templateQualifiedName = templateQualifiedName;
            return this;
        }

        public Builder type( String value )
        {
            this.templateType = value;
            return this;
        }

        public Builder type( Class value )
        {
            this.templateType = value.getSimpleName();
            return this;
        }

        public Builder typeComponent()
        {
            this.templateType = ComponentTemplate.class.getSimpleName();
            return this;
        }

        public Builder typeFormItemSet()
        {
            this.templateType = FormItemSetTemplate.class.getSimpleName();
            return this;
        }

        public TemplateReference build()
        {
            Preconditions.checkNotNull( templateQualifiedName, "templateQualifiedName is required" );
            Preconditions.checkNotNull( templateType, "templateType is required" );
            Preconditions.checkNotNull( name, "name is required" );

            final TemplateReference templateReference = new TemplateReference();
            templateReference.setName( name );
            templateReference.templateQualifiedName = templateQualifiedName;
            try

            {
                final String packageName = this.getClass().getPackage().getName();
                templateReference.templateType = Class.forName( packageName + "." + templateType );
            }
            catch ( ClassNotFoundException e )
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return templateReference;
        }
    }
}
