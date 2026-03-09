package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReaderV7;
import com.enonic.xp.repo.impl.dump.upgrade.v8.DumpUpgrader7to8;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repo.impl.dump.writer.ZipDumpWriterV8;
import com.enonic.xp.upgrade.UpgradeListener;
import com.enonic.xp.util.Version;

public class DumpUpgraderImpl
{
    private static final Logger LOG = LoggerFactory.getLogger( DumpUpgraderImpl.class );

    private static final Version SUPPORTED_MODEL_VERSION = new Version( 8, 0, 0 );

    public DumpUpgradeResult upgrade( final Path basePath, final String dumpName, final UpgradeListener upgradeListener )
    {
        final DumpUpgradeResult.Builder result = DumpUpgradeResult.create().dumpName( dumpName );

        final FileDumpReaderV7 dumpReader = FileDumpReaderV7.create( null, basePath, dumpName );
        final Version modelVersion = Objects.requireNonNullElse( dumpReader.getDumpMeta().getModelVersion(), Version.emptyVersion );

        result.initialVersion( modelVersion );

        if ( !SUPPORTED_MODEL_VERSION.equals( modelVersion ) )
        {
            throw new RepoLoadException(
                "Cannot upgrade dump; model version [" + modelVersion + "] is not supported, expected [" + SUPPORTED_MODEL_VERSION + "]" );
        }

        DumpUpgrader dumpUpgrader = new DumpUpgrader7to8( dumpReader );

        if ( upgradeListener != null )
        {
            upgradeListener.total( 1 );
        }

        final Version targetModelVersion = dumpUpgrader.getModelVersion();
        final String targetDumpName = dumpName + "-upgraded-to-" + targetModelVersion;

        try (DumpWriter dumpWriter = ZipDumpWriterV8.create( basePath, targetDumpName ))
        {
            LOG.info( "Running upgrade step [{}]...", dumpUpgrader.getName() );
            dumpUpgrader.upgrade( basePath, dumpName, dumpWriter );
            LOG.info( "Finished upgrade step [{}]", dumpUpgrader.getName() );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Failed to close dump writer", e );
        }

        if ( upgradeListener != null )
        {
            upgradeListener.upgraded();
        }

        result.upgradedVersion( targetModelVersion );
        result.dumpName( targetDumpName );
        return result.build();
    }

    static void main()
    {
        new DumpUpgraderImpl().upgrade( Path.of( "/Users/rymsha/IdeaProjects/xp/build" ), "dump-2026-03-09T16-00-44", null );
    }
}
