package com.enonic.xp.core.impl.schema;

import com.enonic.xp.core.impl.app.ApplicationTestSupport;

public abstract class AbstractSchemaTest
    extends ApplicationTestSupport
{
    protected final void initializeApps()
    {
        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );
    }
}
