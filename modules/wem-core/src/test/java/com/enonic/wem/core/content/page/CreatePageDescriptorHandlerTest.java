package com.enonic.wem.core.content.page;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.CreatePageDescriptor;
import com.enonic.wem.api.command.module.CreateModuleResource;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static com.enonic.wem.api.form.Input.newInput;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.isA;

public class CreatePageDescriptorHandlerTest
    extends AbstractCommandHandlerTest
{

    private CreatePageDescriptorHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        handler = new CreatePageDescriptorHandler();
        handler.setContext( this.context );
    }

    @Test
    public void testCreatePageDescriptor()
        throws Exception
    {
        Form pageForm = Form.newForm().
            addFormItem( newInput().name( "pause" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            build();
        final ModuleKey module = ModuleKey.from( "mainmodule-1.0.0" );
        final ComponentDescriptorName descriptorName = new ComponentDescriptorName( "landing-page" );
        final PageDescriptorKey key = PageDescriptorKey.from( module, descriptorName );
        final CreatePageDescriptor command = new CreatePageDescriptor().
            key( key ).
            name( descriptorName ).
            displayName( "Landing page" ).
            config( pageForm );

        Resource res = Resource.newResource().build();
        Mockito.when( this.client.execute( isA( CreateModuleResource.class ) ) ).thenReturn( res );
        handler.setCommand( command );
        handler.handle();

        assertEquals( key, command.getResult().getKey() );
    }
}
