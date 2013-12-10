package com.enonic.wem.portal.script.cache;

import java.nio.file.Path;

import org.mozilla.javascript.Script;

final class ScriptCacheEntry
{
    private final Path path;

    private final Script script;

    private final long timestamp;

    public ScriptCacheEntry( final Path path, final Script script )
    {
        this.path = path;
        this.script = script;
        this.timestamp = getCurrentTimestamp();
    }

    public Script getScript()
    {
        return this.script;
    }

    public boolean isModified()
    {
        return getCurrentTimestamp() > this.timestamp;
    }

    private long getCurrentTimestamp()
    {
        return this.path.toFile().lastModified();
    }
}
