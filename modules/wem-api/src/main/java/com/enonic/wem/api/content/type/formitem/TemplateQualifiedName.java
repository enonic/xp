package com.enonic.wem.api.content.type.formitem;


import com.enonic.wem.api.content.ModuleBasedQualifiedName;

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
