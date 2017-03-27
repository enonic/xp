package com.enonic.xp.lib.content;

import java.util.List;
import java.util.stream.Collectors;

import org.codehaus.jparsec.util.Lists;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.lib.content.mapper.ContentMapper;

public final class GetContentsHandler
    extends BaseContextHandler
{
    private String[] keys;

    @Override
    protected Object doExecute()
    {
        ContentPaths contentPaths = ContentPaths.empty();
        ContentIds.Builder idsBuilder = ContentIds.create();

        for ( String key : this.keys )
        {
            if ( key.startsWith( "/" ) )
            {
                contentPaths = contentPaths.add( ContentPath.from( key ) );
            }
            else
            {
                idsBuilder.add( ContentId.from( key ) );
            }
        }

        List<ContentMapper> results = Lists.arrayList();
        if ( contentPaths.getSize() > 0 )
        {
            List<ContentMapper> byPaths = this.getByPaths( contentPaths );
            if ( byPaths != null )
            {
                results.addAll( byPaths );
            }
        }

        ContentIds contentIds = idsBuilder.build();
        if ( contentIds.getSize() > 0 )
        {
            List<ContentMapper> byIds = this.getByIds( contentIds );
            if ( byIds != null )
            {
                results.addAll( byIds );
            }
        }

        return results;
    }

    private List<ContentMapper> getByPaths( final ContentPaths keys )
    {
        if ( keys == null || keys.getSize() == 0 )
        {
            return null;
        }
        try
        {
            return convert( this.contentService.getByPaths( keys ) );
        }
        catch ( final ContentNotFoundException e )
        {
            return this.getByPaths( keys.remove( e.getPaths() ) );
        }
    }

    private List<ContentMapper> getByIds( final ContentIds keys )
    {
        if ( keys == null || keys.getSize() == 0 )
        {
            return null;
        }
        try
        {
            return convert( this.contentService.getByIds( new GetContentByIdsParams( keys ) ) );
        }
        catch ( final ContentNotFoundException e )
        {
            return this.getByIds( keys.remove( e.getIds() ) );
        }
    }

    private List<ContentMapper> convert( final Contents contents )
    {
        return ( contents != null && contents.getSize() > 0 )
            ? contents.stream().map( ContentMapper::new ).collect( Collectors.toList() )
            : Lists.arrayList();
    }

    public void setKeys( final String[] keys )
    {
        this.keys = keys;
    }
}
