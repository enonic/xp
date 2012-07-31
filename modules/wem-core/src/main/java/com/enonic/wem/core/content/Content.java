package com.enonic.wem.core.content;

import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.EntryPath;
import com.enonic.wem.core.content.type.ContentType;

public class Content
{
    private ContentType type;

    private ContentData data = new ContentData();

    public ContentType getType()
    {
        return type;
    }

    public void setType( final ContentType type )
    {
        this.type = type;
        this.data.setConfigItems( type.getConfigItems() );
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
