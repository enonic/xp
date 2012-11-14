package com.enonic.wem.core.content.type.component.inputtype;


import com.enonic.wem.api.content.type.component.inputtype.BaseInputType;

public class InputTypeFactory
{
    public static BaseInputType instantiate( final String simpleClassName, final boolean builtIn )
    {
        Class clazz;
        try
        {
            if ( !builtIn )
            {
                throw new IllegalArgumentException( "Non-built in input types are currently not supported: " + simpleClassName );
            }
            clazz = Class.forName( BaseInputType.class.getPackage().getName() + "." + simpleClassName );
            return (BaseInputType) clazz.newInstance();
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( e );
        }
        catch ( InstantiationException e )
        {
            throw new RuntimeException( e );
        }
        catch ( IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
    }
}
