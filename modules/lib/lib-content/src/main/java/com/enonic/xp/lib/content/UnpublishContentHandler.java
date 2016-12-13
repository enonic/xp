package com.enonic.xp.lib.content;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
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

    private boolean clearPublishInfo;

    public List<String> execute()
    {
        final Branch targetBranch = ContentConstants.BRANCH_MASTER;

        final Context context = ContextBuilder.
            from( ContextAccessor.current() ).
            branch( targetBranch ).
            build();
        final List<ContentId> contentIds = context.callWith( this::resolveContentIds );

        final UnpublishContentParams unpublishContentParams = UnpublishContentParams.create().
            contentIds( ContentIds.from( contentIds ) ).
            unpublishBranch( targetBranch ).
            clearPublishInfo( clearPublishInfo ).
            includeChildren( true ).
            build();

        final UnpublishContentsResult result = this.contentService.unpublishContent( unpublishContentParams );
        return result.getUnpublishedContents().stream().map( ContentId::toString ).collect( Collectors.toList() );
    }

    private List<ContentId> resolveContentIds()
    {
        final List<ContentId> contentIds = new ArrayList<>();
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
        return contentIds;
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

    public void setClearPublishInfo( final boolean clearPublishInfo )
    {
        this.clearPublishInfo = clearPublishInfo;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
    }
}
