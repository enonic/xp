package com.enonic.xp.upgrade.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.CharSource;

/**
 * Rename default analyzer from content_default to document_index_default)
 */
public final class UpgradeModel005
    extends AbstractXsltUpgradeModel
{
    private final static String SUPPORTED_REPO = "cms-repo";

    public UpgradeModel005()
    {
        super( "UpgradeModel005.xsl" );
    }

    @Override
    public boolean supports( final Path path, final String repositoryName, final String branchName )
    {
        return path.endsWith( Paths.get( "_", "node.xml" ) ) && SUPPORTED_REPO.equals( repositoryName );
    }

    @Override
    public String upgrade( final Path path, final CharSource source )
    {
        System.out.println( "Rename default analyzer" );

        return this.transform( path, source );
    }
}
