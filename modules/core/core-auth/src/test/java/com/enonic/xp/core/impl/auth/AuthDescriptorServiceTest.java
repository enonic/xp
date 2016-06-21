package com.enonic.xp.core.impl.auth;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorMode;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

public class AuthDescriptorServiceTest
    extends ApplicationTestSupport
{

    protected AuthDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( AuthDescriptorServiceImpl.DEFAULT_AUTH_APPLICATION_KEY.toString(), "/apps/default" );
        this.service = new AuthDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
    }

    @Test
    public void testGetDescriptor()
        throws Exception
    {
        final AuthDescriptor authDescriptor = this.service.getDescriptor( ApplicationKey.from( "myapp1" ) );

        Assert.assertNotNull( authDescriptor );
        Assert.assertEquals( ApplicationKey.from( "myapp1" ), authDescriptor.getKey() );
        Assert.assertEquals( AuthDescriptorMode.MIXED, authDescriptor.getMode() );

        final Input titleInput = Input.create().
            name( "title" ).
            label( "Title" ).
            inputType( InputTypeName.TEXT_LINE ).
            build();
        Assert.assertEquals( Form.create().addFormItem( titleInput ).build(), authDescriptor.getConfig() );
    }

    @Test
    public void testDefaultDescriptor()
        throws Exception
    {
        final AuthDescriptor authDescriptor = this.service.getDefaultDescriptor();

        Assert.assertNotNull( authDescriptor );
        Assert.assertEquals( AuthDescriptorServiceImpl.DEFAULT_AUTH_APPLICATION_KEY, authDescriptor.getKey() );
        Assert.assertEquals( AuthDescriptorMode.LOCAL, authDescriptor.getMode() );
        Assert.assertEquals( Form.create().build(), authDescriptor.getConfig() );
    }
}
