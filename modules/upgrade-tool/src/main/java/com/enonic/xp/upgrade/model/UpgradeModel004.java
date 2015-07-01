package com.enonic.xp.upgrade.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.CharSource;

/**
 * Remove pre-scaled image attachments (those with label="small", "medium", "large", "extra-large")
 */
public final class UpgradeModel004
    extends AbstractXsltUpgradeModel
{
    private final static String SUPPORTED_REPO = "cms-repo";

    public UpgradeModel004()
    {
        super( "UpgradeModel004.xsl" );
    }

    @Override
    public boolean supports( final Path path, final String repositoryName, final String branchName )
    {
        return path.endsWith( Paths.get( "_", "node.xml" ) ) && SUPPORTED_REPO.equals( repositoryName );
    }

    @Override
    public String upgrade( final Path path, final CharSource source )
    {
        return this.transform( path, source );
    }
}
