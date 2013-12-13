package com.enonic.wem.portal.script.loader;

import java.net.URI;

import com.enonic.wem.api.module.ModuleKey;

public interface ScriptSource
{
    public String getName();

    public URI getUri();

    public String getScriptAsString();

    public long getTimestamp();

    public ModuleKey getModule();

    public boolean isFromSystem();

    public boolean isFromModule();
}
