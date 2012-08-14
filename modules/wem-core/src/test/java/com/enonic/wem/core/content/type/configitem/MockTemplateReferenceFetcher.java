package com.enonic.wem.core.content.type.configitem;

import java.util.HashMap;
import java.util.Map;

public class MockTemplateReferenceFetcher
    implements TemplateReferenceFetcher
{
    private Map<TemplateQualifiedName, Template> templateMap = new HashMap<TemplateQualifiedName, Template>();

    @Override
    public Template getTemplate( final TemplateQualifiedName templateQualifiedName )
    {
        return templateMap.get( templateQualifiedName );
    }

    public void add( final FieldSetTemplate fieldSetTemplate )
    {
        templateMap.put( fieldSetTemplate.getTemplateQualifiedName(), fieldSetTemplate );
    }

    public void add( final FieldTemplate fieldtemplate )
    {
        templateMap.put( fieldtemplate.getTemplateQualifiedName(), fieldtemplate );
    }
}
