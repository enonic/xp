package com.enonic.wem.core.content.data;

import org.elasticsearch.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.valuetype.BasalValueType;


public class Value
    extends Entry
{
    private EntryPath path;

    /**
     * Optional.
     */
    private Field field;

    private Object value;

    private BasalValueType basalValueType;

    private Value()
    {
        // protection
    }

    void setField( final Field field )
    {
        ConfigItemPath configItemPath = path.resolveConfigItemPath();
        Preconditions.checkArgument( configItemPath.equals( field.getPath() ),
                                     "This Value's path [%s] does not match given field's path: " + field.getPath(),
                                     configItemPath.toString() );
        this.field = field;
    }

    public Field getField()
    {
        return field;
    }

    @Override
    public EntryPath getPath()
    {
        return path;
    }

    public Object getValue()
    {
        return value;
    }

    public BasalValueType getBasalValueType()
    {
        return basalValueType;
    }

    @Override
    public String toString()
    {
        return String.valueOf( value );
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private EntryPath path;

        private Field field;

        private Object value;

        private BasalValueType type;


        public Builder()
        {
            value = new Value();
        }

        public Builder field( Field value )
        {
            this.field = value;
            return this;
        }

        public Builder path( EntryPath value )
        {
            this.path = value;
            return this;
        }

        public Builder type( BasalValueType value )
        {
            this.type = value;
            return this;
        }

        public Builder value( Object value )
        {
            this.value = value;
            return this;
        }

        public Value build()
        {
            final Value value = new Value();
            value.path = path;
            value.field = field;
            value.value = this.value;

            final BasalValueType resolvedType = BasalValueType.resolveType( this.value );
            Preconditions.checkArgument( resolvedType != null, "value is of unknown type: " + value.value.getClass().getName() );

            if ( type != null )
            {
                Preconditions.checkArgument( type == resolvedType, "value is not of expected type [%s]: " + type, resolvedType );
                value.basalValueType = type;
            }
            else
            {
                value.basalValueType = resolvedType;
            }

            if ( field != null )
            {
                BasalValueType basalValueTypeOfField = field.getFieldType().getValueType().getBasalValueType();
                Preconditions.checkArgument( value.basalValueType == basalValueTypeOfField,
                                             "value is not of expected type [%s]: " + value.basalValueType, basalValueTypeOfField );
            }

            return value;
        }
    }
}
