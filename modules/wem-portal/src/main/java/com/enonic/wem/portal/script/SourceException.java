package com.enonic.wem.portal.script;

import com.enonic.wem.api.module.ModuleResourceKey;

public final class SourceException
    extends RuntimeException
{
    public String getTag()
    {
        return null;
    }

    public ModuleResourceKey getResource()
    {
        return null;
    }

    public int getLineNumber()
    {
        return -1;
    }
}
