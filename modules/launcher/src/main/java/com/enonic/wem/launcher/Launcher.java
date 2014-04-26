package com.enonic.wem.launcher;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.framework.Felix;

public final class Launcher
{
    public static void main( final String... args )
        throws Exception
    {
        final Map<String, Object> config = new HashMap<>();

        final Felix felix = new Felix( config );
        felix.start();
        felix.stop();
    }
}
