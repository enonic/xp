package com.enonic.xp.script.event;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.EventListener;

public interface ScriptEventListener
    extends EventListener
{
    ApplicationKey getApplication();
}
