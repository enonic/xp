package com.enonic.xp.lib.content;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.lib.content.mapper.PublishContentResultMapper;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class PublishContentHandler
    implements ScriptBean
{
    private String[] keys;

    private Map<String, Object> contentPublishInfo;

    private String[] excludeDescendantsOf;

    private Boolean includeDependencies;

    private ContentService contentService;

    private String message;

    public PublishContentResultMapper execute()
    {
        final Context context = ContextBuilder.from( ContextAccessor.current() ).branch( ContentConstants.BRANCH_DRAFT ).build();

        return context.callWith( this::publishContent );
    }

    private PublishContentResultMapper publishContent()
    {
        final List<ContentPath> contentNotFound = new ArrayList<>();
        final ContentIds.Builder contentIds = ContentIds.create();

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
                else
                {
                    contentNotFound.add( path );
                }
            }
            else
            {
                contentIds.add( ContentId.from( key ) );
            }
        }

        final PushContentParams.Builder builder = PushContentParams.create();
        builder.contentIds( contentIds.build() );
        if ( this.contentPublishInfo != null )
        {
            final Object from = this.contentPublishInfo.get( "from" );
            final Object to = this.contentPublishInfo.get( "to" );
            final ContentPublishInfo contentPublishInfo = ContentPublishInfo.create().
                from( from == null ? null : Instant.parse( (String) from ) ).
                to( to == null ? null : Instant.parse( (String) to ) ).
                build();
            builder.contentPublishInfo( contentPublishInfo );
        }
        if ( this.excludeDescendantsOf != null )
        {
            builder.excludeDescendantsOf( ContentIds.from( this.excludeDescendantsOf ) );
        }
        if ( this.includeDependencies != null )
        {
            builder.includeDependencies( includeDependencies );
        }
        builder.message( message );

        final PublishContentResult result = this.contentService.publish( builder.build() );
        return new PublishContentResultMapper( result, contentNotFound );
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

    public void setExcludeDescendantsOf( final String[] excludeDescendantsOf )
    {
        this.excludeDescendantsOf = excludeDescendantsOf;
    }

    public void setIncludeDependencies( final Boolean includeDependencies )
    {
        this.includeDependencies = includeDependencies;
    }

    public void setContentPublishInfo( final ScriptValue contentPublishInfo )
    {
        this.contentPublishInfo = contentPublishInfo != null ? contentPublishInfo.getMap() : null;
    }

    public void setMessage( final String message )
    {
        this.message = message;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
    }
}
