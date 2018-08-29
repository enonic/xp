package com.enonic.xp.admin.impl.json.content;

import java.util.Objects;

import com.enonic.xp.content.ContentId;

public class ContentIdJson
{
    private final String id;

    public ContentIdJson( final ContentId contentId )
    {
        this.id = contentId.toString();
    }

    public String getId()
    {
        return id;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentIdJson ) )
        {
            return false;
        }

        final ContentIdJson that = (ContentIdJson) o;

        if ( !id.equals( that.id ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id );
    }

}
