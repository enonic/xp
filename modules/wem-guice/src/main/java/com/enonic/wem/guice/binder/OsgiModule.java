package com.enonic.wem.guice.binder;

import com.google.inject.AbstractModule;

public abstract class OsgiModule
    extends AbstractModule
{
    protected final <T> ExportBuilder<T> exportService( final Class<T> type )
    {
        return new ExportBuilderImpl<>( binder(), type );
    }

    protected final <T> ImportBuilder<T> importService( final Class<T> type )
    {
        return new ImportBuilderImpl<>( binder(), type );
    }
}
