package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.command.schema.content.GetAllContentTypes;
import com.enonic.wem.api.command.schema.mixin.GetMixin;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.support.SerializerForFormItemToData;

import static com.enonic.wem.api.form.Form.newForm;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class GetAllContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetAllContentTypesHandler handler;

    private static final SerializerForFormItemToData SERIALIZER_FOR_FORM_ITEM_TO_DATA = new SerializerForFormItemToData();

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();
        handler = new GetAllContentTypesHandler();
        handler.setContext( this.context );
    }

    @Test
    public void handle()
        throws Exception
    {
        // Setup:
        final String contentType1Name = "my-contenttype-1";
        final String contentType2Name = "my-contenttype-2";

        final Nodes nodes = Nodes.newNodes().
            add( Node.newNode().
                name( contentType1Name ).
                id( EntityId.from( "123" ) ).
                property( "displayName", "DisplayName" ).
                build() ).
            add( Node.newNode().
                name( contentType2Name ).
                id( EntityId.from( "234" ) ).
                property( "displayName", "DisplayName2" ).
                build() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetNodesByParent.class ) ) ).thenReturn( nodes );

        // Exercise:
        GetAllContentTypes command = Commands.contentType().get().all();
        this.handler.setCommand( command );
        this.handler.handle();

        // Verify:
        final ContentTypes result = command.getResult();
        assertEquals( 2, result.getSize() );

        verifyContentType( contentType1Name, "DisplayName", result );
        verifyContentType( contentType2Name, "DisplayName2", result );

    }

    @Test
    public void handle_given_mixin()
        throws Exception
    {
        final Mixin mixin = newMixin().name( "my_mixin" ).
            addFormItem( newInput().name( "inputToBeMixedIn" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        final Form form = newForm().addFormItem( newMixinReference( mixin ).name( "myMixin" ).build() ).build();
        final RootDataSet rootDataSetWithForm = createRootDataSetWithForm( form );

        final Nodes nodes = Nodes.newNodes().
            add( Node.newNode().
                name( "my-contenttype-1" ).
                id( EntityId.from( "1" ) ).
                rootDataSet( rootDataSetWithForm ).
                build() ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetNodesByParent.class ) ) ).thenReturn( nodes );
        Mockito.when( client.execute( Mockito.isA( GetMixin.class ) ) ).thenReturn( mixin );

        // Exercise:
        GetAllContentTypes command = Commands.contentType().get().all().mixinReferencesToFormItems( true );
        this.handler.setCommand( command );
        this.handler.handle();

        // One invocation for each contentType with mixin-reference
        Mockito.verify( client, Mockito.times( 1 ) ).execute( Mockito.isA( GetMixin.class ) );
        final ContentTypes result = command.getResult();
        assertEquals( 1, result.getSize() );
        assertNotNull( result.get( 0 ).form().getInput( "inputToBeMixedIn" ) );
        assertNull( result.get( 0 ).form().getFormItem( "myMixin" ) );

    }

    private RootDataSet createRootDataSetWithForm( final Form form )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        final DataSet formItems = new DataSet( "form/formItems" );
        for ( Data data : SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems( form.getFormItems() ) )
        {
            formItems.add( data );
        }
        rootDataSet.add( formItems );
        rootDataSet.setProperty( "displayName", new Value.String( "DisplayName" ) );
        return rootDataSet;
    }

    private void verifyContentType( final String contentTypeName, final String displayName, final ContentTypes result )
    {
        final ContentType contentType = result.getContentType( ContentTypeName.from( contentTypeName ) );
        assertNotNull( contentType );
        assertEquals( contentTypeName, contentType.getName() );
        assertEquals( displayName, contentType.getDisplayName() );
    }
}
