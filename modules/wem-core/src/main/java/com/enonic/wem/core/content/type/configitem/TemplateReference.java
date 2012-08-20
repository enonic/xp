package com.enonic.wem.core.content.type.configitem;


import org.elasticsearch.common.base.Preconditions;

public class TemplateReference
    extends ConfigItem
{
    private TemplateQualifiedName templateQualifiedName;

    private TemplateType templateType;

    private Occurrences occurrences;

    private boolean immutable;

    protected TemplateReference()
    {
        super( ConfigItemType.REFERENCE );
    }

    public TemplateQualifiedName getTemplateQualifiedName()
    {
        return templateQualifiedName;
    }

    public TemplateType getTemplateType()
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
        builder.templateType = template.getType();
        builder.templateQualifiedName = template.getTemplateQualifiedName();
        return builder;
    }

    public static class Builder
    {
        private TemplateQualifiedName templateQualifiedName;

        private String name;

        private TemplateType templateType;

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

        public Builder type( TemplateType value )
        {
            this.templateType = value;
            return this;
        }

        public Builder typeField()
        {
            this.templateType = TemplateType.FIELD;
            return this;
        }

        public Builder typeFieldSet()
        {
            this.templateType = TemplateType.FIELD_SET;
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
            templateReference.templateType = templateType;
            return templateReference;
        }
    }
}
