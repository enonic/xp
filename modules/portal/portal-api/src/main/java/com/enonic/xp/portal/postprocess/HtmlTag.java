package com.enonic.xp.portal.postprocess;

import java.util.HashMap;
import java.util.Map;

public enum HtmlTag
{
    HEAD_BEGIN( "headBegin" ),
    HEAD_END( "headEnd" ),
    BODY_BEGIN( "bodyBegin" ),
    BODY_END( "bodyEnd" );

    private static final Map<String, HtmlTag> LOOKUP_TABLE = new HashMap<>();

    static
    {
        for ( final HtmlTag htmlTag : HtmlTag.values() )
        {
            LOOKUP_TABLE.put( htmlTag.id, htmlTag );
        }
    }

    private final String id;

    HtmlTag( final String id )
    {
        this.id = id;
    }

    public String id()
    {
        return id;
    }

    public static HtmlTag from( final String id )
    {
        return LOOKUP_TABLE.get( id );
    }
}
