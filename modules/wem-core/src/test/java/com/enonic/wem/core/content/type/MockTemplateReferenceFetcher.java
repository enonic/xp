package com.enonic.wem.core.content.type;

import java.util.HashMap;
import java.util.Map;

import com.enonic.wem.core.content.type.configitem.TemplateReferenceFetcher;

public class MockTemplateReferenceFetcher
    implements TemplateReferenceFetcher
{
    private Map<TemplateQualifiedName, FieldSetTemplate> templateMap = new HashMap<TemplateQualifiedName, FieldSetTemplate>();

    @Override
    public FieldSetTemplate getTemplate( final TemplateQualifiedName templateQualifiedName )
    {
        return templateMap.get( templateQualifiedName );
    }

    public void add( final FieldSetTemplate fieldSetTemplate )
    {
        templateMap.put( fieldSetTemplate.getTemplateQualifiedName(), fieldSetTemplate );
    }
}
