package com.enonic.wem.core.schema.mixin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.form.Input.newInput;
import static org.junit.Assert.*;

public class CreateMixinHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateMixinHandler handler;

    private MixinDao mixinDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        mixinDao = Mockito.mock( MixinDao.class );

        handler = new CreateMixinHandler();
        handler.setContext( this.context );
        handler.setMixinDao( mixinDao );
    }

    @Test
    public void createMixin()
        throws Exception
    {
        // setup
        final Mixin createdMixin = Mixin.newMixin().
            name( "age" ).
            displayName( "Age" ).
            description( "description" ).
            addFormItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        Mockito.when( mixinDao.createMixin( Mockito.isA( Mixin.class ) ) ).thenReturn( createdMixin );

        // exercise
        final CreateMixin command = Commands.mixin().create().
            name( "age" ).
            displayName( "Age" ).
            addFormItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( mixinDao, Mockito.atLeastOnce() ).createMixin( Mockito.isA( Mixin.class ) );

        Mixin mixin = command.getResult();
        assertNotNull( mixin );
        assertEquals( "age", mixin.getName().toString() );
        assertEquals( "description", mixin.getDescription() );
    }

}
