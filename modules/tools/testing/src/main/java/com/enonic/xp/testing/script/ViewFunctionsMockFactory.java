package com.enonic.xp.testing.script;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;

final class ViewFunctionsMockFactory
{
    public ViewFunctionService newService()
    {
        return Mockito.mock( ViewFunctionService.class, (Answer) this::urlAnswer );
    }

    private Object urlAnswer( final InvocationOnMock invocation )
    {
        final ViewFunctionParams params = (ViewFunctionParams) invocation.getArguments()[0];
        return params.getName() + "(" + params.getArgs().toString() + ")";
    }
}
