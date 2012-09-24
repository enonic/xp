package com.enonic.wem.api.content.type;


import com.enonic.wem.api.content.ModuleBasedQualifiedName;

public class ContentTypeQualifiedName
    extends ModuleBasedQualifiedName
{
    public ContentTypeQualifiedName( final String qualifiedName )
    {
        super( qualifiedName );
    }

    public ContentTypeQualifiedName( final String moduleName, final String contentTypeName )
    {
        super( moduleName, contentTypeName );
    }

    public String getContentTypeName()
    {
        return getLocalName();
    }
}
