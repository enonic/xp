package com.enonic.wem.core.content.page;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.part.CreatePartDescriptor;
import com.enonic.wem.api.command.module.CreateModuleResource;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
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
        final ResourcePath path = ResourcePath.from( "components/news-part.xml" );
        final PartDescriptorKey key = PartDescriptorKey.from( module, path );
        final CreatePartDescriptor command = new CreatePartDescriptor().
            key( key ).
            name( "news-part" ).
            displayName( "News part" ).
            config( partForm ).
            controllerResource( ModuleResourceKey.from( "mainmodule-1.0.0:/controller/news-part.js" ) );

        Resource res = Resource.newResource().build();
        Mockito.when( this.client.execute( isA( CreateModuleResource.class ) ) ).thenReturn( res );
        handler.setCommand( command );
        handler.handle();

        assertEquals( key, command.getResult().getKey() );
    }
}
