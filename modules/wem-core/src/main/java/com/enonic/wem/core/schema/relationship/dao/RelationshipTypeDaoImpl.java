package com.enonic.wem.core.schema.relationship.dao;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypeXml;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.schema.SchemaIconDao;
import com.enonic.wem.xml.XmlSerializers;

import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;

public final class RelationshipTypeDaoImpl
    implements RelationshipTypeDao
{
    private static final String RELATIONSHIP_TYPE_XML = "relationship-type.xml";

    private Path basePath;


    @Override
    public RelationshipType createRelationshipType( final RelationshipType relationshipType )
    {
        final Path relationshipPath = pathForRelationshipType( relationshipType.getName() );

        writeRelationshipTypeXml( relationshipType, relationshipPath );
        new SchemaIconDao().writeSchemaIcon( relationshipType.getIcon(), relationshipPath );

        return relationshipType;
    }

    @Override
    public void updateRelationshipType( final RelationshipType relationshipType )
    {
        final Path relationshipPath = pathForRelationshipType( relationshipType.getName() );

        writeRelationshipTypeXml( relationshipType, relationshipPath );
        new SchemaIconDao().writeSchemaIcon( relationshipType.getIcon(), relationshipPath );
    }

    @Override
    public RelationshipTypes getAllRelationshipTypes()
    {
        final List<RelationshipType> relationshipTypeList = Lists.newArrayList();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream( this.basePath ))
        {
            for ( Path schemaDir : directoryStream )
            {
                final RelationshipType.Builder relationshipTypeBuilder = readRelationshipType( schemaDir );
                final RelationshipType relationshipType = relationshipTypeBuilder != null ? relationshipTypeBuilder.build() : null;
                if ( relationshipType != null )
                {
                    relationshipTypeList.add( relationshipType );
                }
            }
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not retrieve relationship types" );
        }
        return RelationshipTypes.from( relationshipTypeList );
    }

    @Override
    public RelationshipType.Builder getRelationshipType( final RelationshipTypeName relationshipTypeName )
    {
        final Path relationshipPath = pathForRelationshipType( relationshipTypeName );
        return readRelationshipType( relationshipPath );
    }

    @Override
    public boolean deleteRelationshipType( final RelationshipTypeName relationshipTypeName )
    {
        final Path relationshipPath = pathForRelationshipType( relationshipTypeName );
        final boolean relationTypeDirExists =
            isDirectory( relationshipPath ) && isRegularFile( relationshipPath.resolve( RELATIONSHIP_TYPE_XML ) );
        if ( relationTypeDirExists )
        {
            try
            {
                FileUtils.deleteDirectory( relationshipPath.toFile() );
                return true;
            }
            catch ( IOException e )
            {
                throw new SystemException( e, "Could not delete relationship type [{0}]", relationshipTypeName );
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public RelationshipTypeNames exists( final RelationshipTypeNames relationshipTypeNames )
    {
        final List<RelationshipTypeName> existingList = Lists.newArrayList();
        for ( RelationshipTypeName name : relationshipTypeNames )
        {
            if ( relationshipTypeExists( name ) )
            {
                existingList.add( name );
            }
        }
        return RelationshipTypeNames.from( existingList );
    }

    private boolean relationshipTypeExists( final RelationshipTypeName relationshipType )
    {
        final Path relationshipPath = pathForRelationshipType( relationshipType );
        return isDirectory( relationshipPath ) &&
            isRegularFile( relationshipPath.resolve( relationshipPath.resolve( RELATIONSHIP_TYPE_XML ) ) );
    }

    private RelationshipType.Builder readRelationshipType( final Path relationshipPath )
    {
        final boolean isRelationshipDir =
            isDirectory( relationshipPath ) && isRegularFile( relationshipPath.resolve( RELATIONSHIP_TYPE_XML ) );
        if ( isRelationshipDir )
        {
            final RelationshipType.Builder relationshipType = readRelationshipTypeXml( relationshipPath );
            final SchemaIcon icon = new SchemaIconDao().readSchemaIcon( relationshipPath );
            relationshipType.icon( icon );
            return relationshipType;
        }
        return null;
    }

    private void writeRelationshipTypeXml( final RelationshipType relationshipType, final Path relationshipPath )
    {
        final RelationshipTypeXml relationshipTypeXml = new RelationshipTypeXml();
        relationshipTypeXml.from( relationshipType );
        final String serializedRelationshipType = XmlSerializers.relationshipType().serialize( relationshipTypeXml );

        final Path xmlFile = relationshipPath.resolve( RELATIONSHIP_TYPE_XML );

        try
        {
            Files.createDirectories( relationshipPath );
            Files.write( xmlFile, serializedRelationshipType.getBytes( Charsets.UTF_8 ) );
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not store relationship type [{0}]", relationshipType.getName() );
        }
    }

    private RelationshipType.Builder readRelationshipTypeXml( final Path relationshipPath )
    {
        final Path xmlFile = relationshipPath.resolve( RELATIONSHIP_TYPE_XML );
        try
        {
            final String serializedRelationshipType = new String( Files.readAllBytes( xmlFile ), Charsets.UTF_8 );
            final String relationshipTypeName = relationshipPath.getFileName().toString();

            final RelationshipType.Builder builder = RelationshipType.newRelationshipType().name( relationshipTypeName );
            XmlSerializers.relationshipType().parse( serializedRelationshipType ).to( builder );
            return builder;
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not read relationship type [{0}]", relationshipPath.getFileName() );
        }
    }

    private Path pathForRelationshipType( final RelationshipTypeName relationshipTypeName )
    {
        return this.basePath.resolve( relationshipTypeName.toString() );
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
        throws IOException
    {
        this.basePath = systemConfig.getRelationshiptTypesDir();
        Files.createDirectories( this.basePath );
    }
}
