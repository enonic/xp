package com.enonic.wem.api.content;

import com.enonic.wem.api.workspace.Workspace;

public class PushContentParams
{

    private final ContentId contentId;

    private final Workspace target;

    public PushContentParams( final Workspace target, final ContentId contentId )
    {
        this.target = target;
        this.contentId = contentId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Workspace getTarget()
    {
        return target;
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

        final PushContentParams that = (PushContentParams) o;

        if ( contentId != null ? !contentId.equals( that.contentId ) : that.contentId != null )
        {
            return false;
        }
        if ( target != null ? !target.equals( that.target ) : that.target != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = contentId != null ? contentId.hashCode() : 0;
        result = 31 * result + ( target != null ? target.hashCode() : 0 );
        return result;
    }
}
