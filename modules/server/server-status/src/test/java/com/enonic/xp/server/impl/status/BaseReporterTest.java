package com.enonic.xp.server.impl.status;

import org.junit.Before;

import com.enonic.xp.support.JsonTestHelper;

public abstract class BaseReporterTest
{
    protected JsonTestHelper helper;

    @Before
    public final void setup()
        throws Exception
    {
        this.helper = new JsonTestHelper( this );
        initialize();
    }

    protected abstract void initialize()
        throws Exception;
}
