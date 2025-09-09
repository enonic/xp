package com.enonic.xp.lib.content;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class UnpublishContentHandler
    implements ScriptBean
{
    private String[] keys;

    private ContentService contentService;

    public List<String> execute()
    {
        final Context context = ContextBuilder.from( ContextAccessor.current() ).branch( ContentConstants.BRANCH_MASTER ).build();
        final ContentIds contentIds = context.callWith( this::resolveContentIds );

        final UnpublishContentParams unpublishContentParams = UnpublishContentParams.create().
            contentIds( contentIds ).
            build();

        final UnpublishContentsResult result = this.contentService.unpublish( unpublishContentParams );
        return result.getUnpublishedContents().stream().map( ContentId::toString ).collect( Collectors.toList() );
    }

    private ContentIds resolveContentIds()
    {
        ContentIds.Builder contentIds = ContentIds.create();
        for ( final String key : this.keys )
        {
            if ( key.startsWith( "/" ) )
            {
                final ContentPath path = ContentPath.from( key );
                final Content content = getByPath( path );
                if ( content != null )
                {
                    contentIds.add( content.getId() );
                }
            }
            else
            {
                contentIds.add( ContentId.from( key ) );
            }
        }
        return contentIds.build();
    }

    private Content getByPath( final ContentPath contentPath )
    {
        try
        {
            return this.contentService.getByPath( contentPath );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    public void setKeys( final String[] keys )
    {
        this.keys = keys;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
    }
}
