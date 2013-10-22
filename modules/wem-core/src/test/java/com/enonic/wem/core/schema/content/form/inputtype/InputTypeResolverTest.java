package com.enonic.wem.core.schema.content.form.inputtype;


import org.junit.Test;
import org.mockito.Mockito;

import com.acme.DummyCustomInputType;

import com.enonic.wem.api.form.inputtype.InputTypes;

import static junit.framework.Assert.assertSame;

public class InputTypeResolverTest
{
    @Test
    public void given_name_of_a_builtIn_when_resolve_then_same_instance_is_returned_as_from_InputTypes()
    {
        assertSame( InputTypes.TEXT_LINE, InputTypeResolver.get().resolve( InputTypes.TEXT_LINE.getName() ) );
    }

    @Test
    public void given_name_of_custom_when_resolve_then_instance_is_returned()
    {
        DummyCustomInputType myType = new DummyCustomInputType();
        InputTypeExtensions inputTypeExtensions = Mockito.mock( InputTypeExtensions.class );
        Mockito.when( inputTypeExtensions.getInputType( myType.getName() ) ).thenReturn( myType );
        InputTypeResolver.get().setInputTypeExtensions( inputTypeExtensions );

        assertSame( myType, InputTypeResolver.get().resolve( "custom:DummyCustomInputType" ) );
    }
}
