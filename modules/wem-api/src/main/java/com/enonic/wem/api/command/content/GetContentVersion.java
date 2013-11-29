package com.enonic.wem.api.command.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.versioning.ContentVersionId;

public final class GetContentVersion
    extends Command<Content>
{
    private ContentId contentId;

    private ContentPath contentPath;

    private ContentVersionId version;

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public ContentVersionId getVersion()
    {
        return version;
    }

    public GetContentVersion contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public GetContentVersion contentPath( final ContentPath contentPath )
    {
        this.contentPath = contentPath;
        return this;
    }

    public GetContentVersion version( final ContentVersionId version )
    {
        this.version = version;
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

        final GetContentVersion that = (GetContentVersion) o;

        if ( contentId != null ? !contentId.equals( that.contentId ) : that.contentId != null )
        {
            return false;
        }
        if ( contentPath != null ? !contentPath.equals( that.contentPath ) : that.contentPath != null )
        {
            return false;
        }
        if ( version != null ? !version.equals( that.version ) : that.version != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = contentId != null ? contentId.hashCode() : 0;
        result = 31 * result + ( contentPath != null ? contentPath.hashCode() : 0 );
        result = 31 * result + ( version != null ? version.hashCode() : 0 );
        return result;
    }

    @Override
    public void validate()
    {
        if ( this.contentId == null )
        {
            Preconditions.checkNotNull( this.contentPath, "ContentId/ContentPath cannot be null" );
        }
        Preconditions.checkNotNull( this.version, "Content version cannot be null" );
    }
}
