package com.enonic.wem.core.schema.relationship;

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
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.api.xml.mapper.XmlRelationshipTypeMapper;
import com.enonic.wem.api.xml.model.XmlRelationshipType;
import com.enonic.wem.api.xml.serializer.XmlSerializers;
import com.enonic.wem.core.support.dao.IconDao;

import static java.util.stream.Collectors.toList;

public final class RelationshipTypeLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( RelationshipTypeLoader.class );

    private final static Pattern RELATIONSHIP_TYPE_PATTERN = Pattern.compile( "relationship-type/([^/]+)/relationship-type\\.xml" );

    private final static String RELATIONSHIP_TYPE_FILE = "relationship-type.xml";

    private final static String RELATIONSHIP_TYPE_DIRECTORY = "relationship-type";

    private final IconDao iconDao = new IconDao();

    public RelationshipTypes loadRelationshipTypes( final Module module )
    {
        final ModuleKey moduleKey = module.getKey();
        final List<RelationshipTypeName> relationshipTypeNames = findRelationshipTypeNames( module );

        final List<RelationshipType> relationshipTypes = relationshipTypeNames.stream().
            map( ( relationshipTypeName ) -> loadRelationshipType( moduleKey, relationshipTypeName ) ).
            filter( Objects::nonNull ).
            collect( toList() );

        return RelationshipTypes.from( relationshipTypes );
    }

    private RelationshipType loadRelationshipType( final ModuleKey moduleKey, final RelationshipTypeName relationshipTypeName )
    {
        final String name = relationshipTypeName.getLocalName();
        final ResourceKey folderResourceKey = ResourceKey.from( moduleKey, RELATIONSHIP_TYPE_DIRECTORY + "/" + name );
        final ResourceKey relationshipTypeResourceKey = folderResourceKey.resolve( RELATIONSHIP_TYPE_FILE );

        final Resource resource = Resource.from( relationshipTypeResourceKey );
        if ( resource.exists() )
        {
            try
            {
                final String serializedResource = resource.readString();

                final RelationshipType.Builder relationshipType = parseRelationshipTypeXml( serializedResource );
                final Instant modifiedTime = Instant.now();
                relationshipType.modifiedTime( modifiedTime );
                relationshipType.createdTime( modifiedTime );
                relationshipType.icon( iconDao.readIcon( folderResourceKey ) );
                return relationshipType.name( RelationshipTypeName.from( moduleKey, name ) ).build();
            }
            catch ( Exception e )
            {
                LOG.warn( "Could not load relationshipType [" + relationshipTypeResourceKey + "]", e );
            }
        }
        return null;
    }

    private List<RelationshipTypeName> findRelationshipTypeNames( final Module module )
    {
        return module.getResourcePaths().stream().
            map( this::getRelationshipTypeNameFromResourcePath ).
            filter( Objects::nonNull ).
            map( ( localName ) -> RelationshipTypeName.from( module.getKey(), localName ) ).
            collect( toList() );
    }

    private String getRelationshipTypeNameFromResourcePath( final String resourcePath )
    {
        final Matcher matcher = RELATIONSHIP_TYPE_PATTERN.matcher( resourcePath );
        return matcher.matches() ? matcher.group( 1 ) : null;
    }

    private RelationshipType.Builder parseRelationshipTypeXml( final String serializedRelationshipType )
    {
        final RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        final XmlRelationshipType relationshipTypeXml = XmlSerializers.relationshipType().parse( serializedRelationshipType );
        XmlRelationshipTypeMapper.fromXml( relationshipTypeXml, builder );
        return builder;
    }

}
