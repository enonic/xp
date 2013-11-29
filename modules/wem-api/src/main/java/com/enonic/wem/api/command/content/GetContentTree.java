package com.enonic.wem.api.command.content;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.support.tree.Tree;

public final class GetContentTree
    extends Command<Tree<Content>>
{
    private ContentId contentId;

    @Override
    public void validate()
    {
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public GetContentTree contentId( final ContentId contentId )
    {
        this.contentId = contentId;
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

        if ( contentId != null ? !contentId.equals( that.contentId ) : that.contentId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return contentId != null ? contentId.hashCode() : 0;
    }
}
