package com.enonic.xp.itest.core;

import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;

public abstract class AbstractOsgiTest
{
    protected static Option project( final String artifact )
    {
        return bundle( "com.enonic.xp", artifact, "6.0.0-SNAPSHOT" );
    }

    protected static Option bundle( final String group, final String artifact, final String version )
    {
        return CoreOptions.bundle(
            "file:/Users/srs/development/workspace/xp/xp-6.0/modules/distro/target/install/system/" + group.replace( '.', '/' ) + "/" +
                artifact + "/" + version + "/" + artifact + "-" + version + ".jar" );
    }
}
