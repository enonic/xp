package com.enonic.xp.portal.impl.url2;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.project.ProjectName;

public interface UrlGeneratorContext
{
    ProjectName getProject();

    Branch getBranch();

    ContentPath getSiteKey();

    String getBaseUri();
}
