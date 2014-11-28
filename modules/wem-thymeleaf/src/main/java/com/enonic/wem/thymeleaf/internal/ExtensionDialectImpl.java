package com.enonic.wem.thymeleaf.internal;

import java.util.Set;

import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import com.google.common.collect.Sets;

final class ExtensionDialectImpl
    extends StandardDialect
{
    public ExtensionDialectImpl()
    {
        final Set<IProcessor> processors = Sets.newHashSet();
        processors.add( new ComponentProcessor() );
        setAdditionalProcessors( processors );
    }

    @Override
    public String getPrefix()
    {
        return "wem";
    }
}
