package com.enonic.wem.core.content.type.configitem;


public interface Template
{
    public TemplateQualifiedName getTemplateQualifiedName();

    public ConfigItem create( final TemplateReference templateReference );
}
