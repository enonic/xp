package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindContentByQueryParamsTest
{

    @Test
    public void testEquals()
    {
        final ContentQuery contentQuery = ContentQuery.create().build();

        FindContentByQueryParams params = FindContentByQueryParams.create().
            populateChildren( true ).contentQuery( contentQuery ).build();

        assertEquals( params.isPopulateChildren(), true );
        assertEquals( params.getContentQuery(), contentQuery );

    }

}
