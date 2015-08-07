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
    public UpgradeModel005()
    {
        super( "UpgradeModel005.xsl" );
    }

    @Override
    public boolean supports( final Path path, final String repositoryName, final String branchName )
    {
        return path.endsWith( Paths.get( "_", "node.xml" ) );
    }

    @Override
    public String upgrade( final Path path, final CharSource source )
    {
        return this.transform( path, source );
    }

    @Override
    protected String getLogMsg()
    {
        return "UpgradeModel005: Rename default analyzer from content_default to document_index_default";
    }
}
