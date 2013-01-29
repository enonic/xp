package com.enonic.wem.migrate.account;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

final class OldUserField
{
    private final OldUserFieldType type;

    private Object value;

    public OldUserField( OldUserFieldType type, Object value )
    {
        this.type = type;
        setValue( value );
    }

    public OldUserFieldType getType()
    {
        return this.type;
    }

    public boolean isOfType( OldUserFieldType type )
    {
        return this.type == type;
    }

    public Object getValue()
    {
        return this.value;
    }

    public String getValueAsString()
    {
        return (String) this.value;
    }

    public Date getValueAsDate()
    {
        return (Date) this.value;
    }

    public Locale getValueAsLocale()
    {
        return (Locale) this.value;
    }

    public Boolean getValueAsBoolean()
    {
        return (Boolean) this.value;
    }

    public OldGender getValueAsGender()
    {
        return (OldGender) this.value;
    }

    public TimeZone getValueAsTimeZone()
    {
        return (TimeZone) this.value;
    }

    public byte[] getValueAsBytes()
    {
        return (byte[]) this.value;
    }

    public OldAddress getValueAsAddress()
    {
        return (OldAddress) this.value;
    }

    public void setValue( Object value )
    {
        checkType( value );
        this.value = value;
    }

    private void checkType( Object value )
    {
        if ( value == null )
        {
            return;
        }

        Class<?> clz = value.getClass();
        if ( !this.type.isOfType( clz ) )
        {
            throw new IllegalArgumentException( "Value must be of type [" + this.type.getTypeClass() + "]" );
        }
    }


    public boolean equals( OldUserField compareField )
    {
        if ( compareField == null )
        {
            return false;
        }
        else if ( getType() != compareField.getType() )
        {
            return false;
        }
        else if ( getValue() == null && compareField.getValue() == null )
        {
            return true;
        }
        else if ( getValue() == null && compareField.getValue() != null )
        {
            return false;
        }
        else if ( getValue() != null && compareField.getValue() == null )
        {
            return false;
        }
        else
        {
            if ( isOfType( OldUserFieldType.PHOTO ) )
            {
                byte[] commandPhoto = (byte[]) getValue();
                byte[] remotePhoto = (byte[]) compareField.getValue();
                if ( !( Arrays.equals( commandPhoto, remotePhoto ) ) )
                {
                    return false;
                }
            }
            else
            {
                if ( bothAreBlankStrings( getValue(), compareField.getValue() ) )
                {
                    return true;
                }

                if ( !( getValue().equals( compareField.getValue() ) ) )
                {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean bothAreBlankStrings( Object a, Object b )
    {
        if ( a instanceof String )
        {
            if ( StringUtils.isBlank( (String) a ) && StringUtils.isBlank( (String) b ) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        OldUserField userField = (OldUserField) o;

        if ( type != userField.type )
        {
            return false;
        }
        if ( value != null ? !value.equals( userField.value ) : userField.value != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        final int initialNonZeroOddNumber = 443;
        final int multiplierNonZeroOddNumber = 971;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( type ).append( value ).toHashCode();
    }
}
