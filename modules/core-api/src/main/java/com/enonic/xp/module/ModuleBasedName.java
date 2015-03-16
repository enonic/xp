package com.enonic.xp.module;


import com.google.common.base.Joiner;

public abstract class ModuleBasedName
{
    protected final static String SEPARATOR = ":";

    private final String refString;

    private final ModuleKey moduleKey;

    private final String localName;

    protected ModuleBasedName( final ModuleKey moduleKey, final String localName )
    {
        this.moduleKey = moduleKey;
        this.localName = localName;
        this.refString = Joiner.on( SEPARATOR ).join( this.moduleKey.toString(), this.localName );
    }

    public String getLocalName()
    {
        return localName;
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
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

        final ModuleBasedName moduleBasedName = (ModuleBasedName) o;

        return refString.equals( moduleBasedName.refString );
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }

}
