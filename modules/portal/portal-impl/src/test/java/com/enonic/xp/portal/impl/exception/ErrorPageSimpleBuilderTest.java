package com.enonic.xp.portal.impl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorPageSimpleBuilderTest
{

    @Test
    void build()
    {
        String expected = "<!DOCTYPE html><html>" + "<head><title>1 - test &gt; title</title>" + "<style>html, body { height: 100%; } " +
            "body { font-family: Arial, Helvetica, sans-serif; margin: 0; display: flex; flex-direction: column; justify-content: center; align-items: center; color: lightgray; } " +
            "h1 { font-size: 3em; margin: 0; } " + "h3 { font-size: 1.5em; }</style></head>" +
            "<body><h1>D&#39;oh!</h1><h3>1 - test &gt; title</h3></body></html>";
        assertEquals( expected, new ErrorPageSimpleBuilder().status( 1 ).title( "test > title" ).build() );
    }
}