package com.enonic.xp.descriptor;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DescriptorKeyLocatorTest
{
    private static final ApplicationKey APP = ApplicationKey.from( "myapp" );

    private static final List<String> FILES =
        List.of( "/cms/parts/legacy/legacy.yaml", "/cms/parts/legacyyml/legacyyml.yml", "/cms/parts/controller/controller.js",
                 "/cms/parts/flat.yaml", "/cms/parts/flatyml.yml", "/cms/parts/both.yaml", "/cms/parts/both/both.yaml",
                 "/cms/parts/flatcontroller.js", "/cms/parts/other/wrong.yaml", "/cms/parts/a/b/c.yaml", "/tasks/legacy/legacy.yaml",
                 "/tasks/flat.yaml" );

    @Test
    void cms_legacy_and_flat()
    {
        final DescriptorKeyLocator locator = new DescriptorKeyLocator( service(), "/cms/parts", false );

        assertThat( names( locator ) ).containsExactlyInAnyOrder( "legacy", "legacyyml", "flat", "flatyml", "both" );
    }

    @Test
    void cms_optional_controller_only_in_folder()
    {
        final DescriptorKeyLocator locator = new DescriptorKeyLocator( service(), "/cms/parts", true );

        assertThat( names( locator ) ).containsExactlyInAnyOrder( "legacy", "legacyyml", "controller", "flat", "flatyml", "both" );
    }

    @Test
    void non_cms_legacy_only()
    {
        final DescriptorKeyLocator locator = new DescriptorKeyLocator( service(), "/tasks", false );

        assertThat( names( locator ) ).containsExactly( "legacy" );
    }

    private static List<String> names( final DescriptorKeyLocator locator )
    {
        return locator.findKeys( APP ).stream().map( DescriptorKey::getName ).toList();
    }

    private static ResourceService service()
    {
        final ResourceService service = mock( ResourceService.class );
        when( service.findFiles( eq( APP ), anyString() ) ).thenAnswer( invocation -> {
            final Pattern pattern = Pattern.compile( invocation.getArgument( 1 ) );
            return FILES.stream()
                .filter( path -> pattern.matcher( path ).find() )
                .map( path -> ResourceKey.from( APP, path ) )
                .collect( ResourceKeys.collector() );
        } );
        return service;
    }
}
