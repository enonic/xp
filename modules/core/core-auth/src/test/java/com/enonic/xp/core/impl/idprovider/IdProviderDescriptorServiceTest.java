package com.enonic.xp.core.impl.idprovider;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.core.impl.schema.JsonSchemaService;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class IdProviderDescriptorServiceTest
    extends ApplicationTestSupport
{

    protected IdProviderDescriptorServiceImpl service;

    @Override
    protected void initialize()
    {
        addApplication( "myapp1", "/apps/myapp1" );
        this.service = new IdProviderDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setJsonSchemaService( mock( JsonSchemaService.class ) );
    }

    @Test
    void testGetDescriptor()
    {
        final IdProviderDescriptor idProviderDescriptor = this.service.getDescriptor( ApplicationKey.from( "myapp1" ) );

        assertNotNull( idProviderDescriptor );
        assertEquals( ApplicationKey.from( "myapp1" ), idProviderDescriptor.getKey() );
        assertEquals( IdProviderDescriptorMode.MIXED, idProviderDescriptor.getMode() );
        assertNotNull( idProviderDescriptor.getConfig().getInput( "title" ) );
        assertNotNull( idProviderDescriptor.getConfig().getInput( "defaultPrincipals" ) );
    }
}
