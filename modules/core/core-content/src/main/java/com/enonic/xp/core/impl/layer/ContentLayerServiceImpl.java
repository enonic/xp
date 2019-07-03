package com.enonic.xp.core.impl.layer;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.BranchInfo;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.layer.ContentLayer;
import com.enonic.xp.layer.ContentLayerException;
import com.enonic.xp.layer.ContentLayerName;
import com.enonic.xp.layer.ContentLayerService;
import com.enonic.xp.layer.ContentLayers;
import com.enonic.xp.layer.CreateContentLayerParams;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryService;

@Component
public class ContentLayerServiceImpl
    implements ContentLayerService
{
    private RepositoryService repositoryService;

    @Override
    public ContentLayers list()
    {
        final Repository contentRepository = repositoryService.get( ContentConstants.CONTENT_REPO_ID );
        if ( contentRepository != null )
        {
            final List<ContentLayer> contentLayers = contentRepository.getBranchInfos().
                stream().
                map( this::toLayer ).
                filter( Objects::nonNull ).
                collect( Collectors.toList() );
            return ContentLayers.from( contentLayers );
        }
        return ContentLayers.empty();
    }

    @Override
    public ContentLayer get( final ContentLayerName name )
    {
        final Repository contentRepository = repositoryService.get( ContentConstants.CONTENT_REPO_ID );
        if ( contentRepository != null )
        {
            final Branch draftBranch = toDraftBranch( name );

            for ( BranchInfo branchInfo : contentRepository.getBranchInfos() )
            {
                if ( branchInfo.equals( draftBranch ) )
                {
                    return toLayer( branchInfo );
                }
            }
        }
        return null;
    }

    @Override
    public ContentLayer create( final CreateContentLayerParams params )
    {
        final Repository contentRepository = repositoryService.get( ContentConstants.CONTENT_REPO_ID );
        final Branch draftBranch = Branch.from( ContentLayer.BRANCH_PREFIX_DRAFT + params.getName() );
        final Branch masterBranch = Branch.from( ContentLayer.BRANCH_PREFIX_MASTER + params.getName() );
        final ContentLayerName parentLayer = params.getParentName();
        final Branch parentDraftBranch =
            parentLayer == null ? ContentConstants.BRANCH_DRAFT : Branch.from( ContentLayer.BRANCH_PREFIX_DRAFT + parentLayer );

        if ( contentRepository.getChildBranchInfos( parentDraftBranch ) == null )
        {
            throw new ContentLayerException( MessageFormat.format( "Branch [{0}] not found", parentDraftBranch ) );
        }

        final CreateBranchParams createDraftBranchParams = new CreateBranchParams( draftBranch, parentDraftBranch );
        repositoryService.createBranch( createDraftBranchParams );

        final CreateBranchParams createMasterBranchParams = new CreateBranchParams( masterBranch );
        repositoryService.createBranch( createMasterBranchParams );

        return ContentLayer.from( params.getName(), params.getParentName() );
    }

    private Branch toDraftBranch( final ContentLayerName contentLayerName )
    {
        if ( contentLayerName == null )
        {
            return ContentConstants.BRANCH_DRAFT;
        }
        else
        {
            return Branch.from( ContentLayer.BRANCH_PREFIX_DRAFT + contentLayerName );
        }
    }

    private ContentLayer toLayer( final BranchInfo branchInfo )
    {
        final String branchValue = branchInfo.getBranch().getValue();
        if ( ContentConstants.BRANCH_VALUE_DRAFT.equals( branchValue ) )
        {
            return ContentLayer.DEFAULT_CONTENT_LAYER;
        }

        if ( !branchValue.startsWith( ContentLayer.BRANCH_PREFIX_DRAFT ) )
        {
            return null;
        }
        final ContentLayerName layerName = ContentLayerName.from( branchValue.substring( ContentLayer.BRANCH_PREFIX_DRAFT.length() ) );

        final String parentBranchValue = branchInfo.getParentBranch() == null ? null : branchInfo.getParentBranch().getValue();
        if ( parentBranchValue == null )
        {
            throw new ContentLayerException( MessageFormat.format( "Branch [{0}] has no parent branch", layerName ) );
        }

        final ContentLayerName parentLayerName;
        if ( ContentConstants.BRANCH_VALUE_DRAFT.equals( parentBranchValue ) )
        {
            parentLayerName = null;
        }
        else
        {
            if ( !parentBranchValue.startsWith( ContentLayer.BRANCH_PREFIX_DRAFT ) )
            {
                throw new ContentLayerException(
                    MessageFormat.format( "Branch [{0}] has an invalid parent branch [{1}]", branchValue, parentBranchValue ) );

            }
            parentLayerName = ContentLayerName.from( parentBranchValue.substring( ContentLayer.BRANCH_PREFIX_DRAFT.length() ) );
        }

        return ContentLayer.from( layerName, parentLayerName );
    }


    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }
}
