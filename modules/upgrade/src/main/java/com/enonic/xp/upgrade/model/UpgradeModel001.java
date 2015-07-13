package com.enonic.xp.upgrade.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.CharSource;

/**
 * Replace input-types 'html_part' with 'string'
 */
public final class UpgradeModel001
    extends AbstractXsltUpgradeModel
{
    private final static String SUPPORTED_REPO = "cms-repo";

    public UpgradeModel001()
    {
        super( "UpgradeModel001.xsl" );
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
