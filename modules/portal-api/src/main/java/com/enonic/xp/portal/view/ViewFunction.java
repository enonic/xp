package com.enonic.xp.portal.view;

public interface ViewFunction
{
    public String getName();

    public Object execute( ViewFunctionParams params );
}
