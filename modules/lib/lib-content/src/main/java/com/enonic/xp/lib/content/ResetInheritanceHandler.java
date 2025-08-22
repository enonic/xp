package com.enonic.xp.lib.content;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.script.bean.BeanContext;

public class ResetInheritanceHandler
    extends BaseContextHandler
{
    private Supplier<SyncContentService> syncContentService;

    private String key;

    private String projectName;

    private List<String> inherit;

    @Override
    public void initialize( final BeanContext context )
    {
        super.initialize( context );
        this.syncContentService = context.getService( SyncContentService.class );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setProjectName( final String projectName )
    {
        this.projectName = projectName;
    }

    public void setInherit( final List<String> inherit )
    {
        this.inherit = inherit;
    }

    @Override
    protected Object doExecute()
    {
        validate();

        Content content;

        if ( this.key.startsWith( "/" ) )
        {
            content = contentService.getByPath( ContentPath.from( this.key ) );
        }
        else
        {
            content = contentService.getById( ContentId.from( this.key ) );
        }

        syncContentService.get().
            resetInheritance( ResetContentInheritParams.create().
                contentId( content.getId() ).
                projectName( ProjectName.from( projectName ) ).
                inherit( inherit.stream().
                    map( ContentInheritType::valueOf ).
                    collect( Collectors.toSet() ) ).
                build() );

        return null;
    }

    private void validate()
    {
        Objects.requireNonNull( key, "key is required" );
        Objects.requireNonNull( projectName, "projectName is required" );
        Objects.requireNonNull( inherit, "inherit is required" );
    }
}
