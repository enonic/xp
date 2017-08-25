package com.enonic.xp.testkit;

import java.util.List;
import java.util.function.Consumer;

import org.junit.Assert;

import com.google.common.collect.Lists;

import com.enonic.xp.script.impl.executor.ScriptExecutor;

final class ScriptTestInstance
    implements ScriptTestApi
{
    private ScriptExecutor executor;

    private Consumer<Object[]> before;

    private Consumer<Object[]> after;

    private final List<ScriptTestMethod> testMethods;

    ScriptTestInstance()
    {
        this.testMethods = Lists.newArrayList();
    }

    List<ScriptTestMethod> getTestMethods()
    {
        return this.testMethods;
    }

    void setExecutor( final ScriptExecutor executor )
    {
        this.executor = executor;
    }

    @Override
    public void before( final Consumer<Object[]> callback )
    {
        this.before = callback;
    }

    @Override
    public void after( final Consumer<Object[]> callback )
    {
        this.after = callback;
    }

    @Override
    public void test( final String name, final Consumer<Object[]> callback )
    {
        this.testMethods.add( new ScriptTestMethod( name, callback ) );
    }

    void runBefore( final Object... args )
    {
        if ( this.before != null )
        {
            this.before.accept( args );
        }
    }

    void runAfter( final Object... args )
    {
        if ( this.after != null )
        {
            this.after.accept( args );
        }
    }

    @Override
    public void assertEquals( final Object expected, final Object actual, final String message )
    {
        if ( ( expected instanceof Number ) && ( actual instanceof Number ) )
        {
            Assert.assertEquals( message, ( (Number) expected ).doubleValue(), ( (Number) actual ).doubleValue(), 0 );
        }
        else
        {
            Assert.assertEquals( message, expected, actual );
        }
    }

    @Override
    public void assertNotEquals( final Object expected, final Object actual, final String message )
    {
        if ( ( expected instanceof Number ) && ( actual instanceof Number ) )
        {
            Assert.assertNotEquals( message, ( (Number) expected ).doubleValue(), ( (Number) actual ).doubleValue(), 0 );
        }
        else
        {
            Assert.assertNotEquals( message, expected, actual );
        }
    }

    @Override
    public void assertTrue( final boolean actual, final String message )
    {
        Assert.assertTrue( message, actual );
    }

    @Override
    public void assertFalse( final boolean actual, final String message )
    {
        Assert.assertFalse( message, actual );
    }

    void dispose()
    {
        this.executor.runDisposers();
    }
}
