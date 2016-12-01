package com.enonic.xp.lib.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

import okhttp3.Headers;
import okhttp3.Response;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

import static com.enonic.xp.lib.http.HttpRequestHandler.MAX_IN_MEMORY_BODY_STREAM_BYTES;
import static org.apache.commons.lang.StringUtils.isBlank;

public final class ResponseMapper
    implements MapSerializable
{
    private final static ImmutableSet<String> SKIP_HEADERS =
        ImmutableSet.of( "okhttp-received-millis", "okhttp-selected-protocol", "okhttp-sent-millis" );

    private final static ImmutableList<MediaType> TEXT_CONTENT_TYPES =
        ImmutableList.of( MediaType.ANY_TEXT_TYPE, MediaType.create( "application", "xml" ), MediaType.create( "application", "json" ) );

    private final Response response;

    public ResponseMapper( final Response response )
    {
        this.response = response;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "status", this.response.code() );
        gen.value( "message", this.response.message() );
        final String contentType = this.response.header( "content-type" );

        final boolean isHeadMethod = "HEAD".equalsIgnoreCase( this.response.request().method() );
        final ByteSource bodySource = isHeadMethod ? ByteSource.empty() : getResponseBodyStream();
        final String bodyString = isHeadMethod ? "" : getResponseBodyString( bodySource );

        gen.value( "body", bodyString );
        gen.value( "bodyStream", bodySource );
        gen.value( "contentType", contentType );
        serializeHeaders( "headers", gen, this.response.headers() );
    }

    private Charset getCharset()
    {
        final String contentType = response.header( "content-type" );
        if ( contentType == null )
        {
            return StandardCharsets.UTF_8;
        }
        try
        {
            final MediaType type = MediaType.parse( contentType );
            return type.charset().or( StandardCharsets.UTF_8 );
        }
        catch ( IllegalArgumentException e )
        {
            return StandardCharsets.UTF_8;
        }
    }

    private String getResponseBodyString( final ByteSource source )
    {
        try
        {
            return isTextContent() ? source.asCharSource( getCharset() ).read() : null;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    public ByteSource getResponseBodyStream()
    {
        try
        {
            final long bodyLength = response.body().contentLength();
            if ( bodyLength == -1 || bodyLength > MAX_IN_MEMORY_BODY_STREAM_BYTES )
            {
                final File tempFile = writeAsTmpFile( response.body().byteStream() );
                return Files.asByteSource( tempFile );
            }
            return ByteSource.wrap( this.response.body().bytes() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    private static File writeAsTmpFile( final InputStream inputStream )
        throws IOException
    {
        final File tempFile = File.createTempFile( "xphttp", ".tmp" );
        tempFile.deleteOnExit();
        java.nio.file.Files.copy( inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
        return tempFile;
    }

    private void serializeHeaders( final String name, final MapGenerator gen, final Headers headers )
    {
        gen.map( name );
        for ( final String headerName : headers.names() )
        {
            if ( SKIP_HEADERS.contains( headerName.toLowerCase() ) )
            {
                continue;
            }
            gen.value( headerName, headers.get( headerName ) );
        }
        gen.end();
    }

    private boolean isTextContent()
    {
        final String contentType = this.response.header( "content-type" );
        if ( isBlank( contentType ) )
        {
            return false;
        }

        try
        {
            final MediaType mediaType = MediaType.parse( contentType );
            return TEXT_CONTENT_TYPES.stream().anyMatch( mediaType::is );
        }
        catch ( IllegalArgumentException e )
        {
            return false;
        }
    }

}
