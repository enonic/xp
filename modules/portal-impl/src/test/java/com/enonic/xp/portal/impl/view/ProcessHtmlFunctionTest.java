package com.enonic.xp.portal.impl.view;

import org.junit.Test;

import static org.junit.Assert.*;

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
        assertEquals( "ProcessHtmlParams{params={a=[1], b=[2]}, value=<html/>}", result );
    }
}
