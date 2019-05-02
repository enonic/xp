package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.util.Version;

public abstract class AbstractDumpUpgrader
    implements DumpUpgrader
{
    protected final Path basePath;

    protected FileDumpReader dumpReader;

    protected ObjectMapper mapper = ObjectMapperHelper.create();

    protected DumpUpgradeStepResult.Builder result;

    public AbstractDumpUpgrader( final Path basePath )
    {
        this.basePath = basePath;
    }

    @Override
    public DumpUpgradeStepResult upgrade( final String dumpName )
    {
        result = DumpUpgradeStepResult.create().
            initialVersion( new Version( getModelVersion().getMajor() - 1 ) ).
            upgradedVersion( getModelVersion() ).
            stepName( getName() );
        doUpgrade( dumpName );
        return result.build();
    }

    protected void doUpgrade( final String dumpName )
    {
        dumpReader = new FileDumpReader( basePath, dumpName, null );
        mapper = ObjectMapperHelper.create();
    }

    protected <T> T deserializeValue( final String value, final Class<T> clazz )
    {
        try
        {
            return mapper.readValue( value, clazz );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value [" + value + "]", e );
        }
    }


    public String serialize( final Object value )
    {
        try
        {
            return mapper.writeValueAsString( value );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serialize value", e );
        }
    }

    protected BlobKey addRecord( final Segment segment, final String serializedData )
    {
        final ByteSource byteSource = ByteSource.wrap( serializedData.getBytes( Charsets.UTF_8 ) );
        final BlobRecord blobRecord = dumpReader.getDumpBlobStore().
            addRecord( segment, byteSource );
        return blobRecord.getKey();
    }
}
