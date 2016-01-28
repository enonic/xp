package com.enonic.xp.lib.content;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.lib.content.mapper.PushContentResultMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class PublishContentHandler
    implements ScriptBean
{
    private String[] keys;

    private String targetBranch;

    private String sourceBranch;

    private Boolean includeChildren;

    private Boolean includeDependencies;

    private ContentService contentService;

    public PushContentResultMapper execute()
    {
        final Context context = ContextBuilder.
            from( ContextAccessor.current() ).
            branch( this.sourceBranch ).
            build();

        return context.callWith( this::publishContent );
    }

    private PushContentResultMapper publishContent()
    {
        for ( int cnt = 0; cnt < this.keys.length; cnt++ )
        {
            if ( keys[cnt].startsWith( "/" ) )
            {
                final Content content = this.contentService.getByPath( ContentPath.from( keys[cnt] ) );
                keys[cnt] = content.getId().toString();
            }
        }

        final PushContentParams.Builder builder = PushContentParams.create();
        builder.contentIds( ContentIds.from( keys ) );
        builder.target( Branch.from( targetBranch ) );
        if ( this.includeChildren != null )
        {
            builder.includeChildren( this.includeChildren );
        }
        if ( this.includeDependencies != null )
        {
            builder.includeDependencies( includeDependencies );
        }
        final PushContentsResult result = this.contentService.push( builder.build() );
        return result != null ? new PushContentResultMapper( result ) : null;
    }

    public void setKeys( final String[] keys )
    {
        this.keys = keys;
    }

    public void setTargetBranch( final String targetBranch )
    {
        this.targetBranch = targetBranch;
    }

    public void setSourceBranch( final String sourceBranch )
    {
        this.sourceBranch = sourceBranch;
    }

    public void setIncludeChildren( final Boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }

    public void setIncludeDependencies( final Boolean includeDependencies )
    {
        this.includeDependencies = includeDependencies;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
    }
}
