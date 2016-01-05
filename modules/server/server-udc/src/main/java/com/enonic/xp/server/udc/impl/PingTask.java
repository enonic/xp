package com.enonic.xp.server.udc.impl;

import java.util.TimerTask;

final class PingTask
    extends TimerTask
{
    private final PingSender sender;

    public PingTask( final PingSender sender )
    {
        this.sender = sender;
    }

    @Override
    public void run()
    {
        this.sender.send();
    }
}
