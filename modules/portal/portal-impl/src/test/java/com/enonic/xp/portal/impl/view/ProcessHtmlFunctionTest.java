package com.enonic.xp.portal.impl.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcessHtmlFunctionTest
    extends AbstractUrlViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        final ProcessHtmlFunction function = new ProcessHtmlFunction();
        function.setUrlService( createUrlService() );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "processHtml", "_value=<html/>", "a=1", "b=2" );
        assertEquals( "ProcessHtmlParams{type=server, params={a=[1], b=[2]}, value=<html/>}", result );
    }
}
