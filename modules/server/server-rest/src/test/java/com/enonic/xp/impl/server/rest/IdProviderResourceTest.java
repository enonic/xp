package com.enonic.xp.impl.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.recursive.comparison.ComparingProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.server.rest.model.IdProviderJson;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviders;
import com.enonic.xp.security.SecurityService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class IdProviderResourceTest
{
    private IdProviderResource resource;

    @Mock
    private SecurityService securityService;

    @BeforeEach
    public void setUp()
    {
        resource = new IdProviderResource();
        resource.setSecurityService( securityService );
    }

    @Test
    public void testList()
    {
        final IdProviderConfig config1 = mockIdProviderConfig( "app1" );
        final IdProviderConfig config2 = mockIdProviderConfig( "app2" );

        final IdProvider idProvider1 = mockIdProvider( "key1", "Id Provider 1", "description 1", config1 );
        final IdProvider idProvider2 = mockIdProvider( "key2", "Id Provider 2", "description 2", config2 );

        final IdProviders idProviders = IdProviders.from( idProvider1, idProvider2 );
        final List<IdProviderJson> expected = idProviders.stream().map( IdProviderJson::new ).collect( Collectors.toList() );

        when( securityService.getIdProviders() ).thenReturn( idProviders );

        final List<IdProviderJson> result = resource.list();

        verify( securityService, times( 1 ) ).getIdProviders();
        final ComparingProperties introspectionStrategy = new ComparingProperties();
        assertThat( result.get( 0 ) ).usingRecursiveComparison()
            .withIntrospectionStrategy( introspectionStrategy )
            .isEqualTo( expected.get( 0 ) );
        assertThat( result.get( 1 ) ).usingRecursiveComparison()
            .withIntrospectionStrategy( introspectionStrategy )
            .isEqualTo( expected.get( 1 ) );
    }

    private IdProvider mockIdProvider( final String key, final String displayName, final String description,
                                       final IdProviderConfig idProviderConfig )
    {
        IdProvider idProvider = mock( IdProvider.class );

        Mockito.when( idProvider.getKey() ).thenReturn( IdProviderKey.from( key ) );
        Mockito.when( idProvider.getDisplayName() ).thenReturn( displayName );
        Mockito.when( idProvider.getDescription() ).thenReturn( description );
        Mockito.when( idProvider.getIdProviderConfig() ).thenReturn( idProviderConfig );

        return idProvider;
    }

    private IdProviderConfig mockIdProviderConfig( final String applicationKey )
    {
        IdProviderConfig idProviderConfig = mock( IdProviderConfig.class );

        when( idProviderConfig.getApplicationKey() ).thenReturn( ApplicationKey.from( applicationKey ) );
        when( idProviderConfig.getConfig() ).thenReturn( new PropertyTree() );

        return idProviderConfig;
    }

}
