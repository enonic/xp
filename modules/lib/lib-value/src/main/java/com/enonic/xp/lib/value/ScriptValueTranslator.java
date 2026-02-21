package com.enonic.xp.lib.value;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

public class ScriptValueTranslator
{
    private final PropertyTree propertyTree = new PropertyTree();

    private final BinaryAttachments.Builder binaryAttachmentsBuilder = BinaryAttachments.create();

    private final boolean includeBinaryAttachments;

    public ScriptValueTranslator()
    {
        this( true );
    }

    public ScriptValueTranslator( final boolean includeBinaryAttachments )
    {
        this.includeBinaryAttachments = includeBinaryAttachments;
    }

    public ScriptValueTranslatorResult create( final ScriptValue value )
    {
        final Map<String, Object> map = value.getMap();

        handleMap( this.propertyTree.getRoot(), map );

        return new ScriptValueTranslatorResult( this.propertyTree, this.binaryAttachmentsBuilder.build() );
    }

    private void handleElement( final PropertySet parent, final String name, final Object value )
    {
        if ( value == null )
        {
            return;
        }
        if ( value instanceof Map )
        {
            final PropertySet set = parent.addSet( name );
            handleMap( set, (Map) value );
        }
        else if ( value instanceof Collection )
        {
            handleArray( parent, name, (Collection) value );
        }
        else
        {
            handleValue( parent, name, value );
        }
    }

    private void handleMap( final PropertySet parent, final Map map )
    {
        map.forEach( ( key, value ) -> handleElement( parent, key.toString(), value ) );
    }

    private void handleArray( final PropertySet parent, final String name, final Collection values )
    {
        for ( final Object value : values )
        {
            handleElement( parent, name, value );
        }
    }

    private void handleValue( final PropertySet parent, final String name, final Object value )
    {
        if ( value instanceof Instant )
        {
            parent.addInstant( name, (Instant) value );
        }
        else if ( value instanceof GeoPoint )
        {
            parent.addGeoPoint( name, (GeoPoint) value );
        }
        else if ( value instanceof Double )
        {
            parent.addDouble( name, (Double) value );
        }
        else if ( value instanceof Float )
        {
            parent.addDouble( name, ( (Float) value ).doubleValue() );
        }
        else if ( value instanceof Integer )
        {
            parent.addLong( name, ( (Integer) value ).longValue() );
        }
        else if ( value instanceof Byte )
        {
            parent.addLong( name, ( (Byte) value ).longValue() );
        }
        else if ( value instanceof Long )
        {
            parent.addLong( name, ( (Long) value ) );
        }
        else if ( value instanceof Number )
        {
            parent.addDouble( name, ( (Number) value ).doubleValue() );
        }
        else if ( value instanceof Boolean )
        {
            parent.addBoolean( name, (Boolean) value );
        }
        else if ( value instanceof LocalDateTime )
        {
            parent.addLocalDateTime( name, (LocalDateTime) value );
        }
        else if ( value instanceof LocalDate )
        {
            parent.addLocalDate( name, (LocalDate) value );
        }
        else if ( value instanceof LocalTime )
        {
            parent.addLocalTime( name, (LocalTime) value );
        }
        else if ( value instanceof Date )
        {
            parent.addInstant( name, ( (Date) value ).toInstant() );
        }
        else if ( value instanceof Reference )
        {
            parent.addReference( name, (Reference) value );
        }
        else if ( value instanceof BinaryReference )
        {
            parent.addBinaryReference( name, (BinaryReference) value );
        }
        else if ( value instanceof Link )
        {
            parent.addLink( name, (Link) value );
        }

        else if ( value instanceof BinaryAttachment )
        {
            final BinaryAttachment binaryAttachment = (BinaryAttachment) value;
            parent.addBinaryReference( name, binaryAttachment.getReference() );

            if ( includeBinaryAttachments )
            {
                this.binaryAttachmentsBuilder.add(
                    new BinaryAttachment( binaryAttachment.getReference(), binaryAttachment.getByteSource() ) );
            }
        }

        else
        {
            parent.addString( name, value.toString() );
        }
    }

}
