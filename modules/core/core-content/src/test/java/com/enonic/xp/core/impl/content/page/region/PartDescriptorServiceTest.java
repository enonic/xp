package com.enonic.xp.core.impl.content.page.region;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.core.internal.form.FormFragmentException;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PartDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected PartDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.service = new PartDescriptorServiceImpl( this.descriptorService );
    }

    @Test
    void testGetByKey()
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:mypart" );
        final PartDescriptor descriptor = this.service.getByKey( key );

        assertNotNull( descriptor );
        assertTrue( Instant.now().isAfter( descriptor.getModifiedTime() ) );
    }

    // a fragment the config includes but which does not exist is reported, not swallowed like a descriptor failing to parse
    @Test
    void missing_form_fragment_is_reported()
    {
        when( this.formFragmentService.inlineFormItems( any() ) ).thenThrow(
            new FormFragmentException( "Form fragment [myapp1:missing] not found" ) );

        final FormFragmentException e =
            assertThrows( FormFragmentException.class, () -> this.service.getByKey( DescriptorKey.from( "myapp1:mypart" ) ) );
        assertEquals( "Form fragment [myapp1:missing] not found", e.getMessage() );
    }

    @Test
    void testGetByApplication()
    {
        final PartDescriptors result = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );

        assertNotNull( result );
        assertEquals( 1, result.getSize() );
    }

    @Test
    void testGetByApplications()
    {
        final PartDescriptors result = this.service.getByApplications( ApplicationKeys.from( "myapp1", "myapp2" ) );

        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }
}
