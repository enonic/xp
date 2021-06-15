package com.enonic.xp.core.impl.schema.relationship;

import java.time.Instant;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.SchemaLoader;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.xml.parser.XmlRelationshipTypeParser;

final class RelationshipTypeLoader
    extends SchemaLoader<RelationshipTypeName, RelationshipType>
{
    RelationshipTypeLoader( final ResourceService resourceService )
    {
        super( resourceService, "/site/relationship-types" );
    }

    @Override
    protected RelationshipType load( final RelationshipTypeName name, final Resource resource )
    {
        final RelationshipType.Builder builder = RelationshipType.create();
        parseXml( resource, builder );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );

        builder.icon( loadIcon( name ) );
        return builder.name( name ).build();
    }

    private void parseXml( final Resource resource, final RelationshipType.Builder builder )
    {
        final XmlRelationshipTypeParser parser = new XmlRelationshipTypeParser();
        parser.currentApplication( resource.getKey().getApplicationKey() );
        parser.source( resource.readString() );
        parser.builder( builder );
        parser.parse();
    }

    @Override
    protected RelationshipTypeName newName( final ApplicationKey appKey, final String name )
    {
        return RelationshipTypeName.from( appKey, name );
    }
}
