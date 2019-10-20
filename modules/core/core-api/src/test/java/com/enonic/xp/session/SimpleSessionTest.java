package com.enonic.xp.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class SimpleSessionTest
{
    private static final class SampleValue
    {
    }

    private Session session;

    @BeforeEach
    public void setUp()
    {
        this.session = new SimpleSession( SessionKey.from( "123" ) );
    }

    @Test
    public void testKey()
    {
        final SessionKey key = this.session.getKey();
        assertNotNull( key );
        assertEquals( "123", key.toString() );
    }

    @Test
    public void testAttributeByKey()
    {
        assertEquals( 0, this.session.getAttributes().size() );

        this.session.setAttribute( "key1", "value1" );
        assertEquals( "value1", this.session.getAttribute( "key1" ) );
        assertEquals( 1, this.session.getAttributes().size() );

        this.session.removeAttribute( "key1" );
        assertNull( this.session.getAttribute( "key1" ) );
        assertEquals( 0, this.session.getAttributes().size() );
    }

    @Test
    public void testAttributeByType()
    {
        assertEquals( 0, this.session.getAttributes().size() );

        final SampleValue value = new SampleValue();
        this.session.setAttribute( value );
        assertSame( value, this.session.getAttribute( SampleValue.class ) );
        assertEquals( 1, this.session.getAttributes().size() );

        this.session.removeAttribute( SampleValue.class );
        assertNull( this.session.getAttribute( SampleValue.class ) );
        assertEquals( 0, this.session.getAttributes().size() );
    }
}
