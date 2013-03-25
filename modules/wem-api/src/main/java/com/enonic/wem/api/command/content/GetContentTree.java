package com.enonic.wem.api.command.content;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.support.tree.Tree;

public final class GetContentTree
    extends Command<Tree<Content>>
{

    ContentSelectors<ContentId> contentSelectors;

    @Override
    public void validate()
    {
    }

    public ContentSelectors<ContentId> getContentSelectors()
    {
        return contentSelectors;
    }


    public GetContentTree selectors( final ContentSelectors<ContentId> contentSelectors )
    {
        this.contentSelectors = contentSelectors;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final GetContentTree that = (GetContentTree) o;

        if ( contentSelectors != null ? !contentSelectors.equals( that.contentSelectors ) : that.contentSelectors != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return contentSelectors != null ? contentSelectors.hashCode() : 0;
    }
}
