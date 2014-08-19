package com.enonic.wem.portal.controller;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.portal.postprocess.PostProcessor;
import com.enonic.wem.script.ScriptRunner;
import com.enonic.wem.script.ScriptRunnerFactory;

import static org.junit.Assert.*;

public class JsControllerFactoryImplTest
{
    @Test
    public void testNewController()
    {
        final JsControllerFactoryImpl factory = new JsControllerFactoryImpl();
        factory.postProcessor = Mockito.mock( PostProcessor.class );
        factory.scriptRunnerFactory = Mockito.mock( ScriptRunnerFactory.class );

        final ScriptRunner runner = Mockito.mock( ScriptRunner.class );
        Mockito.when( factory.scriptRunnerFactory.newRunner() ).thenReturn( runner );

        final JsController controller = factory.newController();
        assertNotNull( controller );
    }
}
