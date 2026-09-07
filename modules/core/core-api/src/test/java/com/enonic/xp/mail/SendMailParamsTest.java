package com.enonic.xp.mail;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SendMailParamsTest
{
    @Test
    void headerValueWithLineBreakRejected()
    {
        final SendMailParams.Builder builder = SendMailParams.create();

        assertThrows( IllegalArgumentException.class, () -> builder.addHeader( "X-Ref", "1\r\nBcc: attacker@example.com" ) );
        assertThrows( IllegalArgumentException.class, () -> builder.addHeader( "X-Ref", "1\nBcc: attacker@example.com" ) );
        assertThrows( IllegalArgumentException.class, () -> builder.addHeader( "X-Ref", "1\rBcc: attacker@example.com" ) );
    }

    @Test
    void headerNameWithLineBreakRejected()
    {
        final SendMailParams.Builder builder = SendMailParams.create();

        assertThrows( IllegalArgumentException.class, () -> builder.addHeader( "X-Ref\r\nBcc", "attacker@example.com" ) );
    }

    @Test
    void contentTypeWithLineBreakRejected()
    {
        final SendMailParams.Builder builder = SendMailParams.create();

        assertThrows( IllegalArgumentException.class, () -> builder.contentType( "text/plain\r\nBcc: attacker@example.com" ) );
    }

    @Test
    void singleLineValuesAccepted()
    {
        final SendMailParams params = SendMailParams.create()
            .to( "to@example.com" )
            .from( "from@example.com" )
            .contentType( "text/html" )
            .addHeader( "X-Ref", "1" )
            .build();

        assertEquals( "text/html", params.getContentType() );
        assertEquals( "X-Ref", params.getHeaders().get( 0 ).getKey() );
        assertEquals( "1", params.getHeaders().get( 0 ).getValue() );
    }
}
