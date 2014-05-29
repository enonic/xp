package com.enonic.wem.portal.controller;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.portal.postprocess.PostProcessor;
import com.enonic.wem.portal.script.runner.ScriptRunnerFactory;

import static org.junit.Assert.*;

public class JsControllerFactoryImplTest
{
    @Test
    public void testNewController()
    {
        final JsControllerFactoryImpl factory = new JsControllerFactoryImpl();
        factory.setPostProcessor( Mockito.mock( PostProcessor.class ) );
        factory.setScriptRunnerFactory( Mockito.mock( ScriptRunnerFactory.class ) );

        final JsController controller = factory.newController();
        assertNotNull( controller );
    }
}
