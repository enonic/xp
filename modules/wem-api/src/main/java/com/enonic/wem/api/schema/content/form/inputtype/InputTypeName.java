package com.enonic.wem.api.schema.content.form.inputtype;


public class InputTypeName
{
    private final String ref;

    private final String name;

    private final boolean custom;

    public static InputTypeName from( final String s )
    {
        if ( s.startsWith( "custom:" ) )
        {
            return new InputTypeName( s.substring( "custom:".length(), s.length() ), true );
        }
        else
        {
            return new InputTypeName( s, false );
        }
    }

    public static InputTypeName from( final InputType inputType )
    {
        if ( inputType.isBuiltIn() )
        {
            return new InputTypeName( inputType.getName(), false );
        }
        else
        {
            return new InputTypeName( inputType.getName(), true );
        }
    }

    public InputTypeName( final String name, final boolean custom )
    {
        this.name = name;
        this.custom = custom;
        this.ref = custom ? "custom:" + name : "" + name;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isCustom()
    {
        return this.custom;
    }

    public String toString()
    {
        return ref;
    }
}
