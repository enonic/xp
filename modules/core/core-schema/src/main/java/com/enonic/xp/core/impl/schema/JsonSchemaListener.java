package com.enonic.xp.core.impl.schema;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class JsonSchemaListener
    implements SynchronousBundleListener
{
    private final JsonSchemaService schemaService;

    private final BundleContext bundleContext;

    @Activate
    public JsonSchemaListener( @Reference final JsonSchemaService schemaService, final BundleContext bundleContext )
    {
        this.schemaService = schemaService;
        this.bundleContext = bundleContext;
    }

    @Activate
    public void activate()
    {
        bundleContext.addBundleListener( this );

        final FormItemsJsonSchemaGenerator formItemsJsonSchemaGenerator = new FormItemsJsonSchemaGenerator( Set.of() );
        final String formItemsSchema = formItemsJsonSchemaGenerator.generate();
        schemaService.registerSchema( formItemsSchema );

        final Bundle currentBundle = bundleContext.getBundle();
        loadJsomSchemas( currentBundle );

        for ( Bundle bundle : bundleContext.getBundles() )
        {
            if ( bundle.getBundleContext() != null && bundle.getBundleId() != currentBundle.getBundleId() )
            {
                loadJsomSchemas( bundle );
            }
        }

        schemaService.refreshSchemaRegistry();
    }

    @Deactivate
    public void deactivate()
    {
        bundleContext.removeBundleListener( this );
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        switch ( event.getType() )
        {
            case BundleEvent.INSTALLED:
            case BundleEvent.UPDATED:
                addBundle( event.getBundle() );
                break;
            default:
                break;
        }
    }

    private void addBundle( Bundle bundle )
    {
        if ( loadJsomSchemas( bundle ) )
        {
            schemaService.refreshSchemaRegistry();
        }
    }

    private boolean loadJsomSchemas( final Bundle bundle )
    {
        return schemaService.loadSchemas( loadJsonSchemasFromBundle( bundle ) );
    }

    private List<URL> loadJsonSchemasFromBundle( final Bundle bundle )
    {
        final Enumeration<URL> schemaURLs = bundle.findEntries( "/META-INF/schemas", "*.json", true );
        return schemaURLs != null ? Collections.list( schemaURLs ) : Collections.emptyList();
    }
}
