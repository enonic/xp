package com.enonic.wem.portal.script.resolver;

import java.nio.file.Path;

public interface ScriptResolver
{
    public Path resolve( String name );

    public Path resolve( Path parent, String name );
}
