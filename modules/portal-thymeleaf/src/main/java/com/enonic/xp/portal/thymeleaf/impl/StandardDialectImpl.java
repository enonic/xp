package com.enonic.xp.portal.thymeleaf.impl;

import org.thymeleaf.standard.StandardDialect;

final class StandardDialectImpl
    extends StandardDialect
{
    public StandardDialectImpl()
    {
        getExecutionAttributes().put( "hello", "HI" );
    }
}
