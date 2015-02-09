package com.enonic.wem.repo.internal.elasticsearch.snapshot;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.snapshot.RestoreParams;
import com.enonic.wem.api.snapshot.RestoreResult;
import com.enonic.wem.api.snapshot.SnapshotParams;
import com.enonic.wem.api.snapshot.SnapshotResult;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.entity.NodeHelper;
import com.enonic.wem.repo.internal.snapshot.SnapshotService;

@Component
public class ElasticsearchSnapshotService
    implements SnapshotService
{
    private ElasticsearchDao elasticsearchDao;

    @Override
    public SnapshotResult snapshot( final RepositoryId repositoryId, final SnapshotParams snapshotParams )
    {
        return NodeHelper.runAsAdmin( () -> doSnapshot( repositoryId, snapshotParams ) );
    }

    private SnapshotResult doSnapshot( final RepositoryId repositoryId, final SnapshotParams snapshotParams )
    {
        return this.elasticsearchDao.snapshot( repositoryId, snapshotParams.getSnapshotName() );
    }

    @Override
    public RestoreResult restore( final RepositoryId repositoryId, final RestoreParams restoreParams )
    {
        return NodeHelper.runAsAdmin( () -> doRestore( repositoryId, restoreParams ) );
    }

    private RestoreResult doRestore( final RepositoryId repositoryId, final RestoreParams restoreParams )
    {
        return this.elasticsearchDao.restore( repositoryId, restoreParams.getSnapshotName() );
    }

    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
