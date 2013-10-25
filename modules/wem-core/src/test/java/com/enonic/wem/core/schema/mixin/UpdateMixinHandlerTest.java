package com.enonic.wem.core.schema.mixin;

import org.junit.Ignore;

import com.enonic.wem.core.command.AbstractCommandHandlerTest;

@Ignore
public class UpdateMixinHandlerTest
    extends AbstractCommandHandlerTest
{
    //private UpdateMixinHandler handler;

    //private NodeDao nodeDao;

    /*@Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        nodeDao = Mockito.mock( NodeDao.class );

        handler = new UpdateMixinHandler();
        handler.setContext( this.context );
        handler.setNodeDao( nodeDao );
    }*/

    /*@Test
    public void updateMixin()
        throws Exception
    {
        // setup
        final Mixin existingMixin = newMixin().
            name( "age" ).
            displayName( "Age" ).
            addFormItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        Mockito.when(
            nodeDao.select( Mockito.eq( QualifiedMixinNames.from( "mymodule:age" ) ), Mockito.any( Session.class ) ) ).thenReturn(
            Mixins.from( existingMixin ) );

        final Mixins mixins = Mixins.from( existingMixin );
        Mockito.when( nodeDao.select( isA( QualifiedMixinNames.class ), any( Session.class ) ) ).thenReturn( mixins );

        final FormItem formItemToSet = newInput().name( "age" ).inputType( InputTypes.WHOLE_NUMBER ).build();
        final UpdateMixin command = Commands.mixin().update().
            qualifiedName( QualifiedMixinName.from( "mymodule:age" ) ).editor( SetMixinEditor.newSetMixinEditor().
            displayName( "age2" ).
            addFormItem( formItemToSet ).build() );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( nodeDao, atLeastOnce() ).update( Mockito.isA( Mixin.class ), Mockito.any( Session.class ) );
        assertEquals( UpdateMixinResult.SUCCESS, command.getResult() );
    }*/

    /*@Test
    public void updateMixinNotFound()
        throws Exception
    {
        // setup
        final Mixins mixins = Mixins.empty();
        Mockito.when( nodeDao.select( isA( QualifiedMixinNames.class ), any( Session.class ) ) ).thenReturn( mixins );

        final FormItem formItemToSet = newInput().name( "age" ).inputType( InputTypes.WHOLE_NUMBER ).build();
        final UpdateMixin command = Commands.mixin().update().
            qualifiedName( QualifiedMixinName.from( "mymodule:age" ) ).editor( SetMixinEditor.newSetMixinEditor().
            displayName( "age2" ).
            addFormItem( formItemToSet ).build() );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( nodeDao, never() ).update( Mockito.isA( Mixin.class ), Mockito.any( Session.class ) );
        assertEquals( UpdateMixinResult.NOT_FOUND, command.getResult() );
    }*/
}
