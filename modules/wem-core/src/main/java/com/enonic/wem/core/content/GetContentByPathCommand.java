package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;

final class GetContentByPathCommand
    extends AbstractContentCommand<GetContentByPathCommand>
{
    private ContentPath contentPath;

    Content execute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( contentPath );

        try
        {
            final Node node = nodeService.getByPath( nodePath, new Context( ContentConstants.DEFAULT_WORKSPACE ) );
            return getTranslator().fromNode( node );
        }
        catch ( NoNodeAtPathFoundException e )
        {
            throw new ContentNotFoundException( contentPath );
        }
    }

    GetContentByPathCommand contentPath( final ContentPath path )
    {
        this.contentPath = path;
        return this;
    }
}
