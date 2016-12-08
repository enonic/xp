package com.enonic.xp.script.event;

public interface ScriptEventManager
    extends Iterable<ScriptEventListener>
{
    void add( ScriptEventListener listener );
}
