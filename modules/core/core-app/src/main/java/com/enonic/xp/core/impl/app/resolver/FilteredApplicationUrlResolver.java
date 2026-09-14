package com.enonic.xp.core.impl.app.resolver;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.resource.Resource;

public final class FilteredApplicationUrlResolver
    implements ApplicationUrlResolver
{
    private final ApplicationUrlResolver delegate;

    private final Supplier<Predicate<String>> includeSupplier;

    public FilteredApplicationUrlResolver( final ApplicationUrlResolver delegate, final Supplier<Predicate<String>> includeSupplier )
    {
        this.delegate = delegate;
        this.includeSupplier = includeSupplier;
    }

    @Override
    public Set<String> findFiles()
    {
        final Predicate<String> include = includeSupplier.get();
        return delegate.findFiles().stream().filter( include ).collect( Collectors.toCollection( LinkedHashSet::new ) );
    }

    @Override
    public Resource findResource( final String path )
    {
        return includeSupplier.get().test( path ) ? delegate.findResource( path ) : null;
    }
}
