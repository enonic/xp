package com.enonic.xp.script.impl.util;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openjdk.nashorn.api.scripting.JSObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavascriptHelperFactoryTest
{
    private ScriptEngine engine;

    private JavascriptHelper<Bindings> javascriptHelper;

    @BeforeEach
    void setUp()
    {
        this.engine = NashornHelper.getScriptEngine( getClass().getClassLoader() );
        this.javascriptHelper = new JavascriptHelperFactory( this.engine ).create();
    }

    @Test
    void defineDataProperty()
        throws Exception
    {
        final Bindings object = javascriptHelper.newJsObject();

        javascriptHelper.defineDataProperty( object, "key", "value" );

        assertEquals( "value", object.get( "key" ) );
    }

    @Test
    void defineDataProperty_protoKeyBecomesOwnProperty()
        throws Exception
    {
        final Bindings object = javascriptHelper.newJsObject();
        final Bindings array = javascriptHelper.newJsArray();

        javascriptHelper.defineDataProperty( object, "__proto__", array );
        javascriptHelper.defineDataProperty( object, "key", "value" );

        final JSObject check = (JSObject) engine.eval( "(function (o, a) { return Object.getPrototypeOf(o) === Object.prototype" +
                                                           " && Object.prototype.hasOwnProperty.call(o, '__proto__') && o.__proto__ === a" +
                                                           " && Object.keys(o).join() === '__proto__,key' && o.key === 'value'; })" );
        assertTrue( (Boolean) check.call( null, object, array ) );
    }
}
