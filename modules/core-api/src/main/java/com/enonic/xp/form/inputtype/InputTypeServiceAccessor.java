package com.enonic.xp.form.inputtype;

public final class InputTypeServiceAccessor
{
    private final static InputTypeService SERVICE = new InputTypeServiceImpl();

    public static InputTypeService get()
    {
        return SERVICE;
    }
}
