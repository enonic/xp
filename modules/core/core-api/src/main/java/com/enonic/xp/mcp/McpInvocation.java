package com.enonic.xp.mcp;

import org.jspecify.annotations.NullMarked;

/**
 * Provides progress reporting for a synchronous MCP tool invocation.
 * The controller receives this helper as the optional third argument to {@code execute(name, args, context)}.
 * Progress is supported when the client supplies a progress token and the invocation is active on the calling thread.
 * Reports made outside that scope, including retained helpers and calls from other threads, are ignored.
 * Supported reports must have finite, nonnegative values, with progress at most the supplied total.
 * Only strictly increasing progress values are emitted; the first report may be zero.
 */
@NullMarked
public interface McpInvocation
{
    /**
     * Checks whether this helper can currently emit progress on the calling thread.
     *
     * @return whether the client supplied a progress token and this invocation is active on the calling thread
     */
    boolean isProgressSupported();

    /**
     * Reports progress with the total and message omitted.
     *
     * @param progress finite, nonnegative completed work
     * @throws IllegalArgumentException if progress reporting is supported and progress is invalid
     */
    void progress( double progress );

    /**
     * Reports progress with a message and the total omitted.
     *
     * @param progress finite, nonnegative completed work
     * @param message progress message
     * @throws IllegalArgumentException if progress reporting is supported and progress is invalid
     */
    void progress( double progress, String message );

    /**
     * Reports progress with a total and the message omitted.
     *
     * @param progress finite, nonnegative completed work, at most total
     * @param total finite, nonnegative total work
     * @throws IllegalArgumentException if progress reporting is supported and either value is invalid
     */
    void progress( double progress, double total );

    /**
     * Reports progress with a total and message.
     *
     * @param progress finite, nonnegative completed work, at most total
     * @param total finite, nonnegative total work
     * @param message progress message
     * @throws IllegalArgumentException if progress reporting is supported and either value is invalid
     */
    void progress( double progress, double total, String message );
}
