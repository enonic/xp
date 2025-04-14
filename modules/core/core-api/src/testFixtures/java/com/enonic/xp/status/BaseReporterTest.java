package com.enonic.xp.status;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import com.enonic.xp.support.JsonTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseReporterTest<T extends StatusReporter>
{
    protected final JsonTestHelper helper;

    private final String name;

    private final MediaType mediaType;

    protected T reporter;

    protected Map<String, String> params;

    public BaseReporterTest( final String name, final MediaType mediaType )
    {
        this.name = name;
        this.mediaType = mediaType;
        this.helper = new JsonTestHelper( this );
    }

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.reporter = newReporter();
        this.params = new HashMap<>();
    }

    protected abstract T newReporter()
        throws Exception;

    @Test
    public final void testName()
    {
        assertEquals( this.name, this.reporter.getName() );
    }

    @Test
    public final void testMediaType()
    {
        assertEquals( this.mediaType, this.reporter.getMediaType() );
    }

    protected final String textReport()
        throws Exception
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        this.reporter.report( new StatusContext()
        {
            @Override
            public Optional<String> getParameter( final String name )
            {
                return Optional.ofNullable( params.get( name ) );
            }

            @Override
            public OutputStream getOutputStream()
            {
                return out;
            }
        } );

        return out.toString();
    }

    protected final JsonNode jsonReport()
        throws Exception
    {
        return this.helper.stringToJson( textReport() );
    }
}
