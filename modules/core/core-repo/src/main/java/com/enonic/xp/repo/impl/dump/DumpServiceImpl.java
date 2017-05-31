package com.enonic.xp.repo.impl.dump;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    private final static Path BASE_PATH = Paths.get( HomeDir.get().toString(), "data", "dump" );

    @Override
    public void dump( final DumpParams params )
    {
        RepoDumper.create().
            writer( new FileDumpWriter( BASE_PATH, params.getDumpName() ) ).
            blobStore( this.blobStore ).
            nodeService( this.nodeService ).
            repositoryService( this.repositoryService ).
            repositoryId( params.getRepositoryId() ).
            build().
            execute();
    }

    @Override
    public void load( final LoadParams params )
    {
        System.out.println( "Starting load of dump " + params.getDumpName() );

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
