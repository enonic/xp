package com.enonic.xp.testing;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.Statement;

import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

public final class ScriptRunner
    extends ParentRunner<String>
{
    private final ScriptExports exports;

    private final ScriptRunnerSupport testInstance;

    public ScriptRunner( final Class<?> testClass )
        throws Exception
    {
        super( testClass );
        this.testInstance = createTestInstance();
        this.exports = executeTestScript();
    }

    @Override
    protected List<String> getChildren()
    {
        return this.exports.getValue().getKeys().stream().
            filter( name -> name.startsWith( "test" ) ).
            collect( Collectors.toList() );
    }

    @Override
    protected Description describeChild( final String child )
    {
        return Description.createSuiteDescription( child );
    }

    @Override
    protected void runChild( final String child, final RunNotifier notifier )
    {
        final Description description = describeChild( child );

        Statement statement = functionInvoker( child );
        statement = withBefore( statement );
        statement = withAfter( statement );

        runLeaf( statement, description, notifier );
    }

    private Statement functionInvoker( final String name )
    {
        return new Statement()
        {
            @Override
            public void evaluate()
                throws Throwable
            {
                executeFunction( name );
            }
        };
    }

    private Statement withBefore( final Statement next )
    {
        return new Statement()
        {
            @Override
            public void evaluate()
                throws Throwable
            {
                executeBefore();
                next.evaluate();
            }
        };
    }

    private Statement withAfter( final Statement next )
    {
        return new Statement()
        {
            @Override
            public void evaluate()
                throws Throwable
            {
                try
                {
                    next.evaluate();
                }
                finally
                {
                    executeAfter();
                }
            }
        };
    }

    private void executeBefore()
    {
        executeFunction( "before" );
    }

    private void executeAfter()
    {
        executeFunction( "after" );
    }

    private void executeFunction( final String name )
    {
        final ScriptValue value = this.exports.getValue().getMember( name );
        if ( ( value != null ) && value.isFunction() )
        {
            value.call( this.testInstance );
        }
    }

    private ScriptRunnerSupport createTestInstance()
        throws Exception
    {
        final Object value = getTestClass().getOnlyConstructor().newInstance();
        if ( !( value instanceof ScriptRunnerSupport ) )
        {
            throw new IllegalArgumentException( "Test class must extend " + ScriptRunnerSupport.class.getName() );
        }

        return (ScriptRunnerSupport) value;
    }

    private ScriptExports executeTestScript()
    {
        try
        {
            this.testInstance.setup();
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }

        final String file = this.testInstance.getScriptTestFile();
        return this.testInstance.runScript( file );
    }
}
