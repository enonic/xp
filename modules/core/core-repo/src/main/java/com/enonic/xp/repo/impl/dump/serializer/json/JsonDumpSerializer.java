package com.enonic.xp.repo.impl.dump.serializer.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteSource;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.index.AllTextIndexConfig;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repo.impl.dump.serializer.DumpSerializer;
import com.enonic.xp.repo.impl.node.json.AllTextIndexConfigJson;
import com.enonic.xp.repo.impl.node.json.AttachedBinaryJson;
import com.enonic.xp.repo.impl.node.json.IndexConfigDocumentJson;
import com.enonic.xp.repo.impl.node.json.IndexConfigJson;
import com.enonic.xp.repo.impl.node.json.NodeVersionDataJson;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;
import com.enonic.xp.repo.impl.node.json.PatternConfigJson;
import com.enonic.xp.security.acl.AccessControlList;

public class JsonDumpSerializer
    implements DumpSerializer
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    @Override
    public byte[] serialize( final BranchDumpEntry branchDumpEntry )
    {
        try
        {
            return MAPPER.writeValueAsBytes( BranchDumpEntryJson.from( branchDumpEntry ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serializer dumpEntry", e );
        }
    }

    @Override
    public byte[] serialize( final VersionsDumpEntry versionsDumpEntry )
    {
        try
        {
            return MAPPER.writeValueAsBytes( VersionsDumpEntryJson.from( versionsDumpEntry ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serializer dumpEntry", e );
        }
    }

    @Override
    public byte[] serialize( final CommitDumpEntry commitDumpEntry )
    {
        try
        {
            return MAPPER.writeValueAsBytes( CommitDumpEntryJson.from( commitDumpEntry ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serializer dumpEntry", e );
        }
    }

    @Override
    public BranchDumpEntry toBranchMetaEntry( final String value )
    {
        try
        {
            final BranchDumpEntryJson branchDumpEntryJson = MAPPER.readValue( value, BranchDumpEntryJson.class );
            return BranchDumpEntryJson.fromJson( branchDumpEntryJson );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value [" + value + "] to DumpEntry", e );
        }
    }

    @Override
    public VersionsDumpEntry toNodeVersionsEntry( final String value )
    {
        try
        {
            final VersionsDumpEntryJson nodeVersionMetaEntryJson = MAPPER.readValue( value, VersionsDumpEntryJson.class );
            return VersionsDumpEntryJson.fromJson( nodeVersionMetaEntryJson );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value [" + value + "] to DumpEntry", e );
        }
    }

    @Override
    public CommitDumpEntry toCommitDumpEntry( final String value )
    {
        try
        {
            final CommitDumpEntryJson commitDumpEntryJson = MAPPER.readValue( value, CommitDumpEntryJson.class );
            return CommitDumpEntryJson.fromJson( commitDumpEntryJson );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value [" + value + "] to DumpEntry", e );
        }
    }

    public static NodeStoreVersion toNodeStoreVersion( final ByteSource data, final ByteSource indexConfigDocumentData,
                                                       ByteSource accessControlData )
        throws IOException
    {
        final NodeStoreVersion dumpEntry = toNodeVersionDataDump( data );

        final PatternIndexConfigDocument indexConfigDocument = toIndexConfigDocumentDump( indexConfigDocumentData );

        final AccessControlList accessControl = NodeVersionJsonSerializer.toNodeVersionAccessControl( accessControlData );

        return NodeStoreVersion.create( dumpEntry ).indexConfigDocument( indexConfigDocument ).permissions( accessControl ).build();
    }

    public static NodeStoreVersion toNodeVersionDataDump( final ByteSource data )
    {
        try (InputStream is = data.openStream())
        {
            return JsonDumpSerializer.fromJsonDump( MAPPER.readValue( is, NodeVersionDataJson.class ) );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value to NodeVersionDataJson", e );
        }
    }

    public static PatternIndexConfigDocument toIndexConfigDocumentDump( final ByteSource data )
    {
        try (InputStream is = data.openStream())
        {
            return JsonDumpSerializer.fromJsonDump( MAPPER.readValue( is, IndexConfigDocumentJson.class ) );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value to IndexConfigDocumentJson", e );
        }
    }

    public static PatternIndexConfigDocument fromJsonDump( final IndexConfigDocumentJson json )
    {
        final PatternIndexConfigDocument.Builder builder =
            PatternIndexConfigDocument.create().analyzer( json.analyzer ).defaultConfig( fromJsonDump( json.defaultConfig ) );

        for ( final PatternConfigJson patternConfigJson : json.patternConfigs )
        {
            builder.add( fromJsonDump( patternConfigJson ) );
        }

        if ( json.allTextConfig != null )
        {
            builder.allTextConfig( fromJsonDump( json.allTextConfig ) );
        }
        return builder.build();
    }

    public static AllTextIndexConfig fromJsonDump( final AllTextIndexConfigJson json )
    {
        final AllTextIndexConfig.Builder builder = AllTextIndexConfig.create();

        if ( json.languages != null )
        {
            json.languages.stream()
                .map( l -> Locale.forLanguageTag( l.replace( '_', '-' ) ) )
                .filter( l -> !l.getLanguage().isEmpty() )
                .forEach( builder::addLanguage );
        }

        if ( json.enabled != null )
        {
            builder.enabled( json.enabled );
        }

        if ( json.nGram != null )
        {
            builder.nGram( json.nGram );
        }

        if ( json.fulltext != null )
        {
            builder.fulltext( json.fulltext );
        }

        return builder.build();
    }

    public static NodeStoreVersion fromJsonDump( final NodeVersionDataJson json )
    {
        return NodeStoreVersion.create()
            .id( NodeId.from( json.id ) )
            .data( PropertyTreeJson.fromJson( json.data ) )
            .childOrder( ChildOrder.from( json.childOrder ) )
            .manualOrderValue( json.manualOrderValue )
            .nodeType( NodeType.from( json.nodeType ) )
            .attachedBinaries( json.attachedBinaries.stream().map( AttachedBinaryJson::fromJson ).collect( AttachedBinaries.collector() ) )
            .build();
    }

    public static PathIndexConfig fromJsonDump( PatternConfigJson json )
    {
        return PathIndexConfig.create().path( IndexPath.from( json.path ) ).indexConfig( fromJsonDump( json.indexConfig ) ).build();
    }

    public static IndexConfig fromJsonDump( final IndexConfigJson json )
    {
        final IndexConfig.Builder builder = IndexConfig.create()
            .decideByType( json.decideByType )
            .enabled( json.enabled )
            .nGram( json.nGram )
            .fulltext( json.fulltext )
            .includeInAllText( json.includeInAllText )
            .path( json.path );

        if ( json.indexValueProcessors != null )
        {
            json.indexValueProcessors.stream()
                .map( IndexValueProcessors::get )
                .filter( Objects::nonNull )
                .forEach( builder::addIndexValueProcessor );
        }

        if ( json.languages != null )
        {
            json.languages.stream()
                .map( language -> Locale.forLanguageTag( language.replace( '_', '-' ) ) )
                .filter( l -> !l.getLanguage().isEmpty() )
                .forEach( builder::addLanguage );
        }

        return builder.build();
    }

    public static <T> T readValue( final byte[] value, final Class<T> clazz )
    {
        try
        {
            return MAPPER.readValue( value, clazz );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value", e );
        }
    }

    public static byte[] serialize( final Object value )
    {
        try
        {
            return MAPPER.writeValueAsBytes( value );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serialize value", e );
        }
    }
}
