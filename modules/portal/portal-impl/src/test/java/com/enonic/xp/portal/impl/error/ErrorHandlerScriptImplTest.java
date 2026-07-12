package com.enonic.xp.portal.impl.error;

import org.junit.jupiter.api.Test;

import com.enonic.xp.trace.TestTrace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerScriptImplTest
    extends AbstractErrorHandlerTest
{
    @Test
    void testExecute_recordsScriptTraceAttribute()
    {
        // outside OSGi the @Traced wrapper is inert; a manually bound trace exercises the attribute enrichment code
        final TestTrace trace = TestTrace.of( "errorScript" );
        Tracer.trace( trace, () -> execute( "myapplication:/error/error.js", HttpStatus.INTERNAL_SERVER_ERROR ) );

        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, this.portalResponse.getStatus() );
        assertEquals( "Generic error...", this.portalResponse.getBody() );
        assertEquals( "myapplication:/error/error.js", trace.get( "script" ) );
    }
}
