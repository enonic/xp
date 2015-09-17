package com.enonic.xp.convert;

final class StringConverter
    implements Converter<String>
{
    @Override
    public Class<String> getType()
    {
        return String.class;
    }

    @Override
    public String convert( final Object value )
    {
        return value.toString();
    }
}
