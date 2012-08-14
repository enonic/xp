package com.enonic.wem.core.content.type;


import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.TemplateReference;

public interface Template
{
    public TemplateQualifiedName getTemplateQualifiedName();

    public ConfigItem create( final TemplateReference templateReference );
}
