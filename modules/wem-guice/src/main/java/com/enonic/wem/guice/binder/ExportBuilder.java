package com.enonic.wem.guice.binder;

import java.util.Map;

import com.google.inject.binder.ScopedBindingBuilder;

public interface ExportBuilder<T>
{
    public ExportBuilder<T> property( String key, Object value );

    public ExportBuilder<T> properties( Map<String, Object> properties );

    public ScopedBindingBuilder to( Class<? extends T> type );

    public void toInstance( T instace );
}
