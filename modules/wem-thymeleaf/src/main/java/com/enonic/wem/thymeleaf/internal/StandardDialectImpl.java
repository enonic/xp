package com.enonic.wem.thymeleaf.internal;

import org.thymeleaf.standard.StandardDialect;

final class StandardDialectImpl
    extends StandardDialect
{
    public StandardDialectImpl()
    {
        getExecutionAttributes().put( "hello", "HI" );
    }
}
