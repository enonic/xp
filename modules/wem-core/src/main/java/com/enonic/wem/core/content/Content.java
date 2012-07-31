package com.enonic.wem.core.content;

import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.EntryPath;
import com.enonic.wem.core.content.type.ContentType;

public class Content
{
    private ContentType type;

    private ContentData data;

    public void init()
    {
        if ( type == null )
        {
            data = new ContentData();
        }
        else
        {
            data = new ContentData( type.getConfigItems() );
        }
    }

    public ContentType getType()
    {
        return type;
    }

    public void setType( final ContentType type )
    {
        this.type = type;
    }

    public ContentData getData()
    {
        return data;
    }

    public void setValue( final String path, final Object value )
    {
        this.data.setValue( new EntryPath( path ), value );
    }

    public void setValue( final String path, final String value )
    {
        this.data.setValue( new EntryPath( path ), value );
    }
}
