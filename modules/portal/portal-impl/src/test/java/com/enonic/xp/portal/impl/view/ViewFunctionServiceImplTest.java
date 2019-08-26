package com.enonic.xp.portal.impl.view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.view.ViewFunctionParams;

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

    @Test(expected = IllegalArgumentException.class)
    public void testNotFound()
    {
        final ViewFunctionParams params = new ViewFunctionParams().name( "dummy" );
        this.service.execute( params );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddRemove_notFound()
    {
        final DummyViewFunction function = new DummyViewFunction();
        this.service.addFunction( function );
        this.service.removeFunction( function );

        final ViewFunctionParams params = new ViewFunctionParams().name( "dummy" );
        this.service.execute( params );
    }
}
