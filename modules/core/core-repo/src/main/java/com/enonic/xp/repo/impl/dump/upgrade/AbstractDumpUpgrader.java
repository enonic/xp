package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.util.Version;

public abstract class AbstractDumpUpgrader
    implements DumpUpgrader
{
    protected final Path basePath;

    protected FileDumpReader dumpReader;

    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    protected DumpUpgradeStepResult.Builder result;

    public AbstractDumpUpgrader( final Path basePath )
    {
        this.basePath = basePath;
    }

    @Override
    public DumpUpgradeStepResult upgrade( final String dumpName )
    {
        result = DumpUpgradeStepResult.create()
            .initialVersion( new Version( getModelVersion().getMajor() - 1 ) )
            .upgradedVersion( getModelVersion() )
            .stepName( getName() );
        doUpgrade( dumpName );
        return result.build();
    }

    protected void doUpgrade( final String dumpName )
    {
        dumpReader = FileDumpReader.create( null, basePath, dumpName );
    }

    protected <T> T deserializeValue( final String value, final Class<T> clazz )
    {
        try
        {
            return MAPPER.readValue( value, clazz );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value [" + value + "]", e );
        }
    }


    public byte[] serialize( final Object value )
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

    public void processEntries( final BiConsumer<String, String> processor, final Path tarFile )
    {
        try (TarArchiveInputStream tarInputStream = openStream( tarFile ))
        {
            TarArchiveEntry entry = tarInputStream.getNextTarEntry();
            while ( entry != null )
            {
                String entryContent = new String( tarInputStream.readAllBytes(), StandardCharsets.UTF_8 );
                processor.accept( entryContent, entry.getName() );
                entry = tarInputStream.getNextTarEntry();
            }
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot read meta-data", e );
        }
    }

    private TarArchiveInputStream openStream( final Path metaFile )
        throws IOException
    {
        return new TarArchiveInputStream( new GZIPInputStream( Files.newInputStream( metaFile ) ) );
    }
}
