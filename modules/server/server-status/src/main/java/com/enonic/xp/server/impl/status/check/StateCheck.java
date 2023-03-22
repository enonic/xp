package com.enonic.xp.server.impl.status.check;

public interface StateCheck
{
    StateCheckResult check();

    void deactivate();
}


