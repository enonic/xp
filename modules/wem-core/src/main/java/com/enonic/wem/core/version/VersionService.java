package com.enonic.wem.core.version;

import java.util.Set;

public interface VersionService
{
    public void store( final VersionDocument versionDocument );

    public Set<VersionEntry> getBranch( final VersionBranchQuery query );

}
