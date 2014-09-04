package com.enonic.wem.core.schema;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.schema.BaseSchema;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.xml.XmlSerializers;
import com.enonic.wem.core.schema.mixin.MixinXmlSerializer;
import com.enonic.wem.core.support.dao.IconDao;

import static java.util.stream.Collectors.toList;

public final class SchemaLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( SchemaLoader.class );

    private final static Pattern PATTERN = Pattern.compile( "schema/([^/]+)/schema\\.xml" );

    private static final String SCHEMA_XML = "schema.xml";

    private static final String SCHEMA_DIR = "schema";

    private final IconDao iconDao = new IconDao();

    public Schemas loadSchemas( final Module module )
    {
        final ModuleKey moduleKey = module.getKey();
        final ResourceKey schemaDir = findSchemaDirectory( moduleKey );
        if ( schemaDir == null )
        {
            return Schemas.empty();
        }

        final List<String> schemaNames = getSchemaNames( module );

        final List<Schema> schemas = schemaNames.stream().
            map( ( schemaName ) -> loadSchema( moduleKey, schemaName ) ).
            filter( Objects::nonNull ).
            collect( toList() );

        return Schemas.from( schemas );
    }


    private Schema loadSchema( final ModuleKey moduleKey, final String schemaName )
    {
        final ResourceKey schemaFolderKey = ResourceKey.from( moduleKey, SCHEMA_DIR + "/" + schemaName );
        final ResourceKey schemaXmlKey = schemaFolderKey.resolve( SCHEMA_XML );
        final Resource schemaResource = Resource.from( schemaXmlKey );
        if ( schemaResource.exists() )
        {
            try
            {
                final String serializedSchema = schemaResource.readString();

                final BaseSchema.Builder schemaBuilder = parseSchemaXml( serializedSchema );
                final Instant modifiedTime = Instant.now();
                schemaBuilder.modifiedTime( modifiedTime );
                schemaBuilder.createdTime( modifiedTime );
                schemaBuilder.icon( iconDao.readIcon( schemaFolderKey ) );

                if ( schemaBuilder instanceof ContentType.Builder )
                {
                    final ContentType.Builder contentTypeBuilder = (ContentType.Builder) schemaBuilder;
                    return contentTypeBuilder.name( ContentTypeName.from( moduleKey, schemaName ) ).build();
                }
                else if ( schemaBuilder instanceof Mixin.Builder )
                {
                    final Mixin.Builder mixinBuilder = (Mixin.Builder) schemaBuilder;
                    return mixinBuilder.name( MixinName.from( moduleKey, schemaName ) ).build();
                }
                else if ( schemaBuilder instanceof RelationshipType.Builder )
                {
                    final RelationshipType.Builder relationshipTypeBuilder = (RelationshipType.Builder) schemaBuilder;
                    return relationshipTypeBuilder.name( RelationshipTypeName.from( moduleKey, schemaName ) ).build();
                }
            }
            catch ( Exception e )
            {
                LOG.warn( "Could not load schema from [" + schemaResource + "]", e );
            }
        }
        return null;
    }

    private List<String> getSchemaNames( final Module module )
    {
        return module.getResourcePaths().stream().
            map( PATTERN::matcher ).
            filter( Matcher::matches ).
            map( matcher -> matcher.group( 1 ) ).
            collect( toList() );
    }

    private ResourceKey findSchemaDirectory( final ModuleKey moduleKey )
    {
        final ResourceKey key = ResourceKey.from( moduleKey, SCHEMA_DIR );
        final Resource resource = Resource.from( key );

        if ( resource.exists() )
        {
            return key;
        }

        return null;
    }

    final BaseSchema.Builder parseSchemaXml( final String serializedSchema )
    {
        // TODO improve detection of schema type
        try
        {
            return parseContentTypeXml( serializedSchema );
        }
        catch ( Exception e )
        {
            // DO NOTHING
        }
        try
        {
            return parseRelationshipTypeXml( serializedSchema );
        }
        catch ( Exception e )
        {
            // DO NOTHING
        }
        try
        {
            return parseMixinXml( serializedSchema );
        }
        catch ( Exception e )
        {
            // DO NOTHING
        }
        return null;
    }

    private ContentType.Builder parseContentTypeXml( final String serializedContentType )
    {
        final ContentType.Builder builder = ContentType.newContentType();
        XmlSerializers.contentType().parse( serializedContentType ).to( builder );
        return builder;
    }

    private RelationshipType.Builder parseRelationshipTypeXml( final String serializedRelationshipType )
    {
        final RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        XmlSerializers.relationshipType().parse( serializedRelationshipType ).to( builder );
        return builder;
    }

    private Mixin.Builder parseMixinXml( final String serializedMixin )
    {
        final MixinXmlSerializer xmlSerializer = new MixinXmlSerializer();
        return xmlSerializer.toMixinBuilder( serializedMixin );
    }
}
