package com.enonic.xp.core.impl.content.schema;

import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;

final class ContentTypeRegistry
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentTypeRegistry.class );

    private final BuiltinContentTypes builtInTypes;

    private final ContentTypeLoader contentTypeLoader;

    private final ApplicationService applicationService;

    ContentTypeRegistry( final ContentTypeLoader contentTypeLoader, final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
        this.builtInTypes = new BuiltinContentTypes();
        this.contentTypeLoader = contentTypeLoader;
    }

    public ContentType get( final ContentTypeName name )
    {
        return SchemaHelper.isSystem( name.getApplicationKey() ) ? this.builtInTypes.getContentType( name ) : loadOrNull( name );
    }

    public ContentTypes getByApplication( final ApplicationKey key )
    {
        if ( SchemaHelper.isSystem( key ) )
        {
            return builtInTypes.getAll()
                .stream()
                .filter( type -> type.getName().getApplicationKey().equals( key ) )
                .collect( ContentTypes.collector() );
        }
        else
        {
            return loadByApplication( key ).collect( ContentTypes.collector() );
        }
    }

    public ContentTypes getAll()
    {
        final ApplicationKeys applicationKeys = applicationService.list().getApplicationKeys();
        return Stream.concat( builtInTypes.getAll().stream(), applicationKeys.stream().flatMap( this::loadByApplication ) )
            .collect( ContentTypes.collector() );
    }

    private Stream<ContentType> loadByApplication( final ApplicationKey key )
    {
        return contentTypeLoader.findNames( key ).stream().map( this::loadOrNull ).filter( Objects::nonNull );
    }

    private ContentType loadOrNull( final ContentTypeName name )
    {
        try
        {
            return contentTypeLoader.get( name );
        }
        catch ( final Exception e )
        {
            LOG.error( "Error loading content type: '{}'", name, e );
            return null;
        }
    }
}
