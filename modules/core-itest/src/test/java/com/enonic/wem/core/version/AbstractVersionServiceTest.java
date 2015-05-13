package com.enonic.wem.core.version;

import org.junit.Before;

import com.enonic.wem.core.AbstractIntegrationTest;
import com.enonic.xp.branch.Branch;

public class AbstractVersionServiceTest
    extends AbstractIntegrationTest
{

    protected static final Branch WS_DEFAULT = Branch.create().
        name( "draft" ).
        build();


    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        createContentRepository();
        waitForClusterHealth();
        createDefaultRootNode();
    }


}
