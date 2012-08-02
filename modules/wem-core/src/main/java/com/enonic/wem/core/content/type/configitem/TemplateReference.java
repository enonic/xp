package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.content.type.TemplateQualifiedName;

public class TemplateReference
    extends ConfigItem
{
    private TemplateQualifiedName templateQualifiedName;

    protected TemplateReference()
    {
        super( ConfigItemType.REFERENCE );
    }

    public TemplateQualifiedName getTemplateQualifiedName()
    {
        return templateQualifiedName;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private TemplateQualifiedName templateQualifiedName;

        private String name;

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder template( TemplateQualifiedName value )
        {
            this.templateQualifiedName = value;
            return this;
        }

        public TemplateReference build()
        {
            TemplateReference templateReference = new TemplateReference();
            templateReference.setName( name );
            templateReference.templateQualifiedName = templateQualifiedName;
            return templateReference;
        }
    }
}
