package com.enonic.wem.api.content;


import com.enonic.wem.api.Name;

public class ContentName
    extends Name
{
    public ContentName( final String name )
    {
        super( name );
    }

    public static ContentName from( String str )
    {
        return new ContentName( str );
    }
}
