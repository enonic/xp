package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.content.type.Template;
import com.enonic.wem.core.content.type.TemplateQualifiedName;

public interface TemplateReferenceFetcher
{
    public Template getTemplate( TemplateQualifiedName templateQualifiedName );
}
