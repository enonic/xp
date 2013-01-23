package com.enonic.wem.core.content.dao;

import javax.jcr.Session;

import com.enonic.wem.core.jcr.JcrConstants;

abstract class AbstractSpaceDaoHandler
{
    static final String SPACES_NODE = "spaces";

    static final String SPACES_PATH = JcrConstants.ROOT_NODE + "/" + SPACES_NODE + "/";

    static final String SPACE_CONTENT_ROOT_NODE = "root";

    protected final Session session;

    protected final SpaceJcrMapper spaceJcrMapper = new SpaceJcrMapper();

    AbstractSpaceDaoHandler( final Session session )
    {
        this.session = session;
    }
}
