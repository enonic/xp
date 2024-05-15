package com.enonic.xp.portal.impl.api;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptors;
import com.enonic.xp.api.ApiMount;
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

        final ApiDescriptor apiDescriptor = descriptors.get( 0 );

        final PrincipalKeys allowedPrincipals = apiDescriptor.getAllowedPrincipals();
        assertNotNull( allowedPrincipals );
        assertEquals( 1, allowedPrincipals.getSize() );
        assertEquals( allowedPrincipals.first(), PrincipalKey.from( "role:system.admin" ) );

        final Set<ApiMount> mounts = apiDescriptor.getMounts();
        assertNotNull( allowedPrincipals );
        assertEquals( 1, mounts.size() );
        assertEquals( mounts.iterator().next(), ApiMount.API );
    }
}
