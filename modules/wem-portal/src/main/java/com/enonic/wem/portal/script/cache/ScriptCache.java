package com.enonic.wem.portal.script.cache;

import java.nio.file.Path;

import org.mozilla.javascript.Script;

public interface ScriptCache
{
    public Script get( Path path );

    public void put( Path path, Script script );
}
