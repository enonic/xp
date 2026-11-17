package com.enonic.xp.mcp.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@NullMarked
class McpDescriptorServiceImplTest
{
    private static final ApplicationKey APP = ApplicationKey.from( "myapp" );

    private static final DescriptorKey KEY = DescriptorKey.from( APP, "search" );

    private final ResourceService resourceService = mock( ResourceService.class );

    private final Map<ResourceKey, Resource> resources = new LinkedHashMap<>();

    private final McpDescriptorServiceImpl service = new McpDescriptorServiceImpl( resourceService );

    @BeforeEach
    void setUp()
    {
        when( resourceService.getResource( any( ResourceKey.class ) ) ).thenAnswer( invocation -> {
            final ResourceKey key = invocation.getArgument( 0 );
            return resources.computeIfAbsent( key, missingKey -> {
                final var resource = mock( Resource.class );
                when( resource.getKey() ).thenReturn( missingKey );
                return resource;
            } );
        } );
        when( resourceService.processResource( any() ) ).thenAnswer( invocation -> {
            final ResourceProcessor<?, ?> processor = invocation.getArgument( 0 );
            return processor.process( resourceService.getResource( processor.toResourceKey() ) );
        } );
        when( resourceService.findFiles( any( ApplicationKey.class ), anyString() ) ).thenAnswer( invocation -> {
            final ApplicationKey application = invocation.getArgument( 0 );
            final var pattern = Pattern.compile( invocation.<String>getArgument( 1 ) );
            return resources.entrySet().stream()
                .filter( entry -> entry.getValue().exists() )
                .map( Map.Entry::getKey )
                .filter( key -> key.getApplicationKey().equals( application ) && pattern.matcher( key.getPath() ).matches() )
                .collect( ResourceKeys.collector() );
        } );
    }

    @Test
    void loads_yaml_and_prefers_it_to_yml()
    {
        final var yaml = addResource( "/mcp/search/search.yaml", "tools: [{name: yaml, inputSchema: {}}]" );
        final var yml = addResource( "/mcp/search/search.yml", "tools: [{name: yml, inputSchema: {}}]" );

        final var descriptor = service.getByKey( KEY ).orElseThrow();
        assertThat( descriptor.getKey() ).isEqualTo( KEY );
        assertThat( service.getLoadedByKey( KEY ).orElseThrow().tools().getFirst().name() ).isEqualTo( "yaml" );
        verify( yaml, times( 2 ) ).readString();
        verify( yml, times( 0 ) ).readString();
        assertThat( service.getByApplication( APP ) ).hasSize( 1 );
    }

    @Test
    void loads_yml_when_yaml_is_absent()
    {
        addResource( "/mcp/search/search.yml", "tools: [{name: search, inputSchema: {}}]" );
        assertThat( service.getLoadedByKey( KEY ).orElseThrow().tools().getFirst().name() ).isEqualTo( "search" );
        assertThat( service.getByApplication( APP ) ).hasSize( 1 );
    }

    @Test
    void returns_empty_for_missing_groups_and_applications()
    {
        assertThat( service.getByKey( KEY ) ).isEmpty();
        assertThat( service.getByApplication( APP ) ).isEmpty();
        assertThat( service.getLoadedByKey( KEY ) ).isEmpty();
        assertThat( service.getLoadedByApplication( APP ) ).isEmpty();
        verify( resourceService, times( 2 ) ).findFiles( eq( APP ), eq( "^/mcp/(?<name>[^/]+)/\\k<name>\\.(?:yaml|yml)$" ) );
    }

    @Test
    void ignores_js_only_groups_and_unmatched_paths()
    {
        addResource( "/mcp/search/search.js", "exports.search = function () {};" );
        addResource( "/mcp/other/wrong.yaml", "tools: []" );
        addResource( "/mcp/nested/group/group.yaml", "tools: []" );
        addResource( "/apis/search/search.yaml", "tools: []" );

        assertThat( service.getByKey( KEY ) ).isEmpty();
        assertThat( service.getByApplication( APP ) ).isEmpty();
    }

    @Test
    void orders_groups_deterministically_and_limits_application()
    {
        addResource( "/mcp/zeta/zeta.yml", "tools: []" );
        addResource( "/mcp/alpha/alpha.yaml", "tools: []" );
        addResource( "/mcp/alpha/alpha.yml", "tools: []" );
        final var otherKey = ResourceKey.from( "other:/mcp/other/other.yaml" );
        final var other = mock( Resource.class );
        when( other.exists() ).thenReturn( true );
        resources.put( otherKey, other );

        assertThat( service.getByApplication( APP ) ).extracting( descriptor -> descriptor.getKey().getName() )
            .containsExactly( "alpha", "zeta" );
        assertThat( service.getLoadedByApplication( APP ) ).extracting( group -> group.descriptor().getKey().getName() )
            .containsExactly( "alpha", "zeta" );
    }

    @Test
    void delegates_processing_and_observes_resource_reload()
    {
        final var resource = addResource( "/mcp/search/search.yml", "tools: [{name: before, inputSchema: {}}]" );
        assertThat( service.getLoadedByKey( KEY ).orElseThrow().tools().getFirst().name() ).isEqualTo( "before" );

        when( resource.readString() ).thenReturn( "tools: [{name: after, inputSchema: {}}]" );
        assertThat( service.getLoadedByKey( KEY ).orElseThrow().tools().getFirst().name() ).isEqualTo( "after" );
        verify( resourceService, times( 2 ) ).processResource( any() );
    }

    @Test
    void shares_cached_groups_and_sdk_tools_across_metadata_and_loaded_access()
    {
        final var resource = addResource( "/mcp/search/search.yaml", "tools: [{name: search, inputSchema: {}}]" );
        final var cache = new LinkedHashMap<String, LoadedMcpGroup>();
        doAnswer( invocation -> {
            final ResourceProcessor<DescriptorKey, LoadedMcpGroup> processor = invocation.getArgument( 0 );
            return cache.computeIfAbsent( processor.getSegment() + ":" + processor.getKey(),
                                          ignored -> processor.process( resourceService.getResource( processor.toResourceKey() ) ) );
        } ).when( resourceService ).processResource( any() );

        final var descriptor = service.getByKey( KEY ).orElseThrow();
        final var loaded = service.getLoadedByKey( KEY ).orElseThrow();
        assertThat( descriptor ).isSameAs( loaded.descriptor() );
        assertThat( service.getByApplication( APP ) ).containsExactly( descriptor );
        assertThat( service.getLoadedByApplication( APP ) ).containsExactly( loaded );
        assertThat( service.getLoadedByKey( KEY ).orElseThrow().tools().getFirst() ).isSameAs( loaded.tools().getFirst() );
        verify( resource ).readString();
    }

    @Test
    void reselects_yaml_after_resource_changes()
    {
        final var yml = addResource( "/mcp/search/search.yml", "tools: [{name: yml, inputSchema: {}}]" );
        assertThat( service.getLoadedByKey( KEY ).orElseThrow().tools().getFirst().name() ).isEqualTo( "yml" );
        final var yaml = addResource( "/mcp/search/search.yaml", "tools: [{name: yaml, inputSchema: {}}]" );
        assertThat( service.getLoadedByKey( KEY ).orElseThrow().tools().getFirst().name() ).isEqualTo( "yaml" );
        when( yaml.exists() ).thenReturn( false );
        when( yml.exists() ).thenReturn( false );
        assertThat( service.getByKey( KEY ) ).isEmpty();
    }

    @Test
    void skips_groups_removed_after_discovery()
    {
        when( resourceService.findFiles( eq( APP ), anyString() ) )
            .thenReturn( ResourceKeys.from( "myapp:/mcp/search/search.yaml" ) );
        assertThat( service.getByApplication( APP ) ).isEmpty();
    }

    @Test
    void resolves_controller_without_requiring_descriptor_or_controller()
    {
        assertThat( service.getControllerResourceKey( KEY ) ).isEqualTo( ResourceKey.from( "myapp:/mcp/search/search.js" ) );
    }

    @Test
    void reports_invalid_descriptor_with_resource_key_and_cause()
    {
        addResource( "/mcp/search/search.yaml", "tools: [{name: search, inputSchema: []}]" );
        addResource( "/mcp/search/search.yml", "tools: []" );
        assertThatThrownBy( () -> service.getByKey( KEY ) ).isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "myapp:/mcp/search/search.yaml" )
            .hasMessageContaining( "tools" )
            .hasMessageContaining( "inputSchema" )
            .hasCauseInstanceOf( IllegalArgumentException.class );
    }

    @Test
    void reports_resource_read_errors_with_resource_key()
    {
        final var resource = addResource( "/mcp/search/search.yml", "tools: []" );
        when( resource.readString() ).thenThrow( new IllegalStateException( "Cannot read resource" ) );
        assertThatThrownBy( () -> service.getByKey( KEY ) ).hasMessageContaining( "myapp:/mcp/search/search.yml" )
            .hasMessageContaining( "Cannot read resource" ).hasCauseInstanceOf( IllegalStateException.class );
    }

    private Resource addResource( final String path, final String source )
    {
        final var key = ResourceKey.from( APP, path );
        final var resource = mock( Resource.class );
        when( resource.getKey() ).thenReturn( key );
        when( resource.exists() ).thenReturn( true );
        when( resource.readString() ).thenReturn( source );
        resources.put( key, resource );
        return resource;
    }
}
