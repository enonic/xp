package com.enonic.xp.lib.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ListContentsByParentParams;
import com.enonic.xp.content.ListContentsByParentResult;
import com.enonic.xp.lib.content.mapper.ContentListResultMapper;

@SuppressWarnings("unused")
public final class ListContentHandler
    extends BaseContextHandler
{
    private String parent;

    private boolean recursive;

    @Override
    protected Object doExecute()
    {
        final ListContentsByParentParams.Builder params = ListContentsByParentParams.create().recursive( recursive );

        if ( parent.startsWith( "/" ) )
        {
            params.parentPath( ContentPath.from( parent ) );
        }
        else
        {
            params.parentId( ContentId.from( parent ) );
        }

        final ListContentsByParentResult result = contentService.list( params.build() );

        return new ContentListResultMapper( result );
    }

    public void setParent( final String parent )
    {
        this.parent = parent;
    }

    public void setRecursive( final boolean recursive )
    {
        this.recursive = recursive;
    }
}
