package com.enonic.xp.script.impl.executor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.module.Module;

public class ScriptExecutorImplTest
{
    private ScriptExecutorFactoryImpl factory;

    private Module module;

    @Before
    public void setup()
    {
        this.factory = new ScriptExecutorFactoryImpl();
        this.module = Mockito.mock( Module.class );
        Mockito.when( this.module.getClassLoader() ).thenReturn( getClass().getClassLoader() );
    }

    @Test
    public void create()
    {
        final ScriptExecutor executor = this.factory.newExecutor( this.module );
        Assert.assertNotNull( executor );
    }
}
