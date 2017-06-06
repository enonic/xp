package com.enonic.xp.repo.impl.dump;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.dump.DumpParams;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.LoadParams;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.repo.impl.dump.writer.FileDumpWriter;
import com.enonic.xp.repository.RepositoryService;

@Component(immediate = true)
public class DumpServiceImpl
    implements DumpService
{
    private BlobStore blobStore;

    private NodeService nodeService;

    private RepositoryService repositoryService;

    private String xpVersion;

    private final static Path BASE_PATH = Paths.get( HomeDir.get().toString(), "data", "dump" );

    @Activate
    public void activate( final ComponentContext context )
    {
        xpVersion = context.getBundleContext().
            getBundle().
            getVersion().
            toString();
    }

    @Override
    public void dump( final DumpParams params )
    {
        final DumpResult result = RepoDumper.create().
            writer( FileDumpWriter.create().
                basePath( BASE_PATH ).
                dumpName( params.getDumpName() ).
                blobStore( this.blobStore ).
                build() ).
            nodeService( this.nodeService ).
            repositoryService( this.repositoryService ).
            repositoryId( params.getRepositoryId() ).
            xpVersion( this.xpVersion ).
            build().
            execute();

        System.out.println( result );
    }

    @Override
    public void load( final LoadParams params )
    {
        RepoLoader.create().
            reader( new FileDumpReader( BASE_PATH, params.getDumpName() ) ).
            nodeService( this.nodeService ).
            repositoryService( this.repositoryService ).
            repositoryId( params.getRepositoryId() ).
            build().
            execute();
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }
}
