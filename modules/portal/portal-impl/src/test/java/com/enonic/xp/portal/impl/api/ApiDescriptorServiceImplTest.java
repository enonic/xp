package com.enonic.xp.portal.impl.api;

import org.junit.jupiter.api.Test;

import com.enonic.xp.api.ApiDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApiDescriptorServiceImplTest
    extends AbstractDescriptorServiceTest
{
    private ApiDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.service = new ApiDescriptorServiceImpl( this.resourceService );
    }

    @Test
    public void testGetByApplication()
    {
        final ApplicationKey key = ApplicationKey.from( "myapp1" );
        final ApiDescriptors descriptors = this.service.getByApplication( key );

        assertNotNull( descriptors );

        final PrincipalKeys allowedPrincipals = descriptors.get( 0 ).getAllowedPrincipals();
        assertNotNull( allowedPrincipals );
        assertEquals( 1, allowedPrincipals.getSize() );
        assertEquals( allowedPrincipals.first(), PrincipalKey.from( "role:system.admin" ) );
    }
}
