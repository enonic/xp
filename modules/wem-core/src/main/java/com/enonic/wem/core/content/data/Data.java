package com.enonic.wem.core.content.data;

import org.elasticsearch.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.BreaksRequiredContractException;
import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.datatype.BasalValueType;


public class Data
    extends Entry
{
    private EntryPath path;

    /**
     * Optional.
     */
    private Field field;

    private Object value;

    private BasalValueType basalValueType;

    private Data()
    {
        // protection
    }

    void setField( final Field field )
    {
        ConfigItemPath configItemPath = path.resolveConfigItemPath();
        Preconditions.checkArgument( configItemPath.equals( field.getPath() ),
                                     "This Data's path [%s] does not match given field's path: " + field.getPath(),
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
    public void checkBreaksRequiredContract()
    {
        if ( breaksRequiredContract() )
        {
            throw new BreaksRequiredContractException( this );
        }
    }

    public boolean breaksRequiredContract()
    {
        return field != null && field.breaksRequiredContract( this );
    }

    public boolean isValid()
    {
        return field == null || field.isValidAccordingToFieldTypeConfig( this );
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

    public static Builder newData()
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
            value = new Data();
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

        public Data build()
        {
            final Data data = new Data();
            data.path = path;
            data.field = field;
            data.value = this.value;

            BasalValueType resolvedType = null;
            if ( this.value != null )
            {
                resolvedType = BasalValueType.resolveType( this.value );
                Preconditions.checkArgument( resolvedType != null, "value is of unknown type: " + data.value.getClass().getName() );
            }

            if ( type != null )
            {
                Preconditions.checkArgument( type == resolvedType, "value is not of expected type [%s]: " + type, resolvedType );
                data.basalValueType = type;
            }
            else
            {
                data.basalValueType = resolvedType;
            }

            if ( field != null & data.basalValueType != null )
            {
                BasalValueType basalValueTypeOfField = field.getFieldType().getDataType().getBasalValueType();
                Preconditions.checkArgument( data.basalValueType == basalValueTypeOfField,
                                             "value is not of expected type [%s]: " + data.basalValueType, basalValueTypeOfField );
            }

            Preconditions.checkArgument( data.isValid(), "Value is not valid for field [%s]: " + data.value, field );

            return data;
        }
    }
}
