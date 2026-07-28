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

    public ScriptRunnerSupport()
    {
        super();
    }

    public ScriptRunnerSupport( final boolean http )
    {
        super( http );
    }

    public abstract String getScriptTestFile();

    @TestFactory
    List<DynamicTest> js()
    {
        return findTestNames().stream().map( name -> dynamicTest( name, () -> {
            ScriptExports exports = this.runScript( this.getScriptTestFile() );
            executeFunction( exports, "before" );
            executeFunction( exports, name );
            executeFunction( exports, "after" );
        } ) ).collect( Collectors.toList() );
    }

    /**
     * Discovers the exported test functions on the harness the {@code @BeforeEach} lifecycle has
     * already set up, and leaves it running: the dynamic tests are executed after this method
     * returns and share this instance, so a script executor torn down here would be torn down for
     * them too — engines that close their script contexts reject every later execution.
     */
    private Set<String> findTestNames()
    {
        return this.runScript( this.getScriptTestFile() )
            .getValue()
            .getKeys()
            .stream()
            .filter( name -> name.startsWith( "test" ) )
            .collect( Collectors.toSet() );
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
