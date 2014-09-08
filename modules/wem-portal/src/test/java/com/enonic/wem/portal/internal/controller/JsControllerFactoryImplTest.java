package com.enonic.wem.portal.internal.controller;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptService;

import static org.junit.Assert.*;

public class JsControllerFactoryImplTest
{
    @Test
    public void testNewController()
    {
        final JsControllerFactoryImpl factory = new JsControllerFactoryImpl();
        factory.postProcessor = Mockito.mock( PostProcessor.class );
        factory.scriptService = Mockito.mock( ScriptService.class );

        final ScriptExports exports = Mockito.mock( ScriptExports.class );
        Mockito.when( factory.scriptService.execute( Mockito.any() ) ).thenReturn( exports );

        final JsController controller = factory.newController();
        assertNotNull( controller );
    }
}
