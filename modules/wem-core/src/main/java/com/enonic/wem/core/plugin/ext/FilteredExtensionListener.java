package com.enonic.wem.core.plugin.ext;


import com.enonic.wem.api.plugin.ext.Extension;

public abstract class FilteredExtensionListener<T extends Extension>
    implements ExtensionListener
{
    private final Class<T> type;

    public FilteredExtensionListener( final Class<T> type )
    {
        this.type = type;
    }

    public final Class<T> getType()
    {
        return this.type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void extensionAdded( final Extension ext )
    {
        if ( isOfType( ext ) )
        {
            addExtension( (T) ext );
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void extensionRemoved( final Extension ext )
    {
        if ( isOfType( ext ) )
        {
            removeExtension( (T) ext );
        }
    }

    private boolean isOfType( final Extension ext )
    {
        return this.type.isAssignableFrom( ext.getClass() );
    }

    protected abstract void addExtension( T ext );

    protected abstract void removeExtension( T ext );
}
