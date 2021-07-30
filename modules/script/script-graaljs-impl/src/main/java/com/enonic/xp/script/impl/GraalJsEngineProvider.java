package com.enonic.xp.script.impl;

import org.graalvm.polyglot.Engine;

public interface GraalJsEngineProvider
{
    Engine getEngine();
}
