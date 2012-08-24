package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.content.ModuleBasedQualifiedName;

public class TemplateQualifiedName
    extends ModuleBasedQualifiedName
{
    public TemplateQualifiedName( final String qualifiedName )
    {
        super( qualifiedName );
    }

    public TemplateQualifiedName( final String moduleName, final String templateName )
    {
        super( moduleName, templateName );
    }

    public String getTemplateName()
    {
        return getLocalName();
    }

}
