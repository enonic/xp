package com.enonic.xp.core.impl.auth;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.auth.IdProviderDescriptor;
import com.enonic.xp.auth.IdProviderDescriptorMode;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

public class IdProviderDescriptorServiceTest
    extends ApplicationTestSupport
{

    protected IdProviderDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        addApplication( "myapp1", "/apps/myapp1" );
        this.service = new IdProviderDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
    }

    @Test
    public void testGetDescriptor()
        throws Exception
    {
        final IdProviderDescriptor idProviderDescriptor = this.service.getDescriptor( ApplicationKey.from( "myapp1" ) );

        Assert.assertNotNull( idProviderDescriptor );
        Assert.assertEquals( ApplicationKey.from( "myapp1" ), idProviderDescriptor.getKey() );
        Assert.assertEquals( IdProviderDescriptorMode.MIXED, idProviderDescriptor.getMode() );

        final Input titleInput = Input.create().
            name( "title" ).
            label( "Title" ).
            inputType( InputTypeName.TEXT_LINE ).
            build();
        Assert.assertEquals( Form.create().addFormItem( titleInput ).build(), idProviderDescriptor.getConfig() );
    }
}
