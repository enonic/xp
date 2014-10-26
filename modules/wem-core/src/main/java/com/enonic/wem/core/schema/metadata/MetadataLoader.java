package com.enonic.wem.core.schema.metadata;

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
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;
import com.enonic.wem.api.xml.mapper.XmlMetadataSchemaMapper;
import com.enonic.wem.api.xml.model.XmlMetadataSchema;
import com.enonic.wem.api.xml.serializer.XmlSerializers;
import com.enonic.wem.core.support.dao.IconDao;

import static java.util.stream.Collectors.toList;

public final class MetadataLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( MetadataLoader.class );

    private final static Pattern METADATA_PATTERN = Pattern.compile( "metadata/([^/]+)/metadata\\.xml" );

    private final static String METADATA_FILE = "metadata.xml";

    private final static String METADATA_DIRECTORY = "metadata";

    private final IconDao iconDao = new IconDao();

    public MetadataSchemas loadMetadatas( final Module module )
    {
        final ModuleKey moduleKey = module.getKey();
        final List<MetadataSchemaName> metadataNames = findMetadataNames( module );

        final List<MetadataSchema> metadatas = metadataNames.stream().
            map( ( metadataName ) -> loadMetadata( moduleKey, metadataName ) ).
            filter( Objects::nonNull ).
            collect( toList() );

        return MetadataSchemas.from( metadatas );
    }

    private MetadataSchema loadMetadata( final ModuleKey moduleKey, final MetadataSchemaName metadataName )
    {
        final String name = metadataName.getLocalName();
        final ResourceKey folderResourceKey = ResourceKey.from( moduleKey, METADATA_DIRECTORY + "/" + name );
        final ResourceKey metadataResourceKey = folderResourceKey.resolve( METADATA_FILE );

        final Resource resource = Resource.from( metadataResourceKey );
        if ( resource.exists() )
        {
            try
            {
                final String serializedResource = resource.readString();

                final MetadataSchema.Builder metadata = parseMetadataSchemaXml( serializedResource );
                final Instant modifiedTime = Instant.now();
                metadata.modifiedTime( modifiedTime );
                metadata.createdTime( modifiedTime );
                metadata.icon( iconDao.readIcon( folderResourceKey ) );
                return metadata.name( MetadataSchemaName.from( moduleKey, name ) ).build();
            }
            catch ( Exception e )
            {
                LOG.warn( "Could not load metadata [" + metadataResourceKey + "]", e );
            }
        }
        return null;
    }

    private List<MetadataSchemaName> findMetadataNames( final Module module )
    {
        return module.getResourcePaths().stream().
            map( this::getMetadataNameFromResourcePath ).
            filter( Objects::nonNull ).
            map( ( localName ) -> MetadataSchemaName.from( module.getKey(), localName ) ).
            collect( toList() );
    }

    private String getMetadataNameFromResourcePath( final String resourcePath )
    {
        final Matcher matcher = METADATA_PATTERN.matcher( resourcePath );
        return matcher.matches() ? matcher.group( 1 ) : null;
    }

    private MetadataSchema.Builder parseMetadataSchemaXml( final String serializedMetadataSchema )
    {
        final MetadataSchema.Builder builder = MetadataSchema.newMetadataSchema();
        final XmlMetadataSchema metadataSchemaXml = XmlSerializers.metadataSchema().parse( serializedMetadataSchema );
        XmlMetadataSchemaMapper.fromXml( metadataSchemaXml, builder );
        return builder;
    }

}
