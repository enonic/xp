package com.enonic.wem.guice.binder;

public interface ServiceBuilder<T>
{
    public ServiceBuilder<T> attribute( String key, Object value );

    public ServiceBuilder<T> filter( String filter );

    public void importSingle();

    public void importMultiple();

    public void export();

    public void exportAs( Class<? super T> iface );
}
