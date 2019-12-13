package com.enonic.xp.portal.impl.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.view.ViewFunctionParams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ViewFunctionServiceImplTest
{
    private ViewFunctionServiceImpl service;

    @BeforeEach
    public void setup()
    {
        this.service = new ViewFunctionServiceImpl();
    }

    @Test
    public void testFound()
    {
        this.service.addFunction( new DummyViewFunction() );

        final ViewFunctionParams params = new ViewFunctionParams().name( "dummy" );
        final Object result = this.service.execute( params );

        assertEquals( "Hello Dummy", result );
    }

    @Test
    public void testNotFound()
    {
        final ViewFunctionParams params = new ViewFunctionParams().name( "dummy" );
        assertThrows(IllegalArgumentException.class, () -> this.service.execute( params ));
    }

    @Test
    public void testAddRemove_notFound()
    {
        final DummyViewFunction function = new DummyViewFunction();
        this.service.addFunction( function );
        this.service.removeFunction( function );

        final ViewFunctionParams params = new ViewFunctionParams().name( "dummy" );
        assertThrows(IllegalArgumentException.class, () -> this.service.execute( params ));
    }
}
