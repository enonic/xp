package com.enonic.wem.core.content;

import javax.jcr.Session;

import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.Contents;

public class ContentHasChildPopulator
{

    public Contents populateHasChild( final Session session, final Contents contents )
    {
        final Contents.Builder contentsBuilder = new Contents.Builder();

        for ( final Content content : contents )
        {
            GetChildContent getChildContentCommand = new GetChildContent().parentPath( content.getPath() );
            final Contents childContents = new GetChildContentService( session, getChildContentCommand ).execute();

            if ( hasContentNode( childContents ) )
            {
                // TODO: set of children to be removed, use dummy for now
                contentsBuilder.add( Content.newContent( content ).addChildId( ContentId.from( "Dummy" ) ).build() );
            }
            else
            {
                contentsBuilder.add( content );
            }
        }

        return contentsBuilder.build();
    }

    private boolean hasContentNode( final Contents children )
    {
        for ( final Content child : children )
        {
            if ( !child.getName().toString().startsWith( ContentService.NON_CONTENT_NODE_PREFIX ) )
            {
                return true;
            }
        }

        return false;
    }

}
