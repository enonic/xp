package com.enonic.xp.script.graaljs.impl.value;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.script.ScriptValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScriptValueFactoryImplTest
{
    private Context context;

    @BeforeEach
    public void setUp()
    {
        this.context = Context.newBuilder( "js" ).allowAllAccess( true ).build();
    }

    @AfterEach
    public void destroy()
    {
        context.close();
    }

    @Test
    public void test()
    {
        ScriptValueFactory factory = new ScriptValueFactoryImpl();

        ScriptValue value = factory.newValue( "2" );
        assertNotNull( value );
        assertEquals( "2", value.getValue( String.class ) );
        assertEquals( "2", value.getValue() );

        Value arrayValue = context.eval( "js", "var array = [1,2,3]; array;" );
        ScriptValue scriptValue = factory.newValue( arrayValue );

        assertNotNull( scriptValue );
        assertTrue( scriptValue.isArray() );
    }
}
