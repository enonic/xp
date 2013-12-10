package com.enonic.wem.portal.exception;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import com.enonic.wem.web.mvc.FreeMarkerView;

public final class ExceptionRenderer
{
    private final static int NUM_STACK_ELEMENTS = 14;

    private final static int NUM_SOURCE_LINES = 5;

    public final class ExceptionInfo
    {
        private final String message;

        private final List<String> trace;

        public ExceptionInfo( final Throwable e )
        {
            this.message = e.getMessage();
            this.trace = ExceptionRenderer.getStackTrace( e );
        }

        public String getMessage()
        {
            return this.message;
        }

        public List<String> getTrace()
        {
            return this.trace;
        }
    }

    public final class SourceInfo
    {
        private final Path path;

        private final int line;

        private final int column;

        private final List<String> lines;

        public SourceInfo( final Path path, final int line, final int column )
        {
            this.path = path;
            this.line = line;
            this.column = column;

            final int numLines = Math.max( 0, this.line - NUM_SOURCE_LINES );
            this.lines = readLines( this.path.toFile() ).subList( numLines, this.line );
        }

        public Path getPath()
        {
            return path;
        }

        public int getLine()
        {
            return line;
        }

        public int getColumn()
        {
            return column;
        }

        public int getFromLine()
        {
            return Math.max( 0, this.line - NUM_SOURCE_LINES ) + 1;
        }

        public List<String> getLines()
        {
            return this.lines;
        }
    }

    private final FreeMarkerView view;

    private int status;

    public ExceptionRenderer()
    {
        this.view = FreeMarkerView.template( "portalError.ftl" );
    }

    public ExceptionRenderer status( final int status )
    {
        this.status = status;
        this.view.put( "status", this.status );
        return this;
    }

    public ExceptionRenderer title( final String value )
    {
        this.view.put( "title", value );
        return this;
    }

    public ExceptionRenderer description( final String value )
    {
        this.view.put( "description", value );
        return this;
    }

    public ExceptionRenderer exception( final Throwable e )
    {
        this.view.put( "exception", new ExceptionInfo( e ) );
        return this;
    }

    public ExceptionRenderer source( final Path path, final int line, final int column )
    {
        final SourceInfo info = new SourceInfo( path, line, column );
        this.view.put( "source", info );
        return this;
    }

    public Response render()
    {
        return Response.status( this.status ).entity( view ).type( MediaType.TEXT_HTML_TYPE ).build();
    }

    private static List<String> getStackTrace( final Throwable e )
    {
        List<String> list = Lists.newArrayList();
        for ( final StackTraceElement item : e.getStackTrace() )
        {
            list.add( item.toString() );
        }

        if ( list.size() > NUM_STACK_ELEMENTS )
        {
            list = list.subList( 0, NUM_STACK_ELEMENTS );
            list.add( "..." );
        }

        return list;
    }


    private static List<String> readLines( final File file )
    {
        if ( !file.isFile() )
        {
            return Lists.newArrayList();
        }

        try
        {
            return Files.readLines( file, Charsets.UTF_8 );
        }
        catch ( final Exception e )
        {
            return Lists.newArrayList();
        }
    }
}
