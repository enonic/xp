package com.enonic.xp.web.servlet;

import com.enonic.xp.exception.BaseException;

/**
 * Created by gri on 12/08/15.
 */
public class OutOfScopeException
    extends BaseException
{
    public OutOfScopeException( final String message )
    {
        super( message );
    }
}
