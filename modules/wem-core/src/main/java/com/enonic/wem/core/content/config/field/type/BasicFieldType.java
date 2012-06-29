package com.enonic.wem.core.content.config.field.type;


import com.enonic.wem.core.content.FieldValue;
import com.enonic.wem.core.content.config.field.type.value.ValueType;

public class BasicFieldType
    extends AbstractBaseFieldType
{
    BasicFieldType( final String name, final ValueType valueType )
    {
        super( name, valueType );
    }

    public boolean validValue( FieldValue fieldValue )
    {
        return true;
    }

    public static Builder newBuilder( String name, ValueType valueType )
    {
        return new Builder( name, valueType );
    }

    public static class Builder
    {
        private BasicFieldType fieldType;

        private Builder( String name, ValueType valueType )
        {
            fieldType = new BasicFieldType( name, valueType );
        }

        public BasicFieldType build()
        {
            return fieldType;
        }
    }

    public FieldTypeJsonGenerator getJsonGenerator()
    {
        return BaseFieldTypeJsonGenerator.DEFAULT;
    }
}
