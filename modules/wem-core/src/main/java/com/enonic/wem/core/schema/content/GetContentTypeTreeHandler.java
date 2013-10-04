package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.schema.content.GetContentTypeTree;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;


public class GetContentTypeTreeHandler
    extends CommandHandler<GetContentTypeTree>
{
    private ContentTypeDao contentTypeDao;

    @Override
    public void handle( final GetContentTypeTree command )
        throws Exception
    {
        final ContentTypeTreeFactory factory = new ContentTypeTreeFactory( context.getJcrSession(), contentTypeDao );
        final Tree<ContentType> contentTypeTree = factory.createTree();
        command.setResult( contentTypeTree );
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
