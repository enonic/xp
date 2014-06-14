package com.enonic.wem.core.version;

public interface VersionService
{
    public void store( final VersionDocument versionDocument );

    public VersionBranch getBranch( final VersionBranchQuery query );

}
