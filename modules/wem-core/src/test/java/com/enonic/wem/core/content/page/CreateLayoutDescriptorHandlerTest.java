package com.enonic.wem.core.content.page;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.layout.CreateLayoutDescriptor;
import com.enonic.wem.api.command.module.CreateModuleResource;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.page.layout.CreateLayoutDescriptorHandler;

import static com.enonic.wem.api.form.Input.newInput;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.isA;

public class CreateLayoutDescriptorHandlerTest
    extends AbstractCommandHandlerTest
{

    private CreateLayoutDescriptorHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        handler = new CreateLayoutDescriptorHandler();
        handler.setContext( this.context );
    }

    @Test
    public void testCreateLayoutDescriptor()
        throws Exception
    {
        Form layoutForm = Form.newForm().
            addFormItem( newInput().name( "columns" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            build();

        final ModuleKey module = ModuleKey.from( "mainmodule-1.0.0" );
        final ResourcePath path = ResourcePath.from( "components/layout.xml" );
        final LayoutDescriptorKey key = LayoutDescriptorKey.from( module, path );
        final CreateLayoutDescriptor command = new CreateLayoutDescriptor().
            key( key ).
            name( new ComponentDescriptorName( "fancy-layout" ) ).
            displayName( "Fancy layout" ).
            config( layoutForm ).
            controllerResource( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/fancy-layout.js" ) );

        Resource res = Resource.newResource().build();
        Mockito.when( this.client.execute( isA( CreateModuleResource.class ) ) ).thenReturn( res );
        handler.setCommand( command );
        handler.handle();

        assertEquals( key, command.getResult().getKey() );
    }
}
