package com.enonic.xp.web.jetty.impl;

import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LastResortErrorHandlerTest
{
    @Test
    void writeErrorPage()
        throws Exception
    {
        final StringWriter writer = new StringWriter();
        new LastResortErrorHandler().writeErrorPage( null, writer, 400, "message ignored", true );
        assertEquals( "<!DOCTYPE html>\n<html>\n<head>\n<title>400 - Bad Request</title>\n</head>\n<body>400 - Bad Request</body>\n</html>",
                      writer.toString() );
    }
}