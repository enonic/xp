package com.enonic.xp.lib.thymeleaf;

import org.thymeleaf.standard.StandardDialect;

final class StandardDialectImpl
    extends StandardDialect
{
    public StandardDialectImpl()
    {
        getExecutionAttributes().put( "hello", "HI" );
    }
}
