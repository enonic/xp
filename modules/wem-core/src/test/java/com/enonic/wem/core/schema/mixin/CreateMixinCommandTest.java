package com.enonic.wem.core.schema.mixin;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.schema.mixin.CreateMixinParams;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.form.Input.newInput;
import static org.junit.Assert.*;

public class CreateMixinCommandTest
    extends AbstractCommandHandlerTest
{
    @Test
    public void createMixin()
    {
        final MixinDao mixinDao = Mockito.mock( MixinDao.class );

        // setup
        final Mixin createdMixin = Mixin.newMixin().
            name( "age" ).
            displayName( "Age" ).
            description( "description" ).
            addFormItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        Mockito.when( mixinDao.createMixin( Mockito.isA( Mixin.class ) ) ).thenReturn( createdMixin );

        // exercise
        final CreateMixinParams params = new CreateMixinParams().
            name( "age" ).
            displayName( "Age" ).
            addFormItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() );

        final Mixin result = new CreateMixinCommand().mixinDao( mixinDao ).params( params ).execute();

        // verify
        Mockito.verify( mixinDao, Mockito.atLeastOnce() ).createMixin( Mockito.isA( Mixin.class ) );

        assertNotNull( result );
        assertEquals( "age", result.getName().toString() );
        assertEquals( "description", result.getDescription() );
    }

}
