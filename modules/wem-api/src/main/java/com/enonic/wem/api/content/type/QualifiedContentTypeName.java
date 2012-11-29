package com.enonic.wem.api.content.type;


import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.ModuleName;

public final class QualifiedContentTypeName
    extends ModuleBasedQualifiedName
{
    public QualifiedContentTypeName( final String qualifiedName )
    {
        super( qualifiedName );
    }

    public QualifiedContentTypeName( final ModuleName moduleName, final String contentTypeName )
    {
        super( moduleName, contentTypeName );
    }

    public String getContentTypeName()
    {
        return getLocalName();
    }
}
