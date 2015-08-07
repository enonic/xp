package com.enonic.xp.upgrade.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.CharSource;

/**
 * Removes owner, modifier, creator from fulltext and ngram indexing
 */
public final class UpgradeModel006
    extends AbstractXsltUpgradeModel
{
    private final static String SUPPORTED_REPO = "cms-repo";

    public UpgradeModel006()
    {
        super( "UpgradeModel006.xsl" );
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
        return "UpgradeModel006: Removes owner, modifier, creator from fulltext and ngram indexing";
    }
}
