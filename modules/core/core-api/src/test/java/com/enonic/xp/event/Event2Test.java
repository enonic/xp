package com.enonic.xp.event;

import org.junit.Test;

import com.enonic.xp.content.ContentId;

import static org.junit.Assert.*;

public class Event2Test
{
    @Test
    public void testBuilder()
    {
        final Event2 event = this.createTestEvent();

        assertEquals( "type", event.getType() );
        assertEquals( true, event.isDistributed() );
        assertNotNull( event.getData() );
        assertTrue( event.hasValue( "key1" ) );
        assertTrue( event.hasValue( "key2" ) );
        assertEquals( "val1", event.getValue( "key1" ).get() );
        assertEquals( "val2", event.getValue( "key2" ).get() );
    }

    @Test
    public void testToString()
    {
        final Event2 event = this.createTestEvent();

        final String result = event.toString();

        assertTrue( result.contains( "type=type" ) );
        assertTrue( result.contains( "distributed=true" ) );
        assertTrue( result.contains( "data={key1=val1, key2=val2}" ) );
    }

    @Test
    public void testClone()
    {
        final Event2 event = this.createTestEvent();
        final Event2 clonedEvent = Event2.create( event ).build();

        assertNotEquals( event, clonedEvent );
        assertNotEquals( event.getTimestamp(), clonedEvent.getTimestamp() );
        assertEquals( event.getType(), clonedEvent.getType() );
        assertEquals( event.isDistributed(), clonedEvent.isDistributed() );
        assertEquals( event.getData(), clonedEvent.getData() );
    }

    @Test
    public void testDataValues()
    {
        final Event2 testEvent = Event2.create( "type" ).
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
    public void testGetValueAs()
    {
        final Event2 testEvent = Event2.create( "type" ).
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
    public void testGetNullValues()
    {
        final Event2 testEvent = Event2.create( "type" ).
            value( "key1", "val1" ).
            build();

        assertTrue( testEvent.getValue( "key1" ).isPresent() );
        assertFalse( testEvent.getValue( "key2" ).isPresent() );
    }

    @Test
    public void testSubTypes()
    {
        final Event2 testEvent = Event2.create( "type1.type2.type3.type4" ).build();

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

    private Event2 createTestEvent()
    {
        return Event2.create( "type" ).
            distributed( true ).
            value( "key1", "val1" ).
            value( "key2", "val2" ).
            build();
    }
}
