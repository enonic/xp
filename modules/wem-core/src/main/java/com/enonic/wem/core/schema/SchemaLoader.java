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
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.xml.mapper.XmlContentTypeMapper;
import com.enonic.wem.api.xml.mapper.XmlMetadataSchemaMapper;
import com.enonic.wem.api.xml.mapper.XmlMixinMapper;
import com.enonic.wem.api.xml.mapper.XmlRelationshipTypeMapper;
import com.enonic.wem.api.xml.model.XmlContentType;
import com.enonic.wem.api.xml.model.XmlMetadataSchema;
import com.enonic.wem.api.xml.model.XmlMixin;
import com.enonic.wem.api.xml.model.XmlRelationshipType;
import com.enonic.wem.api.xml.serializer.XmlSerializers2;
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
                    final ContentType.Builder builder = (ContentType.Builder) schemaBuilder;
                    return builder.name( ContentTypeName.from( moduleKey, schemaName ) ).build();
                }
                else if ( schemaBuilder instanceof Mixin.Builder )
                {
                    final Mixin.Builder builder = (Mixin.Builder) schemaBuilder;
                    return builder.name( MixinName.from( moduleKey, schemaName ) ).build();
                }
                else if ( schemaBuilder instanceof RelationshipType.Builder )
                {
                    final RelationshipType.Builder builder = (RelationshipType.Builder) schemaBuilder;
                    return builder.name( RelationshipTypeName.from( moduleKey, schemaName ) ).build();
                }
                else if ( schemaBuilder instanceof MetadataSchema.Builder )
                {
                    final MetadataSchema.Builder builder = (MetadataSchema.Builder) schemaBuilder;
                    return builder.name( MetadataSchemaName.from( moduleKey, schemaName ) ).build();
                }
            }
            catch ( Exception e )
            {
                LOG.warn( "Could not load schema [" + schemaXmlKey + "]", e );
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
        BaseSchema.Builder schema = parseContentTypeXml( serializedSchema );
        if ( schema == null )
        {
            schema = parseRelationshipTypeXml( serializedSchema );
        }
        if ( schema == null )
        {
            schema = parseMixinXml( serializedSchema );
        }
        if ( schema == null )
        {
            schema = parseMetadataSchemaXml( serializedSchema );
        }
        return schema;
    }

    private ContentType.Builder parseContentTypeXml( final String serializedContentType )
    {
        try
        {
            final ContentType.Builder builder = ContentType.newContentType();
            final XmlContentType contentTypeXml = XmlSerializers2.contentType().parse( serializedContentType );
            XmlContentTypeMapper.fromXml( contentTypeXml, builder );
            return builder;
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private RelationshipType.Builder parseRelationshipTypeXml( final String serializedRelationshipType )
    {
        try
        {
            final RelationshipType.Builder builder = RelationshipType.newRelationshipType();
            final XmlRelationshipType relationshipTypeXml = XmlSerializers2.relationshipType().parse( serializedRelationshipType );
            XmlRelationshipTypeMapper.fromXml( relationshipTypeXml, builder );
            return builder;
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private Mixin.Builder parseMixinXml( final String serializedMixin )
    {
        try
        {
            final Mixin.Builder builder = Mixin.newMixin();
            final XmlMixin mixinXml = XmlSerializers2.mixin().parse( serializedMixin );
            XmlMixinMapper.fromXml( mixinXml, builder );
            return builder;
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private MetadataSchema.Builder parseMetadataSchemaXml( final String serializedMetadataSchema )
    {
        try
        {
            final MetadataSchema.Builder builder = MetadataSchema.newMetadataSchema();
            final XmlMetadataSchema metadataSchemaXml = XmlSerializers2.metadataSchema().parse( serializedMetadataSchema );
            XmlMetadataSchemaMapper.fromXml( metadataSchemaXml, builder );
            return builder;
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}
