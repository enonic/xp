package com.enonic.xp.lib.portal.url;

import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.url.PortalScope;
import com.enonic.xp.portal.url.PortalScopeParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

/**
 * Backs {@code portalScope} of {@code /lib/xp/portal}. The script keeps the resolved scope hidden in the object it returns,
 * to pass it on to {@code pageUrlParts} and {@code processHtmlParts}, and reads its Base URL with {@link #baseUrlOf}, so
 * that no script engine reads the scope itself.
 */
public final class PortalScopeHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String key;

    private String projectName;

    private String branch;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setProjectName( final String projectName )
    {
        this.projectName = projectName;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public PortalScope resolve()
    {
        final PortalScopeParams.Builder params = PortalScopeParams.create()
            .setProjectName( this.projectName == null ? null : ProjectName.from( this.projectName ) )
            .setBranch( this.branch == null ? null : Branch.from( this.branch ) );

        // a key is an id, or a path when it starts with a slash
        if ( this.key != null && this.key.startsWith( "/" ) )
        {
            params.setContentPath( ContentPath.from( this.key ) );
        }
        else if ( this.key != null )
        {
            params.setContentId( ContentId.from( this.key ) );
        }

        return urlServiceSupplier.get().portalScope( params.build() );
    }

    public String baseUrlOf( final PortalScope scope )
    {
        return scope.baseUrl();
    }
}
