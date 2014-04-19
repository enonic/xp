package com.enonic.wem.portal.script.loader;

import java.nio.file.Path;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;

public interface ScriptSource
{
    public String getName();

    public Path getPath();

    public String getScriptAsString();

    public long getTimestamp();

    public ModuleKey getModule();

    public ModuleResourceKey getResource();
}
