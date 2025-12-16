package com.enonic.xp.web.jetty.impl;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LastResortErrorHandlerTest
{
    @Test
    void writeErrorPage()
        throws Exception
    {
        final StringWriter writer = new StringWriter();
        new LastResortErrorHandler().writeErrorHtml( null, writer, StandardCharsets.UTF_8, 400, "message ignored", null );
        assertEquals( "<!DOCTYPE html>\n<html>\n<head>\n<title>400 - Bad Request</title>\n</head>\n<body>400 - Bad Request</body>\n</html>",
                      writer.toString() );
    }
}
