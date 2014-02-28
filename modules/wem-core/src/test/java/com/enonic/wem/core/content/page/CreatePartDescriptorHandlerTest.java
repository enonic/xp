package com.enonic.wem.core.content.page;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.module.CreateModuleResource;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.part.CreatePartDescriptorParams;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.page.part.CreatePartDescriptorHandler;

import static com.enonic.wem.api.form.Input.newInput;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.isA;

public class CreatePartDescriptorHandlerTest
    extends AbstractCommandHandlerTest
{

    private CreatePartDescriptorHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        handler = new CreatePartDescriptorHandler();
        handler.setContext( this.context );
    }

    @Test
    public void testCreatePartDescriptor()
        throws Exception
    {
        Form partForm = Form.newForm().
            addFormItem( newInput().name( "width" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            build();

        final ModuleKey module = ModuleKey.from( "mainmodule-1.0.0" );
        final ComponentDescriptorName descriptorName = new ComponentDescriptorName( "news-part" );
        final PartDescriptorKey key = PartDescriptorKey.from( module, descriptorName );
        final CreatePartDescriptorParams params = new CreatePartDescriptorParams().
            key( key ).
            name( descriptorName ).
            displayName( "News part" ).
            config( partForm );

        Resource res = Resource.newResource().build();
        Mockito.when( this.client.execute( isA( CreateModuleResource.class ) ) ).thenReturn( res );
        handler.setCommand( params );
        handler.handle();

        assertEquals( key, params.getResult().getKey() );
    }
}
