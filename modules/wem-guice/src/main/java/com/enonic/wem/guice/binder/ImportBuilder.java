package com.enonic.wem.guice.binder;

import com.google.inject.binder.ScopedBindingBuilder;

public interface ImportBuilder<T>
{
    public ImportBuilder<T> filter( String filter );

    public ScopedBindingBuilder toSingle();

    public ScopedBindingBuilder toMultiple();
}
