package com.enonic.xp.web;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WebResponseTest
{
    @Test
    void header_same_name_overrides_previous_value()
    {
        final WebResponse webResponse = WebResponse.create().header( "a", "b" ).header( "a", "c" ).build();
        assertEquals( Map.of( "a", "c" ), webResponse.getHeaders() );
    }

    @Test
    void headers_same_name_overrides_previous_values()
    {
        final WebResponse.Builder<?> builder = WebResponse.create();
        builder.headers( Map.of( "a", "av", "b", "bv" ) );

        final WebResponse webResponse = builder.headers( Map.of( "a", "av-new", "b", "bv-new" ) ).build();
        assertEquals( Map.of( "a", "av-new", "b", "bv-new" ), webResponse.getHeaders() );
    }

    @Test
    void headers_case_insensitive()
    {
        final WebResponse webResponse = WebResponse.create().headers( Map.of( "A", "av", "B", "bv" ) ).build();

        final Map<String, String> headers = webResponse.getHeaders();
        assertAll( () -> assertEquals( Map.of( "a", "av", "B", "bv" ), headers ), () -> assertEquals( "av", headers.get( "A" ) ),
                   () -> assertEquals( "bv", headers.get( "b" ) ) );
    }
}
