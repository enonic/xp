package com.enonic.wem.api.content.type.form;


import com.enonic.wem.api.content.ModuleBasedQualifiedName;

public class SubTypeQualifiedName
    extends ModuleBasedQualifiedName
{
    public SubTypeQualifiedName( final String qualifiedName )
    {
        super( qualifiedName );
    }

    public SubTypeQualifiedName( final String moduleName, final String subTypeName )
    {
        super( moduleName, subTypeName );
    }

    public String getSubTypeName()
    {
        return getLocalName();
    }

}
