package com.enonic.xp.content;

import com.enonic.xp.project.Project;

public interface ProjectSynchronizer
{
    void sync( final ContentId contentId, final Project sourceProject, final Project targetProject );

    void syncWithChildren( final ContentId contentId, final Project sourceProject, final Project targetProject );

    void syncWithChildren( final ContentPath contentPath, final Project sourceProject, final Project targetProject );

    Content syncRenamed( final ContentId contentId, final Project sourceProject, final Project targetProject );

    Content syncMoved( final ContentId contentId, final Project sourceProject, final Project targetProject );

    Content syncUpdated( final ContentId contentId, final Project sourceProject, final Project targetProject );

    Content syncSorted( final ContentId contentId, final Project sourceProject, final Project targetProject );

    boolean syncDeleted( final ContentId contentId, final Project sourceProject, final Project targetProject );

    Content syncManualOrderUpdated( final ContentId contentId, final Project sourceProject, final Project targetProject );

    Content syncCreated( final ContentId contentId, final Project sourceProject, final Project targetProject );

}
