package com.enonic.xp.portal.impl.handler;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.handler.attachment.AttachmentHandlerWorker;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(service = UniversalApiHandler.class, property = {"key=" + AttachmentMediaHandler.ATTACHMENT_API,
    "displayName=Attachment Media API", "allowedPrincipals=role:system.everyone", "mount=xp"}, configurationPid = "com.enonic.xp.portal")
public class AttachmentMediaHandler
    extends MediaHandlerBase
{
    static final String ATTACHMENT_API = "media:attachment";

    @Activate
    public AttachmentMediaHandler( @Reference final ContentService contentService, @Reference final ProjectService projectService )
    {
        super( contentService, projectService );
    }

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        doActivate( config );
    }

    @Override
    public WebResponse handle( final WebRequest webRequest )
    {
        final String path = WebHandlerHelper.findApiPath( webRequest, ATTACHMENT_API );
        final AttachmentPathParser pathParser = new AttachmentPathParser( path );
        final PathMetadata pathMetadata = pathParser.parse();

        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( ALLOWED_METHODS );
        }

        checkArguments( webRequest, pathMetadata );

        return ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( pathMetadata.repositoryId )
            .branch( pathMetadata.branch )
            .build()
            .callWith( () -> {
                final AttachmentHandlerWorker worker = new AttachmentHandlerWorker( webRequest, this.contentService );

                worker.download = HandlerHelper.getParameter( webRequest, "download" ) != null;
                worker.id = pathMetadata.contentId;
                worker.fingerprint = pathMetadata.fingerprint;
                worker.name = pathMetadata.name;
                worker.privateCacheControlHeaderConfig = this.privateCacheControlHeaderConfig;
                worker.publicCacheControlHeaderConfig = this.publicCacheControlHeaderConfig;
                worker.contentSecurityPolicy = this.contentSecurityPolicy;
                worker.contentSecurityPolicySvg = this.contentSecurityPolicySvg;
                worker.branch = pathMetadata.branch;

                return worker.execute();
            } );
    }


    private static final class AttachmentPathParser
        extends PathParser<PathMetadata>
    {
        // Attachment path is: "/{project[:draft]}/{id[:fingerprint]}/{name}"

        static final int NAME_INDEX = 2;

        static final int PATH_VARIABLES_LIMIT = 4;

        AttachmentPathParser( final String path )
        {
            super( path, PATH_VARIABLES_LIMIT );
        }

        PathMetadata parse()
        {
            final PathMetadata metadata = doParse();

            metadata.name = pathVariables[NAME_INDEX];

            return metadata;
        }

        @Override
        PathMetadata createMetadata()
        {
            return new PathMetadata();
        }
    }
}
