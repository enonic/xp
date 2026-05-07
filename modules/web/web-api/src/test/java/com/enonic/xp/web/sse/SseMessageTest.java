package com.enonic.xp.web.sse;

import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SseMessageTest
{
    @Test
    void data_only()
    {
        assertThat( writeTo( SseMessage.create().data( "hello" ).build() ) ).isEqualTo( "data:hello\n\n" );
    }

    @Test
    void data_multiline_splitsLines()
    {
        assertThat( writeTo( SseMessage.create().data( "line1\nline2" ).build() ) ).isEqualTo( "data:line1\ndata:line2\n\n" );
    }

    @Test
    void fullFrame()
    {
        assertThat( writeTo( SseMessage.create().id( "42" ).event( "update" ).data( "hello" ).build() ) ).isEqualTo(
            "id:42\nevent:update\ndata:hello\n\n" );
    }

    @Test
    void comment_only()
    {
        assertThat( writeTo( SseMessage.create().comment( "keep-alive" ).build() ) ).isEqualTo( ":keep-alive\n\n" );
    }

    @Test
    void comment_multiline_splitsLines()
    {
        assertThat( writeTo( SseMessage.create().comment( "line1\nline2" ).build() ) ).isEqualTo( ":line1\n:line2\n\n" );
    }

    @Test
    void mixedDataAndComment()
    {
        assertThat( writeTo( SseMessage.create().data( "hello" ).comment( "debug" ).data( "world" ).build() ) ).isEqualTo(
            "data:hello\n:debug\ndata:world\n\n" );
    }

    @Test
    void multipleDataCallsAppend()
    {
        assertThat( writeTo( SseMessage.create().data( "one" ).data( "two" ).build() ) ).isEqualTo( "data:one\ndata:two\n\n" );
    }

    @Test
    void toString_omitsTerminatingBlankLine()
    {
        assertThat( SseMessage.create().data( "hello" ).build().toString() ).isEqualTo( "data:hello\n" );
    }

    @Test
    void id_null_throws()
    {
        assertThatThrownBy( () -> SseMessage.create().id( null ) ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void data_null_throws()
    {
        assertThatThrownBy( () -> SseMessage.create().data( null ) ).isInstanceOf( NullPointerException.class );
    }

    private static String writeTo( final SseMessage message )
    {
        final StringWriter sw = new StringWriter();
        try
        {
            message.writeTo( sw );
        }
        catch ( final Exception e )
        {
            throw new AssertionError( e );
        }
        return sw.toString();
    }
}
