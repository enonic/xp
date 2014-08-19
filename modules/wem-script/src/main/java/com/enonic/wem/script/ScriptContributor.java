package com.enonic.wem.script;

import java.util.Map;

public interface ScriptContributor
{
    public Map<String, String> getLibraries();

    public Map<String, Object> getVariables();
}

