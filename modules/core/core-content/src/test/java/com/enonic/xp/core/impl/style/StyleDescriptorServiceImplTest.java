package com.enonic.xp.core.impl.style;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.ImageStyleNotFoundException;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class StyleDescriptorServiceImplTest
    extends ApplicationTestSupport

{
    private StyleDescriptorServiceImpl service;

    @Override
    protected void initialize()
    {
        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );

        this.service = new StyleDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setApplicationService( this.applicationService );
    }

    @Test
    void getImageStyle()
    {
        final ImageStyle style = service.getImageStyle( DescriptorKey.from( "myapp1:editor-style-square" ) );
        assertEquals( "editor-style-square", style.getName() );
    }

    @Test
    void missingImageStyle()
    {
        assertThrows( ImageStyleNotFoundException.class,
                      () -> service.getImageStyle( DescriptorKey.from( "myapp1:missing" ) ) );
        final var withoutDescriptor = spy( service );
        doReturn( null ).when( withoutDescriptor ).getByApplication( ApplicationKey.from( "missing" ) );
        assertThrows( ImageStyleNotFoundException.class,
                      () -> withoutDescriptor.getImageStyle( DescriptorKey.from( "missing:card" ) ) );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void invalidImageStyle( final int quality )
    {
        final var invalid = spy( service );
        doReturn( StyleDescriptor.create().application( ApplicationKey.from( "myapp1" ) )
                      .addStyleElement( ImageStyle.create().name( "card" ).quality( quality ).build() ).build() )
            .when( invalid ).getByApplication( ApplicationKey.from( "myapp1" ) );
        assertThrows( IllegalArgumentException.class,
                      () -> invalid.getImageStyle( DescriptorKey.from( "myapp1:card" ) ) );
    }

    @Test
    void getByApplication()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp1" );
        final StyleDescriptor descriptor = this.service.getByApplication( appKey );
        assertNotNull( descriptor );
        assertEquals( descriptor.getApplicationKey(), appKey );
        assertTrue( Instant.now().isAfter( descriptor.getModifiedTime() ) );
    }

    @Test
    void getByApplications()
    {
        final ApplicationKeys appKeys = ApplicationKeys.from( "myapp1", "myapp2" );
        final StyleDescriptors descriptors = this.service.getByApplications( appKeys );
        assertNotNull( descriptors );
        assertEquals( 2, descriptors.getSize() );
    }

    @Test
    void getAll()
    {
        final StyleDescriptors descriptors = this.service.getAll();
        assertNotNull( descriptors );
        assertEquals( 2, descriptors.getSize() );
    }

    @Test
    void getByApplicationInvalidStyles()
    {
        addApplication( "myapp3", "/apps/myapp3" );

        final ApplicationKey appKey = ApplicationKey.from( "myapp3" );
        assertThrows( Exception.class, () -> this.service.getByApplication( appKey ) );
    }
}
