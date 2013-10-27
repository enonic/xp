package com.enonic.wem.core.schema.mixin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static com.enonic.wem.api.form.Input.newInput;
import static org.junit.Assert.*;

public class CreateMixinHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateMixinHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        handler = new CreateMixinHandler();
        handler.setContext( this.context );
    }

    @Test
    public void createMixin()
        throws Exception
    {
        // setup
        Node node = Node.newNode().
            id( EntityId.from( "abc" ) ).
            name( "age" ).
            property( "displayName", "Age" ).
            addDataSet( new DataSet( "formItems" ) ).
            build();

        Mockito.when( client.execute( Mockito.isA( CreateNode.class ) ) ).thenReturn( new CreateNodeResult( node ) );

        // exercise
        CreateMixin command = Commands.mixin().create().
            name( "age" ).
            displayName( "Age" ).
            addFormItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( client, Mockito.atLeastOnce() ).execute( Mockito.isA( CreateNode.class ) );

        QualifiedMixinName mixinName = command.getResult();
        assertNotNull( mixinName );
        assertEquals( "age", mixinName.toString() );
    }

}
