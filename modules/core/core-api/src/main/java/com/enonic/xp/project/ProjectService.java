package com.enonic.xp.project;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.project.layer.ContentLayer;
import com.enonic.xp.project.layer.ContentLayerKey;
import com.enonic.xp.project.layer.CreateLayerParams;
import com.enonic.xp.project.layer.ModifyLayerParams;

@PublicApi
public interface ProjectService
{
    Project create( final CreateProjectParams params );

    Project modify( final ModifyProjectParams params );

    Projects list();

    Project get( final ProjectName projectName );

    boolean delete( final ProjectName projectName );

    ContentLayer createLayer( final CreateLayerParams params );

    ContentLayer modifyLayer( final ModifyLayerParams params );

    boolean deleteLayer( final ContentLayerKey key );
}
