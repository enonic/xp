package com.enonic.wem.admin.rest;

import com.google.inject.Singleton;

import com.enonic.wem.admin.json.rpc.controller.JsonRpcController;
import com.enonic.wem.admin.rest.provider.JsonObjectProvider;
import com.enonic.wem.admin.rest.provider.JsonSerializableProvider;
import com.enonic.wem.admin.rest.resource.account.AccountExportResource;
import com.enonic.wem.admin.rest.resource.account.AccountImageResource;
import com.enonic.wem.admin.rest.resource.auth.AuthResource;
import com.enonic.wem.admin.rest.resource.content.ContentImageResource;
import com.enonic.wem.admin.rest.resource.jcr.GetNodesResource;
import com.enonic.wem.admin.rest.resource.relationship.RelationshipResource;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageResource;
import com.enonic.wem.admin.rest.resource.space.SpaceImageResource;
import com.enonic.wem.admin.rest.resource.space.SpaceResource;
import com.enonic.wem.admin.rest.resource.status.StatusResource;
import com.enonic.wem.admin.rest.resource.tools.ToolsResource;
import com.enonic.wem.admin.rest.resource.upload.UploadResource;
import com.enonic.wem.admin.rest.resource.util.CountryResource;
import com.enonic.wem.admin.rest.resource.util.LocaleResource;
import com.enonic.wem.admin.rest.resource.util.TimeZoneResource;
import com.enonic.wem.admin.rest.ui.BackgroundImageResource;
import com.enonic.wem.web.jaxrs.JaxRsServlet;

@Singleton
public final class RestServlet
    extends JaxRsServlet
{
    @Override
    protected void configure()
    {
        addClass( JsonObjectProvider.class );
        addClass( JsonSerializableProvider.class );

        addClass( BackgroundImageResource.class );
        addClass( AccountExportResource.class );
        addClass( AccountImageResource.class );
        addClass( ContentImageResource.class );
        addClass( SchemaImageResource.class );
        addClass( GetNodesResource.class );
        addClass( UploadResource.class );
        addClass( AuthResource.class );
        addClass( ToolsResource.class );
        addClass( StatusResource.class );

        addClass( SpaceResource.class );
        addClass( SpaceImageResource.class );

        addClass( RelationshipResource.class );

        addClass( CountryResource.class );
        addClass( TimeZoneResource.class );
        addClass( LocaleResource.class );

        addSingleton( JsonRpcController.class );
    }
}
