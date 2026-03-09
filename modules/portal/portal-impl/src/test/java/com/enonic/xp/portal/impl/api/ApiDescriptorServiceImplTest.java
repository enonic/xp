package com.enonic.xp.portal.impl.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.core.impl.schema.JsonSchemaService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class ApiDescriptorServiceImplTest
    extends AbstractDescriptorServiceTest
{
    private ApiDescriptorServiceImpl service;

    private JsonSchemaService jsonSchemaService;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.jsonSchemaService = Mockito.mock( JsonSchemaService.class );
        this.service = new ApiDescriptorServiceImpl( this.resourceService, this.jsonSchemaService );
    }

    @Test
    void testGetByApplication()
    {
        final ApplicationKey key = ApplicationKey.from( "myapp1" );
        final ApiDescriptors descriptors = this.service.getByApplication( key );

        assertNotNull( descriptors );
        assertEquals( 2, descriptors.getSize() );

        descriptors.forEach( descriptor -> {
            assertNotNull( descriptor );
            if ( descriptor.getKey().equals( DescriptorKey.from( ApplicationKey.from( "myapp1" ), "api" ) ) )
            {
                final PrincipalKeys allowedPrincipals = descriptor.getAllowedPrincipals();
                assertNotNull( allowedPrincipals );
                assertEquals( 1, allowedPrincipals.getSize() );
                assertEquals( allowedPrincipals.first(), PrincipalKey.from( "role:cms.admin" ) );
            }
            else if ( descriptor.getKey().equals( DescriptorKey.from( ApplicationKey.from( "myapp1" ), "myapi" ) ) )
            {
                final PrincipalKeys allowedPrincipals = descriptor.getAllowedPrincipals();
                assertNotNull( allowedPrincipals );
                assertEquals( 2, allowedPrincipals.getSize() );
            }
        } );
    }

    @Test
    void testGetByApplicationEmpty()
    {
        final ApplicationKey key = ApplicationKey.from( "myapp2" );
        final ApiDescriptors descriptors = this.service.getByApplication( key );

        assertNotNull( descriptors );
        assertEquals( 0, descriptors.getSize() );
    }

    @Test
    void testGetByKey()
    {
        final DescriptorKey key = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "api" );
        final ApiDescriptor descriptor = this.service.getByKey( key );

        assertNotNull( descriptor );
        assertEquals( key, descriptor.getKey() );
        assertEquals( 1, descriptor.getAllowedPrincipals().getSize() );
        assertEquals( PrincipalKey.from( "role:cms.admin" ), descriptor.getAllowedPrincipals().first() );
    }

    @Test
    void testGetByKeyMultiplePrincipals()
    {
        final DescriptorKey key = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "myapi" );
        final ApiDescriptor descriptor = this.service.getByKey( key );

        assertNotNull( descriptor );
        assertEquals( key, descriptor.getKey() );

        final PrincipalKeys allowedPrincipals = descriptor.getAllowedPrincipals();
        assertEquals( 2, allowedPrincipals.getSize() );
        assertEquals( PrincipalKey.from( "role:cms.admin" ), allowedPrincipals.first() );
    }

    @Test
    void testGetByKeyNotFound()
    {
        final DescriptorKey key = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "nonexistent" );
        final ApiDescriptor descriptor = this.service.getByKey( key );

        assertNull( descriptor );
    }

    @Test
    void testGetControllerResourceKey()
    {
        final DescriptorKey key = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "api" );
        final ResourceKey controllerKey = this.service.getControllerResourceKey( key );

        assertNotNull( controllerKey );
        assertEquals( ResourceKey.from( "myapp1:/apis/api/api.js" ), controllerKey );
    }

    @Test
    void testGetControllerResourceKeyPath()
    {
        final DescriptorKey key = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "myapi" );
        final ResourceKey controllerKey = this.service.getControllerResourceKey( key );

        assertEquals( ApplicationKey.from( "myapp1" ), controllerKey.getApplicationKey() );
        assertEquals( "/apis/myapi/myapi.js", controllerKey.getPath() );
    }

    @Test
    void testValidationCalledOnGetByKey()
    {
        final DescriptorKey key = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "api" );
        this.service.getByKey( key );

        verify( this.jsonSchemaService ).validate( eq( "https://json-schema.enonic.com/8.0.0/api.schema.json" ), anyString() );
    }

    @Test
    void testValidationFailureOnGetByKey()
    {
        doThrow( new RuntimeException( "Validation failed" ) ).when( this.jsonSchemaService ).validate( anyString(), anyString() );

        final DescriptorKey key = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "api" );
        assertThrows( RuntimeException.class, () -> this.service.getByKey( key ) );
    }
}
