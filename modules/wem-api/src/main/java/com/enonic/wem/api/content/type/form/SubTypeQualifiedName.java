package com.enonic.wem.api.content.type.form;


import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.ModuleName;

public class SubTypeQualifiedName
    extends ModuleBasedQualifiedName
{
    public SubTypeQualifiedName( final String qualifiedName )
    {
        super( qualifiedName );
    }

    public SubTypeQualifiedName( final ModuleName moduleName, final String subTypeName )
    {
        super( moduleName, subTypeName );
    }

    public SubTypeQualifiedName( final String moduleName, final String subTypeName )
    {
        super( new ModuleName( moduleName ), subTypeName );
    }

    public String getSubTypeName()
    {
        return getLocalName();
    }

}
