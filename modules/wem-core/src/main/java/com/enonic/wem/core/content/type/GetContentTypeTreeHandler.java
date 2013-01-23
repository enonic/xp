package com.enonic.wem.core.content.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.GetContentTypeTree;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

@Component
public class GetContentTypeTreeHandler
    extends CommandHandler<GetContentTypeTree>
{
    private ContentTypeDao contentTypeDao;

    public GetContentTypeTreeHandler()
    {
        super( GetContentTypeTree.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContentTypeTree command )
        throws Exception
    {
        final ContentTypeTreeFactory factory = new ContentTypeTreeFactory( context.getJcrSession(), contentTypeDao );
        final Tree<ContentType> contentTypeTree = factory.createTree();
        command.setResult( contentTypeTree );
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
