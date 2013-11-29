package com.enonic.wem.core.blob.binary.dao;

import javax.jcr.Session;

import com.enonic.wem.core.jcr.JcrConstants;

abstract class AbstractBinaryDaoHandler
{
    public static final String BINARIES_NODE = "binaries";

    public static final String BINARIES_PATH = JcrConstants.ROOT_NODE + "/" + BINARIES_NODE + "/";

    public static final String DATA_PROPERTY = "data";

    protected final Session session;

    protected AbstractBinaryDaoHandler( final Session session )
    {
        this.session = session;
    }
}
