package com.enonic.wem.migrate.account;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

final class OldUserFields
    implements Iterable<OldUserField>
{
    private final boolean mutlipleAddresses;

    private final Multimap<OldUserFieldType, OldUserField> fields;

    public OldUserFields( boolean mutlipleAddresses )
    {
        this.mutlipleAddresses = mutlipleAddresses;
        this.fields = LinkedHashMultimap.create();
    }

    public void add( final OldUserField field )
    {
        final OldUserFieldType type = field.getType();
        if ( type == OldUserFieldType.ADDRESS )
        {
            if ( this.mutlipleAddresses || !this.fields.containsKey( type ) )
            {
                this.fields.put( type, field );
            }
        }
        else
        {
            this.fields.removeAll( type );
            this.fields.put( type, field );
        }
    }

    public OldUserField getField( OldUserFieldType type )
    {
        Collection<OldUserField> result = this.fields.get( type );
        if ( ( result != null ) && !result.isEmpty() )
        {
            return result.iterator().next();
        }
        else
        {
            return null;
        }
    }

    public Collection<OldUserField> getFields( OldUserFieldType type )
    {
        return this.fields.get( type );
    }

    public Iterator<OldUserField> iterator()
    {
        return this.fields.values().iterator();
    }

    public void addAll( Collection<OldUserField> fields )
    {
        for ( OldUserField field : fields )
        {
            add( field );
        }
    }

    public Collection<OldUserField> getAll()
    {
        return this.fields.values();
    }

    public void clear()
    {
        this.fields.clear();
    }

    public int getSize()
    {
        return this.fields.size();
    }

    public void remove( OldUserFieldType type )
    {
        this.fields.removeAll( type );
    }

    public void remove( Collection<OldUserFieldType> types )
    {
        for ( OldUserFieldType type : types )
        {
            this.fields.removeAll( type );
        }
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

        OldUserFields that = (OldUserFields) o;

        if ( !fields.equals( that.fields ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return fields.hashCode();
    }

    public OldUserFields setTitle( String value )
    {
        add( new OldUserField( OldUserFieldType.TITLE, value ) );
        return this;
    }

    public String getPrefix()
    {
        return getUserFieldValueAsString( OldUserFieldType.PREFIX );
    }

    public String getSuffix()
    {
        return getUserFieldValueAsString( OldUserFieldType.SUFFIX );
    }

    private String getUserFieldValueAsString( OldUserFieldType type )
    {
        final OldUserField userField = getField( type );
        if ( userField == null )
        {
            return null;
        }
        return userField.getValueAsString();
    }
}
