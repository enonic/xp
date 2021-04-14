package com.enonic.xp.core.impl.schema.content;

import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;

final class ContentTypeRegistry
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentTypeRegistry.class );

    private final BuiltinContentTypes builtInTypes;

    private final ContentTypeLoader contentTypeLoader;

    private final ApplicationService applicationService;

    public ContentTypeRegistry( final ResourceService resourceService, final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
        this.builtInTypes = new BuiltinContentTypes();
        this.contentTypeLoader = new ContentTypeLoader( resourceService );
    }

    public ContentType get( final ContentTypeName name )
    {
        if ( SchemaHelper.isSystem( name.getApplicationKey() ) )
        {
            return this.builtInTypes.getContentType( name );
        }

        try
        {
            return contentTypeLoader.get( name );
        }
        catch ( final Exception e )
        {
            LOG.error( "Error loading content type: '" + name + "'", e );
            return null;
        }
    }

    public ContentTypes getByApplication( final ApplicationKey key )
    {
        return ContentTypes.from( namesStream( ApplicationKeys.from( key ) ).map( this::get )
                                      .filter( Objects::nonNull )
                                      .collect( ImmutableList.toImmutableList() ) );
    }

    public ContentTypes getAll()
    {
        return ContentTypes.from( Stream.concat( this.builtInTypes.getAll().stream(),
                                                 namesStream( this.applicationService.getInstalledApplicationKeys() ).map( this::get ) )
                                      .filter( Objects::nonNull )
                                      .collect( ImmutableList.toImmutableList() ) );
    }

    private Stream<ContentTypeName> namesStream( final ApplicationKeys applicationKeys )
    {
        return applicationKeys.stream()
            .flatMap( key -> SchemaHelper.isSystem( key ) ? builtInTypes.getAll()
                .stream()
                .filter( type -> type.getName().getApplicationKey().equals( key ) )
                .map( BaseSchema::getName ) : contentTypeLoader.findNames( key ).stream() );
    }
}
