package com.enonic.xp.lib.content;

import java.util.stream.Collectors;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;

public class GetOutboundDependenciesHandler
    extends BaseContextHandler
{

    private String key;

    public void setKey( final String key )
    {
        this.key = key;
    }

    @Override
    protected Object doExecute()
    {
        validate();

        ContentIds contentIds;

        if ( this.key.startsWith( "/" ) )
        {
            final Content content = contentService.getByPath( ContentPath.from( this.key ) );

            contentIds = contentService.getOutboundDependencies( content.getId() );
        }
        else
        {
            contentIds = contentService.getOutboundDependencies( ContentId.from( key ) );
        }

        return contentIds.stream().map( ContentId::toString ).collect( Collectors.toList());
    }

    private void validate()
    {
        if ( key == null || "".equals( key.trim() ) )
        {
            throw new IllegalArgumentException( "Parameter 'key' is required" );
        }
    }

}
