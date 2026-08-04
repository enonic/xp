package com.enonic.xp.admin.impl.portal.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.admin.extension.AdminExtensionDescriptorService;
import com.enonic.xp.admin.extension.AdminExtensionResponseProcessor;
import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.security.PrincipalKeys;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminExtensionResponseProcessorExecutorTest
{
    private AdminExtensionDescriptorService descriptorService;

    private AdminExtensionResponseProcessorExecutor executor;

    private AdminToolDescriptor tool;

    private PortalRequest request;

    private PortalResponse response;

    @BeforeEach
    void setUp()
    {
        this.descriptorService = mock( AdminExtensionDescriptorService.class );
        this.executor = new AdminExtensionResponseProcessorExecutor( this.descriptorService );
        this.tool = AdminToolDescriptor.create().key( DescriptorKey.from( "toolapp:tool" ) ).interfaces( "admin.dashboard" ).build();
        this.request = new PortalRequest();
        this.response = PortalResponse.create().build();
    }

    @Test
    void executesProcessorOfMountedExtension()
    {
        final PortalResponse processed = PortalResponse.create().body( "processed" ).build();
        addProcessor( "app:ext", ( req, res ) -> processed );
        mockDescriptor( "app:ext", Set.of( "admin.dashboard" ), true );

        assertSame( processed, this.executor.execute( this.tool, this.request, this.response ) );
    }

    @Test
    void genericExtensionRunsForAnyTool()
    {
        final PortalResponse processed = PortalResponse.create().body( "processed" ).build();
        addProcessor( "app:ext", ( req, res ) -> processed );
        mockDescriptor( "app:ext", Set.of( AdminExtensionDescriptor.GENERIC_INTERFACE ), true );

        assertSame( processed, this.executor.execute( this.tool, this.request, this.response ) );
    }

    @Test
    void skipsExtensionNotMountedToTool()
    {
        addProcessor( "app:ext", ( req, res ) -> PortalResponse.create().build() );
        mockDescriptor( "app:ext", Set.of( "other.interface" ), true );

        assertSame( this.response, this.executor.execute( this.tool, this.request, this.response ) );
    }

    @Test
    void skipsExtensionWithoutDescriptor()
    {
        addProcessor( "app:ext", ( req, res ) -> PortalResponse.create().build() );
        when( this.descriptorService.getByKey( eq( DescriptorKey.from( "app:ext" ) ) ) ).thenReturn( null );

        assertSame( this.response, this.executor.execute( this.tool, this.request, this.response ) );
    }

    @Test
    void skipsExtensionWhenAccessDenied()
    {
        addProcessor( "app:ext", ( req, res ) -> PortalResponse.create().build() );
        mockDescriptor( "app:ext", Set.of( "admin.dashboard" ), false );

        assertSame( this.response, this.executor.execute( this.tool, this.request, this.response ) );
    }

    @Test
    void executesProcessorsOrderedByExtensionKey()
    {
        final List<String> invocations = new ArrayList<>();
        addProcessor( "appb:ext", ( req, res ) -> {
            invocations.add( "appb:ext" );
            return res;
        } );
        addProcessor( "appa:ext", ( req, res ) -> {
            invocations.add( "appa:ext" );
            return res;
        } );
        mockDescriptor( "appa:ext", Set.of( "admin.dashboard" ), true );
        mockDescriptor( "appb:ext", Set.of( "admin.dashboard" ), true );

        assertSame( this.response, this.executor.execute( this.tool, this.request, this.response ) );
        assertEquals( List.of( "appa:ext", "appb:ext" ), invocations );
    }

    @Test
    void removedProcessorDoesNotRun()
    {
        final AdminExtensionResponseProcessor processor = ( req, res ) -> PortalResponse.create().build();
        addProcessor( "app:ext", processor );
        this.executor.removeProcessor( processor );
        mockDescriptor( "app:ext", Set.of( "admin.dashboard" ), true );

        assertSame( this.response, this.executor.execute( this.tool, this.request, this.response ) );
    }

    @Test
    void failsWhenProcessorReturnsNull()
    {
        addProcessor( "app:ext", ( req, res ) -> null );
        mockDescriptor( "app:ext", Set.of( "admin.dashboard" ), true );

        assertThatThrownBy( () -> this.executor.execute( this.tool, this.request, this.response ) ).isInstanceOf(
            NullPointerException.class ).hasMessage( "Response processor for extension [app:ext] returned null" );
    }

    @Test
    void failsToRegisterProcessorWithoutKeyProperty()
    {
        assertThatThrownBy( () -> this.executor.addProcessor( ( req, res ) -> res, Map.of() ) ).isInstanceOf(
            NullPointerException.class ).hasMessage( "DescriptorKey cannot be null" );
    }

    private void addProcessor( final String extensionKey, final AdminExtensionResponseProcessor processor )
    {
        this.executor.addProcessor( processor, Map.of( "key", extensionKey ) );
    }

    private void mockDescriptor( final String key, final Set<String> interfaces, final boolean accessAllowed )
    {
        final AdminExtensionDescriptor descriptor = mock( AdminExtensionDescriptor.class );
        when( descriptor.getInterfaces() ).thenReturn( interfaces );
        when( descriptor.hasInterface( any( String.class ) ) ).thenAnswer( inv -> interfaces.contains( inv.<String>getArgument( 0 ) ) );
        when( descriptor.isAccessAllowed( any( PrincipalKeys.class ) ) ).thenReturn( accessAllowed );
        when( this.descriptorService.getByKey( eq( DescriptorKey.from( key ) ) ) ).thenReturn( descriptor );
    }
}
