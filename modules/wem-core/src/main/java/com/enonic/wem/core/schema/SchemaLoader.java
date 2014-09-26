package com.enonic.wem.core.schema;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.schema.BaseSchema;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKind;
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

import static com.enonic.wem.api.schema.SchemaKind.CONTENT_TYPE;
import static com.enonic.wem.api.schema.SchemaKind.METADATA_SCHEMA;
import static com.enonic.wem.api.schema.SchemaKind.MIXIN;
import static com.enonic.wem.api.schema.SchemaKind.RELATIONSHIP_TYPE;
import static java.util.stream.Collectors.toList;

public final class SchemaLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( SchemaLoader.class );

    private final static Pattern CONTENT_TYPE_PATTERN = Pattern.compile( "content-type/([^/]+)/content-type\\.xml" );

    private final static Pattern MIXIN_PATTERN = Pattern.compile( "mixin/([^/]+)/mixin\\.xml" );

    private final static Pattern RELATIONSHIP_TYPE_PATTERN = Pattern.compile( "relationship-type/([^/]+)/relationship-type\\.xml" );

    private final static Pattern METADATA_PATTERN = Pattern.compile( "metadata/([^/]+)/metadata\\.xml" );

    private final static ImmutableMap<SchemaKind, Pattern> SCHEMA_PATTERNS =
        ImmutableMap.of( CONTENT_TYPE, CONTENT_TYPE_PATTERN, MIXIN, MIXIN_PATTERN, RELATIONSHIP_TYPE, RELATIONSHIP_TYPE_PATTERN,
                         METADATA_SCHEMA, METADATA_PATTERN );

    private final static ImmutableMap<SchemaKind, String> SCHEMA_FILES =
        ImmutableMap.of( CONTENT_TYPE, "content-type.xml", MIXIN, "mixin.xml", RELATIONSHIP_TYPE, "relationship-type.xml", METADATA_SCHEMA,
                         "metadata.xml" );

    private final static ImmutableMap<SchemaKind, String> SCHEMA_DIRECTORIES =
        ImmutableMap.of( CONTENT_TYPE, "content-type", MIXIN, "mixin", RELATIONSHIP_TYPE, "relationship-type", METADATA_SCHEMA,
                         "metadata" );

    private final IconDao iconDao = new IconDao();

    public Schemas loadSchemas( final Module module )
    {
        final ModuleKey moduleKey = module.getKey();
        final List<SchemaKindName> schemaKeys = findSchemaKeys( module );

        final List<Schema> schemas = schemaKeys.stream().
            map( ( schemaKey ) -> loadSchema( moduleKey, schemaKey ) ).
            filter( Objects::nonNull ).
            collect( toList() );

        return Schemas.from( schemas );
    }

    private Schema loadSchema( final ModuleKey moduleKey, final SchemaKindName schemaKey )
    {
        final SchemaKind kind = schemaKey.schemaKind;
        final String schemaName = schemaKey.name;
        final ResourceKey schemaFolderKey = ResourceKey.from( moduleKey, SCHEMA_DIRECTORIES.get( kind ) + "/" + schemaName );
        final ResourceKey schemaXmlKey = schemaFolderKey.resolve( SCHEMA_FILES.get( kind ) );

        final Resource schemaResource = Resource.from( schemaXmlKey );
        if ( schemaResource.exists() )
        {
            try
            {
                final String serializedSchema = schemaResource.readString();

                final BaseSchema.Builder schemaBuilder = parseSchemaXml( kind, serializedSchema );
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

    private List<SchemaKindName> findSchemaKeys( final Module module )
    {
        return module.getResourcePaths().stream().
            map( this::resourcePathToSchemaKey ).
            filter( Objects::nonNull ).
            collect( toList() );
    }

    private SchemaKindName resourcePathToSchemaKey( final String resourcePath )
    {
        return SCHEMA_PATTERNS.entrySet().stream().
            map( schemaEntry -> {
                final Matcher matcher = schemaEntry.getValue().matcher( resourcePath );
                return matcher.matches() ? new SchemaKindName( schemaEntry.getKey(), matcher.group( 1 ) ) : null;
            } ).
            filter( Objects::nonNull ).
            findFirst().
            orElse( null );
    }

    private BaseSchema.Builder parseSchemaXml( final SchemaKind kind, final String serializedSchema )
    {
        switch ( kind )
        {
            case CONTENT_TYPE:
                return parseContentTypeXml( serializedSchema );
            case MIXIN:
                return parseMixinXml( serializedSchema );
            case RELATIONSHIP_TYPE:
                return parseRelationshipTypeXml( serializedSchema );
            case METADATA_SCHEMA:
                return parseMetadataSchemaXml( serializedSchema );
        }
        throw new IllegalArgumentException( "Unsupported SchemaKind [" + kind + "]" );
    }

    private ContentType.Builder parseContentTypeXml( final String serializedContentType )
    {
        final ContentType.Builder builder = ContentType.newContentType();
        final XmlContentType contentTypeXml = XmlSerializers2.contentType().parse( serializedContentType );
        XmlContentTypeMapper.fromXml( contentTypeXml, builder );
        return builder;
    }

    private RelationshipType.Builder parseRelationshipTypeXml( final String serializedRelationshipType )
    {
        final RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        final XmlRelationshipType relationshipTypeXml = XmlSerializers2.relationshipType().parse( serializedRelationshipType );
        XmlRelationshipTypeMapper.fromXml( relationshipTypeXml, builder );
        return builder;
    }

    private Mixin.Builder parseMixinXml( final String serializedMixin )
    {
        final Mixin.Builder builder = Mixin.newMixin();
        final XmlMixin mixinXml = XmlSerializers2.mixin().parse( serializedMixin );
        XmlMixinMapper.fromXml( mixinXml, builder );
        return builder;
    }

    private MetadataSchema.Builder parseMetadataSchemaXml( final String serializedMetadataSchema )
    {
        final MetadataSchema.Builder builder = MetadataSchema.newMetadataSchema();
        final XmlMetadataSchema metadataSchemaXml = XmlSerializers2.metadataSchema().parse( serializedMetadataSchema );
        XmlMetadataSchemaMapper.fromXml( metadataSchemaXml, builder );
        return builder;
    }

    private static class SchemaKindName
    {
        public final SchemaKind schemaKind;

        public final String name;

        SchemaKindName( final SchemaKind schemaKind, final String name )
        {
            this.schemaKind = schemaKind;
            this.name = name;
        }
    }
}
