package com.enonic.wem.core.content.attachment.dao;

import javax.jcr.Session;

import com.enonic.wem.core.content.dao.AbstractContentDaoHandler;
import com.enonic.wem.core.index.IndexService;

abstract class AbstractAttachmentDaoHandler
    extends AbstractContentDaoHandler
{
    protected final AttachmentJcrMapper attachmentJcrMapper = new AttachmentJcrMapper();

    AbstractAttachmentDaoHandler( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

}
