package com.enonic.wem.core.script.cache;

import java.util.concurrent.Callable;

import javax.script.CompiledScript;

public interface ScriptCache
{
    public CompiledScript get( String key, Callable<CompiledScript> loader );
}
