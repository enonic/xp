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
        final PropertySet root = this.propertyTree.getRoot();
        map.forEach( ( k, v ) -> handleElement( root, k, v ) );

        return new ScriptValueTranslatorResult( this.propertyTree, this.binaryAttachmentsBuilder.build() );
    }

    private void handleElement( final PropertySet parent, final String name, final Object value )
    {
        if ( value instanceof Map<?, ?> map )
        {
            final PropertySet innerSet = parent.addSet( name );
            map.forEach( ( k, v ) -> handleElement( innerSet, k.toString(), v ) );
        }
        else if ( value instanceof Collection<?> coll )
        {
            coll.forEach( v -> handleElement( parent, name, v ) );
        }
        else
        {
            handleValue( parent, name, value );
        }
    }

    private void handleValue( final PropertySet parent, final String name, final Object value )
    {
        switch ( value )
        {
            case null ->
            {
            }
            case Double v -> parent.addDouble( name, v );
            case Float v -> parent.addDouble( name, v.doubleValue() );
            case Integer i -> parent.addLong( name, i.longValue() );
            case Byte b -> parent.addLong( name, b.longValue() );
            case Long l -> parent.addLong( name, l );
            case Number number -> parent.addDouble( name, number.doubleValue() );
            case Boolean b -> parent.addBoolean( name, b );
            case LocalDateTime localDateTime -> parent.addLocalDateTime( name, localDateTime );
            case LocalDate localDate -> parent.addLocalDate( name, localDate );
            case LocalTime localTime -> parent.addLocalTime( name, localTime );
            case Date date -> parent.addInstant( name, date.toInstant() );
            case Instant instant -> parent.addInstant( name, instant );
            case Reference reference -> parent.addReference( name, reference );
            case BinaryReference binaryReference -> parent.addBinaryReference( name, binaryReference );
            case Link link -> parent.addLink( name, link );
            case GeoPoint geoPoint -> parent.addGeoPoint( name, geoPoint );
            case BinaryAttachment binaryAttachment ->
            {
                parent.addBinaryReference( name, binaryAttachment.getReference() );

                if ( includeBinaryAttachments )
                {
                    this.binaryAttachmentsBuilder.add(
                        new BinaryAttachment( binaryAttachment.getReference(), binaryAttachment.getByteSource() ) );
                }
            }
            default -> parent.addString( name, value.toString() );
        }
    }

}
