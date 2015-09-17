package com.enonic.xp.portal.impl.view;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.portal.url.PortalUrlService;

public abstract class AbstractUrlViewFunctionTest
    extends AbstractViewFunctionTest
{
    protected final PortalUrlService createUrlService()
    {
        return Mockito.mock( PortalUrlService.class, (Answer) this::urlAnswer );
    }

    private Object urlAnswer( final InvocationOnMock invocation )
        throws Exception
    {
        return invocation.getArguments()[0].toString();
    }
}
