package com.enonic.xp.launcher.impl.framework;

public enum FrameworkLifecycleAction
{
    RESET, RESTART;

    public static FrameworkLifecycleAction from( int value )
    {
        switch ( value )
        {
            case 1:
                return RESET;
            case 2:
                return RESTART;
            default:
                throw new IllegalArgumentException( "Invalid action " + value );
        }
    }
}
