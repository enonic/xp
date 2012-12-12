package com.enonic.wem.api.content.type.form;


import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.ModuleName;

public class QualifiedSubTypeName
    extends ModuleBasedQualifiedName
{
    public QualifiedSubTypeName( final String qualifiedName )
    {
        super( qualifiedName );
    }

    public QualifiedSubTypeName( final ModuleName moduleName, final String subTypeName )
    {
        super( moduleName, subTypeName );
    }

    public QualifiedSubTypeName( final String moduleName, final String subTypeName )
    {
        super( new ModuleName( moduleName ), subTypeName );
    }

    public String getSubTypeName()
    {
        return getLocalName();
    }

    public static QualifiedSubTypeName from( final String value )
    {
        return new QualifiedSubTypeName( value );
    }
}
