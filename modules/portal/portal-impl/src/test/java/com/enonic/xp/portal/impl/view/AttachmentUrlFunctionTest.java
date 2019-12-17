package com.enonic.xp.portal.impl.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttachmentUrlFunctionTest
    extends AbstractUrlViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        final AttachmentUrlFunction function = new AttachmentUrlFunction();
        function.setUrlService( createUrlService() );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "attachmentUrl", "_name=test" );
        assertEquals( "AttachmentUrlParams{type=server, params={}, name=test, download=false}", result );
    }
}
