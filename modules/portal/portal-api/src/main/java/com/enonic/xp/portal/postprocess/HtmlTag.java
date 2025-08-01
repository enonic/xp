package com.enonic.xp.portal.postprocess;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HtmlTag
{
    HEAD_BEGIN( "headBegin" ),
    HEAD_END( "headEnd" ),
    BODY_BEGIN( "bodyBegin" ),
    BODY_END( "bodyEnd" );

    private static final Map<String, HtmlTag> LOOKUP_TABLE =
        Arrays.stream( values() ).collect( Collectors.toUnmodifiableMap( e -> e.id, Function.identity() ) );

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
