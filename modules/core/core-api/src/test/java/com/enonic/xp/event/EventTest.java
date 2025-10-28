package com.enonic.xp.event;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventTest
{
    @Test
    void testBuilder()
    {
        final Event event = this.createTestEvent();

        assertEquals( "type", event.getType() );
        assertEquals( true, event.isDistributed() );
        assertNotNull( event.getData() );
        assertTrue( event.hasValue( "key1" ) );
        assertTrue( event.hasValue( "key2" ) );
        assertEquals( "val1", event.getValue( "key1" ).get() );
        assertEquals( "val2", event.getValue( "key2" ).get() );
    }

    @Test
    void testToString()
    {
        final Event event = this.createTestEvent();

        final String result = event.toString();

        assertTrue( result.contains( "type=type" ) );
        assertTrue( result.contains( "distributed=true" ) );
        assertTrue( result.contains( "data={key1=val1, key2=val2}" ) );
    }

    @Test
    void testClone()
    {
        final Event event = this.createTestEvent();
        final Event clonedEvent = Event.create( event ).build();

        assertEquals( event.getType(), clonedEvent.getType() );
        assertEquals( event.isDistributed(), clonedEvent.isDistributed() );
        assertEquals( event.getData(), clonedEvent.getData() );
        assertEquals( event, clonedEvent );
    }

    @Test
    void testDataValues()
    {
        final Event testEvent = Event.create( "type" ).
            distributed( true ).
            value( "int1", 1 ).
            value( "long1", 10L ).
            value( "bool1", false ).
            value( "obj1", this.createTestEvent() ).
            build();

        assertTrue( testEvent.hasValue( "int1" ) );
        assertTrue( testEvent.hasValue( "long1" ) );
        assertTrue( testEvent.hasValue( "bool1" ) );
        assertTrue( testEvent.hasValue( "obj1" ) );
        assertTrue( testEvent.getValue( "int1" ).get() instanceof Integer );
        assertTrue( testEvent.getValue( "long1" ).get() instanceof Long );
        assertTrue( testEvent.getValue( "bool1" ).get() instanceof Boolean );
        assertTrue( testEvent.getValue( "obj1" ).get() instanceof String );
    }

    @Test
    void testGetValueAs()
    {
        final Event testEvent = Event.create( "type" ).
            value( "int1", 1 ).
            value( "long1", 10L ).
            value( "obj1", ContentId.from( "testId" ) ).
            build();

        assertTrue( testEvent.getValueAs( Double.class, "int1" ).get() != null );
        assertTrue( testEvent.getValueAs( Boolean.class, "long1" ).get() != null );
        assertTrue( testEvent.getValueAs( ContentId.class, "obj1" ).get() != null );
        assertFalse( testEvent.getValueAs( ContentId.class, "obj2" ).isPresent() );
    }

    @Test
    void testGetNullValues()
    {
        final Event testEvent = Event.create( "type" ).
            value( "key1", "val1" ).
            build();

        assertTrue( testEvent.getValue( "key1" ).isPresent() );
        assertFalse( testEvent.getValue( "key2" ).isPresent() );
    }

    @Test
    void testSubTypes()
    {
        final Event testEvent = Event.create( "type1.type2.type3.type4" ).build();

        assertTrue( testEvent.isSubType( "type1" ) );
        assertTrue( testEvent.isSubType( "type1.type2" ) );
        assertTrue( testEvent.isSubType( "type1.type2.type3" ) );
        assertFalse( testEvent.isSubType( "type1.type3" ) );
        assertFalse( testEvent.isSubType( "type2" ) );
        assertFalse( testEvent.isSubType( "type4" ) );
        assertFalse( testEvent.isSubType( "type2.type3.type4" ) );
        assertFalse( testEvent.isSubType( "type1.type2.type3.type4" ) );
        assertFalse( testEvent.isSubType( "type2.type3" ) );

        assertTrue( testEvent.isType( "type1.type2.type3.type4" ) );
        assertFalse( testEvent.isType( "type1.type2.type3" ) );
    }

    private Event createTestEvent()
    {
        return Event.create( "type" ).
            distributed( true ).
            value( "key1", "val1" ).
            value( "key2", "val2" ).
            build();
    }
}
