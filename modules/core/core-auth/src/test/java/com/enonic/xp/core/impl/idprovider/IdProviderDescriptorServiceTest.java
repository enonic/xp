package com.enonic.xp.core.impl.idprovider;

import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.core.impl.schema.JsonSchemaService;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorMode;
import com.enonic.xp.inputtype.InputTypeName;

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
        this.service = new IdProviderDescriptorServiceImpl( mock( BundleContext.class ) );
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

        final Input titleInput = Input.create().
            name( "title" ).
            label( "Title" ).
            inputType( InputTypeName.TEXT_LINE ).
            build();
        assertEquals( Form.create().addFormItem( titleInput ).build(), idProviderDescriptor.getConfig() );
    }
}
