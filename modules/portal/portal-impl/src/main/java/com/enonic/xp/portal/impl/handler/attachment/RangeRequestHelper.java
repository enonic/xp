package com.enonic.xp.portal.impl.handler.attachment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public final class RangeRequestHelper
{
    private static final byte[] CRLF = {13, 10};

    private static final String MULTIPART_BOUNDARY = "__BOUNDARY__";

    public void handleRangeRequest( final WebRequest request, final WebResponse.Builder response, final ByteSource body,
                                    final MediaType contentType )
        throws IOException
    {
        response.header( "Accept-Ranges", "bytes" );
        if ( !request.getHeaders().containsKey( "Range" ) )
        {
            return;
        }

        final String rangeHeader = request.getHeaders().getOrDefault( "Range", "" ).trim();
        final String rangeValue = rangeHeader.length() > "bytes=".length() ? rangeHeader.substring( "bytes=".length() ) : "";

        long fileLength = body.size();
        final List<Range> ranges = parseRangeBytesHeader( rangeValue, fileLength );
        if ( ranges.isEmpty() )
        {
            response.status( HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE );
            response.header( "Content-Range", "bytes */" + fileLength );
            response.body( null );
            return;
        }

        response.status( HttpStatus.PARTIAL_CONTENT );
        if ( ranges.size() == 1 )
        {
            final Range range = ranges.get( 0 );
            response.header( "Content-Length", Long.toString( range.length ) );
            response.header( "Content-Range", "bytes " + range.start + "-" + range.end + "/" + fileLength );
            response.body( body.slice( range.start, range.length ) );
        }
        else
        {
            writeMultipart( response, ranges, fileLength, body, contentType );
        }
    }

    private List<Range> parseRangeBytesHeader( final String rangeHeaderValue, final long fileLength )
    {
        final String[] rangeValues = rangeHeaderValue.split( "," );
        try
        {
            return Arrays.stream( rangeValues ).
                map( ( rangeValue ) -> parseRangeBytes( rangeValue, fileLength ) ).
                filter( Objects::nonNull ).
                collect( Collectors.toList() );
        }
        catch ( NumberFormatException e )
        {
            return Collections.emptyList();
        }
    }

    private Range parseRangeBytes( final String rangeValue, final long fileLength )
    {
        final long start;
        long end;
        if ( rangeValue.startsWith( "-" ) )
        {
            end = fileLength - 1;
            start = fileLength - 1 - Long.parseLong( rangeValue.substring( "-".length() ) );
        }
        else
        {
            String[] range = rangeValue.split( "-" );
            start = Long.parseLong( range[0] );
            end = range.length > 1 ? Long.parseLong( range[1] ) : fileLength - 1;
        }
        if ( end > fileLength - 1 )
        {
            end = fileLength - 1;
        }

        return start <= end ? new Range( start, end ) : null;
    }

    private void writeMultipart( final WebResponse.Builder response, final List<Range> ranges, final long fileLength, final ByteSource body,
                                 final MediaType contentType )
        throws IOException
    {
        final String boundary = MULTIPART_BOUNDARY + UUID.randomUUID().toString().replace( "-", "" );
        response.contentType( MediaType.parse( "multipart/byteranges; boundary=" + boundary ) );

        final List<ByteSource> responseParts = new ArrayList<>();
        for ( final Range range : ranges )
        {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            writeLine( output, "" );
            writeLine( output, "--" + boundary );
            writeLine( output, "Content-Type: " + contentType.toString() );
            writeLine( output, "Content-Range: bytes " + range.start + "-" + range.end + "/" + fileLength );
            writeLine( output, "" );

            final ByteSource partHeaders = ByteSource.wrap( output.toByteArray() );
            final ByteSource partBody = body.slice( range.start, range.length );
            responseParts.add( partHeaders );
            responseParts.add( partBody );
        }

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        writeLine( output, "" );
        writeLine( output, "--" + boundary + "--" );
        final ByteSource multipartEnd = ByteSource.wrap( output.toByteArray() );
        responseParts.add( multipartEnd );

        response.body( ByteSource.concat( responseParts ) );
    }

    private void writeLine( final ByteArrayOutputStream output, final String string )
        throws IOException
    {
        output.write( string.getBytes( StandardCharsets.UTF_8 ) );
        output.write( CRLF );
    }

}
