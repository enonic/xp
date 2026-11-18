package com.enonic.xp.mcp.impl;

import java.math.BigDecimal;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.mcp.McpInvocation;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;

import io.modelcontextprotocol.modern.McpException;
import io.modelcontextprotocol.modern.McpSchema.CallToolOutcome;
import io.modelcontextprotocol.modern.McpSchema.CallToolResult;
import io.modelcontextprotocol.modern.server.McpRequestContext;
import io.modelcontextprotocol.modern.server.McpSyncNotifier;
import io.modelcontextprotocol.modern.server.McpSyncResponse;

@NullMarked
public final class McpInvocationScope
{
    private static final ScopedValue<McpInvocation> CURRENT = ScopedValue.newInstance();

    private static final McpInvocation INACTIVE = new Invocation( null );

    private McpInvocationScope()
    {
    }

    public static McpInvocation current()
    {
        return CURRENT.orElse( INACTIVE );
    }

    public static McpSyncResponse<CallToolOutcome> respond( final McpRequestContext context, final Supplier<CallToolResult> action )
    {
        final var token = context.progressToken();
        if ( token == null )
        {
            return McpSyncResponse.result( ScopedValue.where( CURRENT, INACTIVE ).call( action::get ) );
        }
        if ( !( token instanceof String ) && !( token instanceof Number number && isInteger( number ) ) )
        {
            throw McpException.invalidParams( "MCP progressToken must be a string or integer" );
        }
        final var capturedContext = ContextBuilder.copyOf( ContextAccessor.current() ).build();
        final var capturedRequest = copyRequest( PortalRequestAccessor.get() );
        return McpSyncResponse.streaming( notifier -> capturedContext.callWith( () -> {
            final var invocation = new Invocation( notifier );
            try
            {
                return PortalRequestAccessor.callWith( capturedRequest,
                                                       () -> ScopedValue.where( CURRENT, invocation ).call( action::get ) );
            }
            finally
            {
                invocation.active = false;
            }
        } ) );
    }

    private static boolean isInteger( final Number number )
    {
        try
        {
            return new BigDecimal( number.toString() ).stripTrailingZeros().scale() <= 0;
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
    }

    private static PortalRequest copyRequest( final @Nullable PortalRequest source )
    {
        if ( source == null )
        {
            return new PortalRequest();
        }
        final var copy = new PortalRequest( source );
        copy.setApplicationKey( source.getApplicationKey() );
        copy.setMode( source.getMode() );
        copy.setRepositoryId( source.getRepositoryId() );
        copy.setBranch( source.getBranch() );
        copy.setContentPath( source.getContentPath() );
        copy.setBaseUri( source.getBaseUri() );
        copy.setContextPath( source.getContextPath() );
        copy.setSite( source.getSite() );
        copy.setContent( source.getContent() );
        copy.setProject( source.getProject() );
        copy.setComponent( source.getComponent() );
        copy.setPageDescriptor( source.getPageDescriptor() );
        copy.setControllerScript( source.getControllerScript() );
        copy.setValidTicket( source.isValidTicket() );
        return copy;
    }

    private static final class Invocation
        implements McpInvocation
    {
        private final @Nullable McpSyncNotifier notifier;

        private final Thread owner = Thread.currentThread();

        private boolean active = true;

        private double previous = -1;

        private Invocation( final @Nullable McpSyncNotifier notifier )
        {
            this.notifier = notifier;
        }

        @Override
        public boolean isProgressSupported()
        {
            return Thread.currentThread() == owner && active && notifier != null && CURRENT.orElse( INACTIVE ) == this;
        }

        @Override
        public void progress( final double progress )
        {
            report( progress, null, null );
        }

        @Override
        public void progress( final double progress, final String message )
        {
            report( progress, null, message );
        }

        @Override
        public void progress( final double progress, final double total )
        {
            report( progress, total, null );
        }

        @Override
        public void progress( final double progress, final double total, final String message )
        {
            report( progress, total, message );
        }

        private void report( final double progress, final @Nullable Double total, final @Nullable String message )
        {
            if ( !isProgressSupported() )
            {
                return;
            }
            if ( !Double.isFinite( progress ) || progress < 0 ||
                total != null && ( !Double.isFinite( total ) || total < 0 || progress > total ) )
            {
                throw new IllegalArgumentException( "MCP progress and total must be finite and nonnegative, with progress at most total" );
            }
            if ( progress > previous )
            {
                previous = progress;
                notifier.progress( progress, total, message );
            }
        }
    }
}
