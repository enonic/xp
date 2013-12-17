package com.enonic.wem.core.content;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.entity.GetNodesByParentService;

public class ContentHasChildPopulator
{
    // TODO: This is not very nice. We have to decide how to do this more efficient.

    private final static ContentNodeTranslator CONTENT_NODE_TRANSLATOR = new ContentNodeTranslator();

    public Contents populateHasChild( final Session session, final Contents contents )
    {
        final Contents.Builder contentsBuilder = new Contents.Builder();

        for ( final Content content : contents )
        {
            final GetNodesByParent getNodesByParentCommand =
                new GetNodesByParent( ContentNodeHelper.translateContentPathToNodePath( content.getPath() ) );

            final Nodes nodes = new GetNodesByParentService( session, getNodesByParentCommand ).execute();

            if ( hasContentNode( nodes ) )
            {
                contentsBuilder.add( Content.newContent( content ).addChildId( ContentId.from( "Dummy" ) ).build() );
            }
            else
            {
                contentsBuilder.add( content );
            }
        }

        return contentsBuilder.build();
    }

    private boolean hasContentNode( final Nodes nodes )
    {
        for ( final Node node : nodes )
        {
            if ( !node.name().toString().startsWith( ContentDao.NON_CONTENT_NODE_PREFIX ) )
            {
                return true;
            }
        }

        return false;
    }

}
