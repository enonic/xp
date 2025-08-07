package com.enonic.xp.core.impl;

import com.enonic.xp.core.impl.form.PropertyTreeMarshallerServiceImpl;
import com.enonic.xp.form.PropertyTreeMarshallerService;

public class PropertyTreeMarshallerServiceFactory
{
    private PropertyTreeMarshallerServiceFactory()
    {
    }

    public static PropertyTreeMarshallerService newInstance( )
    {
        return new PropertyTreeMarshallerServiceImpl();
    }
}
