package com.enonic.wem.core.content.data;

import org.elasticsearch.common.base.Preconditions;
import org.joda.time.DateMidnight;

import com.google.common.base.Objects;

import com.enonic.wem.core.content.datatype.DataType;
import com.enonic.wem.core.content.datatype.JavaType;
import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.InvalidValueException;


public class Data
    extends Entry
{
    private EntryPath path;

    /**
     * Optional.
     */
    private Field field;

    private Object value;

    private DataType type;

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

    public String getString()
    {
        if ( type.isConvertibleTo( JavaType.STRING ) )
        {
            return type.convertToString( value );
        }
        return null;
    }

    public DateMidnight getDate()
    {
        //return DataTypes.DATE.toDate( this );
        return JavaType.DATE.toDate( this );
    }

    public DataType getDataType()
    {
        return type;
    }

    public void checkValidity()
        throws InvalidValueException
    {
        type.checkValidity( value );
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "path", path );
        s.add( "field", field );
        s.add( "type", type.getName() );
        s.add( "value", value );
        return s.toString();
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

        private DataType type;


        public Builder()
        {
            value = new Data();
        }

        public Builder field( Field value )
        {
            this.field = value;
            if ( value != null )
            {
                this.type = this.field.getFieldType().getDataType();
            }
            return this;
        }

        public Builder path( EntryPath value )
        {
            this.path = value;
            return this;
        }

        public Builder type( DataType value )
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
            if ( field != null )
            {
                Preconditions.checkArgument( this.type.equals( this.field.getFieldType().getDataType() ),
                                             "Given DataType [%s] does not match the given field's DataType: " +
                                                 field.getFieldType().getDataType(), type );
            }

            Preconditions.checkNotNull( this.type, "type is required" );

            final Data data = new Data();
            data.path = this.path;
            data.field = this.field;
            data.type = this.type;
            data.value = this.value != null
                ? data.type.ensureType( this.value )
                : null; // TODO: Research, is null values needed? If not should not be allowed...

            return data;
        }
    }
}
