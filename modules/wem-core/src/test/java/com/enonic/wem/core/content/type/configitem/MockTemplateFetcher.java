package com.enonic.wem.core.content.type.configitem;

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

    public void add( final FieldSetTemplate fieldSetTemplate )
    {
        templateMap.put( fieldSetTemplate.getQualifiedName(), fieldSetTemplate );
    }

    public void add( final FieldTemplate fieldtemplate )
    {
        templateMap.put( fieldtemplate.getQualifiedName(), fieldtemplate );
    }
}
