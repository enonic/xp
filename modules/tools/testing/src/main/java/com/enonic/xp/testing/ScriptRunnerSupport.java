package com.enonic.xp.testing;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public abstract class ScriptRunnerSupport
    extends ScriptTestSupport
{
    public abstract String getScriptTestFile();

    @TestFactory
    List<DynamicTest> js() throws Exception
    {
        return findTestNames().stream().map( name -> dynamicTest( name, () -> {
                ScriptExports exports = this.runScript( this.getScriptTestFile() );
                executeFunction( exports, "before" );
                executeFunction( exports, name );
                executeFunction( exports, "after" );
            } ) )
            .collect( Collectors.toList() );
    }

    private Set<String> findTestNames()
        throws Exception
    {
        this.initialize();
        try
        {
            return this.runScript( this.getScriptTestFile() ).getValue()
                .getKeys().stream()
                .filter( name -> name.startsWith( "test" ) ).collect( Collectors.toSet());
        }
        finally
        {
            this.deinitialize();
        }
    }

    private void executeFunction( final ScriptExports exports, final String name )
    {
        final ScriptValue value = exports.getValue().getMember( name );
        if ( ( value != null ) && value.isFunction() )
        {
            value.call( this );
        }
    }
}
