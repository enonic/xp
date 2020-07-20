package com.enonic.xp.launcher.impl.framework;

public class FrameworkLifecycleActor
{
    private final FrameworkService framework;

    public FrameworkLifecycleActor( final FrameworkService framework )
    {
        this.framework = framework;
    }

    public void accept( final int value )
    {
        switch ( FrameworkLifecycleAction.from( value ) )
        {
            case RESET:
                this.framework.reset();
                break;
            case RESTART:
                this.framework.restart();
                break;
        }
    }
}
