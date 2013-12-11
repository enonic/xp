package com.enonic.wem.portal.script.cache;

import org.mozilla.javascript.Script;

public interface ScriptCache
{
    public Script get( String key );

    public void put( String key, Script script );
}
