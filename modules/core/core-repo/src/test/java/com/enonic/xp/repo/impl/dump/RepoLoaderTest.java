package com.enonic.xp.repo.impl.dump;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;

public class RepoLoaderTest
    extends AbstractNodeTest
{

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        createDefaultRootNode();
    }

}