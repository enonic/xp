package com.enonic.wem.api.content.type.formitem;

import java.util.HashMap;
import java.util.Map;

public class MockTemplateFetcher
    implements TemplateFetcher
{
    private Map<TemplateQualifiedName, Template> templateMap = new HashMap<TemplateQualifiedName, Template>();

    @Override
    public Template getTemplate( final TemplateQualifiedName qualifiedName )
    {
        return templateMap.get( qualifiedName );
    }

    public void add( final FormItemSetTemplate formItemSetTemplate )
    {
        templateMap.put( formItemSetTemplate.getQualifiedName(), formItemSetTemplate );
    }

    public void add( final ComponentTemplate fieldtemplate )
    {
        templateMap.put( fieldtemplate.getQualifiedName(), fieldtemplate );
    }
}
