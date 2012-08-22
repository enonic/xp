package com.enonic.wem.core.content;

import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.EntryPath;
import com.enonic.wem.core.content.type.ContentType;

public class Content
{
    private ContentType type;

    private String name;

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

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public ContentData getData()
    {
        return data;
    }

    public void setData( final String path, final Object value )
    {
        this.data.setData( new EntryPath( path ), value );
    }

    public void setData( final String path, final String value )
    {
        this.data.setData( new EntryPath( path ), value );
    }

    public String getValueAsString( final String path )
    {
        return this.data.getValueAsString( new EntryPath( path ) );
    }

    public void checkBreaksRequiredContract()
    {
        this.data.breaksRequiredContract();
    }
}
