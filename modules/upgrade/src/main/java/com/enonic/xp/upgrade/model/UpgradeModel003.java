package com.enonic.xp.upgrade.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.CharSource;

/**
 * Replace moduleConfig with siteConfig in Content data
 */
public final class UpgradeModel003
    extends AbstractXsltUpgradeModel
{
    private final static String SUPPORTED_REPO = "cms-repo";

    public UpgradeModel003()
    {
        super( "UpgradeModel003.xsl" );
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

    @Override
    protected String getLogMsg()
    {
        return "UpgradeModel003: Replace moduleConfig with siteConfig in Content data";
    }
}
