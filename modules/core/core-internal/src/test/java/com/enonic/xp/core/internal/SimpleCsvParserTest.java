package com.enonic.xp.core.internal;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleCsvParserTest
{
    @Test
    void parseLine()
    {
        assertEquals( List.of( "a", "b", "c" ), SimpleCsvParser.parseLine( "a,b,c" ) );
        assertEquals( List.of( "Cache-Control: no-store, private", " X-Instance: \"jupiter\"" ),
                      SimpleCsvParser.parseLine( "\"Cache-Control: no-store, private\", \"X-Instance: \"\"jupiter\"\"\"" ) );
    }
}
