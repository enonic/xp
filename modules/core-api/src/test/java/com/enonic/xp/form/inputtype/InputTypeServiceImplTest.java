package com.enonic.xp.form.inputtype;

import org.junit.Test;

import static org.junit.Assert.*;

public class InputTypeServiceImplTest
{
    private final InputTypeService service;

    public InputTypeServiceImplTest()
    {
        this.service = new InputTypeServiceImpl();
    }

    @Test
    public void resolveType()
    {
        assertSame( InputTypes.TEXT_LINE, this.service.get( InputTypeName.TEXT_LINE ) );
    }

    @Test(expected = InputTypeNotFoundException.class)
    public void resolveType_unknown()
    {
        this.service.get( InputTypeName.from( "unknown" ) );
    }
}
