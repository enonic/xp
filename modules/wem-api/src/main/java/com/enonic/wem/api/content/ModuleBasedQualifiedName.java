package com.enonic.wem.api.content;


import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Name;
import com.enonic.wem.api.module.ModuleName;

public abstract class ModuleBasedQualifiedName
{
    private final ModuleName moduleName;

    private final Name localName;

    private final String refString;

    public ModuleBasedQualifiedName( final String qualifiedName )
    {
        Preconditions.checkNotNull( qualifiedName, "QualifiedName is null" );
        Preconditions.checkArgument( StringUtils.isNotBlank( qualifiedName ), "QualifiedName is blank" );

        int colonPos = qualifiedName.indexOf( ":" );
        Preconditions.checkArgument( colonPos >= 0, "QualifiedName is missing colon: " + qualifiedName );
        Preconditions.checkArgument( colonPos > 0, "QualifiedName is missing module name: " + qualifiedName );
        Preconditions.checkArgument( colonPos < qualifiedName.length() - 1, "QualifiedName is missing local name: " + qualifiedName );

        this.moduleName = ModuleName.from( qualifiedName.substring( 0, colonPos ) );
        this.localName = Name.from( qualifiedName.substring( colonPos + 1, qualifiedName.length() ) );
        this.refString = qualifiedName;
    }

    public ModuleBasedQualifiedName( final ModuleName moduleName, final String localName )
    {
        this.moduleName = moduleName;
        this.localName = Name.from( localName );
        this.refString = moduleName + ":" + localName;
    }

    public ModuleBasedQualifiedName( final ModuleName moduleName, final Name localName )
    {
        this.moduleName = moduleName;
        this.localName = localName;
        this.refString = moduleName + ":" + localName;
    }

    public ModuleName getModuleName()
    {
        return moduleName;
    }

    public String getLocalName()
    {
        return localName.toString();
    }

    public String toString()
    {
        return refString;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ModuleBasedQualifiedName that = (ModuleBasedQualifiedName) o;

        return refString.equals( that.refString );
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }
}
