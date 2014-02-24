package com.enonic.wem.core.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.ContentId;

@Deprecated
public abstract class AbstractContentDaoHandler
{
    protected final Node doGetContentNode( final ContentId contentId )
        throws RepositoryException
    {
        throw new RuntimeException( "ContentDao should not be used" );
    }

}
