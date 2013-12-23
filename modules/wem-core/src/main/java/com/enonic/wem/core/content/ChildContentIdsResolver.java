package com.enonic.wem.core.content;

import javax.jcr.Session;

import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;

class ChildContentIdsResolver
{
    private final Session session;

    ChildContentIdsResolver( final Session session )
    {
        this.session = session;
    }

    Content resolve( final Content content )
    {
        final Contents children = new GetChildContentService( session, new GetChildContent().parentPath( content.getPath() ) ).execute();

        if ( children.isNotEmpty() )
        {
            final Content.Builder builder = Content.newContent( content );
            for ( Content child : children )
            {
                builder.addChildId( child.getId() );
            }
            return builder.build();
        }
        else
        {
            return content;
        }
    }

    Contents resolve( final Contents contents )
    {
        final Contents.Builder builder = new Contents.Builder();

        for ( final Content content : contents )
        {
            builder.add( resolve( content ) );
        }

        return builder.build();
    }

}
