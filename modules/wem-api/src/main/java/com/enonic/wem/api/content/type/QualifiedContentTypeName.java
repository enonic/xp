package com.enonic.wem.api.content.type;


import com.enonic.wem.api.content.ModuleBasedQualifiedName;

public class QualifiedContentTypeName
    extends ModuleBasedQualifiedName
{
    public QualifiedContentTypeName( final String qualifiedName )
    {
        super( qualifiedName );
    }

    public QualifiedContentTypeName( final String moduleName, final String contentTypeName )
    {
        super( moduleName, contentTypeName );
    }

    public String getContentTypeName()
    {
        return getLocalName();
    }
}
