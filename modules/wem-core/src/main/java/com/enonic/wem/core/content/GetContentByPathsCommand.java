package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;


final class GetContentByPathsCommand
    extends AbstractContentCommand<GetContentByPathsCommand>
{
    private ContentPaths contentPaths;

    Contents execute()
    {
        final Contents contents;

        try
        {
            contents = doExecute();
        }
        catch ( NoNodeAtPathFoundException ex )
        {
            throw new ContentNotFoundException( ContentPath.from( ex.getPath().toString() ) );
        }

        return contents;
    }

    private Contents doExecute()
    {
        final NodePaths paths = ContentNodeHelper.translateContentPathsToNodePaths( contentPaths );
        final Nodes nodes = nodeService.getByPaths( paths );

        return getTranslator().fromNodes( nodes );
    }

    GetContentByPathsCommand contentPaths( final ContentPaths paths )
    {
        this.contentPaths = paths;
        return this;
    }
}
