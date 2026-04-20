package com.enonic.xp.web.sse;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

/**
 * A Server-Sent Events message — a collection of pre-formatted lines terminated by a blank line when written.
 *
 * <p>Build with {@link #create()}. The builder exposes {@code id}, {@code event}, {@code data}, and
 * {@code comment} which can each be called multiple times to append to the frame.</p>
 */
@NullMarked
public final class SseMessage
{
    private final List<String> lines;

    private SseMessage( final List<String> lines )
    {
        this.lines = List.copyOf( lines );
    }

    /**
     * Writes the frame to the given writer (each line followed by the terminating blank line). Does not flush.
     */
    public void writeTo( final Writer writer )
        throws IOException
    {
        for ( final String line : lines )
        {
            writer.write( line );
        }
        writer.write( "\n" );
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        for ( final String line : lines )
        {
            sb.append( line );
        }
        return sb.toString();
    }

    /**
     * Starts a new builder.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builder for {@link SseMessage}. Methods may be called in any order and multiple times; each call appends
     * a line to the frame. Per the SSE spec, multiple {@code id}/{@code event} lines in the same frame are
     * overwritten on the client (last one wins); multiple {@code data} and {@code comment} lines are legal and
     * processed as-is.
     */
    public static final class Builder
    {
        private final List<String> lines = new ArrayList<>();

        private Builder()
        {
        }

        /**
         * Appends an {@code id:} line.
         *
         * @throws NullPointerException if {@code id} is {@code null}.
         */
        public Builder id( final String id )
        {
            lines.add( "id:" + requireNonNull( id, "id is required" ) + "\n" );
            return this;
        }

        /**
         * Appends an {@code event:} line which sets the type of the dispatched event on the client. If not
         * called, the client dispatches an untyped {@code message} event.
         *
         * @throws NullPointerException if {@code event} is {@code null}.
         */
        public Builder event( final String event )
        {
            lines.add( "event:" + requireNonNull( event, "event is required" ) + "\n" );
            return this;
        }

        /**
         * Appends one or more {@code data:} lines, one per {@code \n}-separated line in the input. The client
         * joins the data lines with {@code \n} (the trailing one is stripped) when it builds the dispatched
         * event.
         *
         * @throws NullPointerException if {@code data} is {@code null}.
         */
        public Builder data( final String data )
        {
            requireNonNull( data, "data is required" );
            for ( final String line : data.split( "\n", -1 ) )
            {
                lines.add( "data:" + line + "\n" );
            }
            return this;
        }

        /**
         * Appends one or more comment lines, one per {@code \n}-separated line in the input. Each emitted
         * line starts with {@code :} and is ignored by the client — useful for keep-alive pings or embedding
         * debug annotations between real events.
         *
         * @throws NullPointerException if {@code comment} is {@code null}.
         */
        public Builder comment( final String comment )
        {
            requireNonNull( comment, "comment is required" );
            for ( final String line : comment.split( "\n", -1 ) )
            {
                lines.add( ":" + line + "\n" );
            }
            return this;
        }

        /**
         * Returns an immutable {@link SseMessage} ready to be written.
         */
        public SseMessage build()
        {
            return new SseMessage( lines );
        }
    }
}
