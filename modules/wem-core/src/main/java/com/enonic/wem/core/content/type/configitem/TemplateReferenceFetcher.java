package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.content.type.FieldSetTemplate;
import com.enonic.wem.core.content.type.TemplateQualifiedName;

public interface TemplateReferenceFetcher
{
    public FieldSetTemplate getTemplate( TemplateQualifiedName templateQualifiedName );
}
