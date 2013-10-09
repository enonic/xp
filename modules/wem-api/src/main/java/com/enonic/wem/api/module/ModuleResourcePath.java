package com.enonic.wem.api.module;


import java.util.Objects;

import com.enonic.wem.api.Path;

public class ModuleResourcePath
{
    private final ModuleKey moduleKey;

    private final Path path;

    private final String refString;

    public ModuleResourcePath( final ModuleKey moduleKey, final Path path )
    {
        this.moduleKey = moduleKey;
        this.path = path;
        this.refString = moduleKey.toString() + ":" + path.toString();
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    public Path getPath()
    {
        return path;
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

        final ModuleResourcePath that = (ModuleResourcePath) o;

        return Objects.equals( this.refString, that.refString );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.refString );
    }

    public String toString()
    {
        return refString;
    }
}
