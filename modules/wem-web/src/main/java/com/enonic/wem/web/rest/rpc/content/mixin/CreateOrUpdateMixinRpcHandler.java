package com.enonic.wem.web.rest.rpc.content.mixin;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.content.mixin.CreateMixin;
import com.enonic.wem.api.command.content.mixin.GetMixins;
import com.enonic.wem.api.command.content.mixin.UpdateMixins;
import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.api.content.mixin.MixinEditor;
import com.enonic.wem.api.content.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.mixin.QualifiedMixinNames;
import com.enonic.wem.core.content.mixin.MixinXmlSerializer;
import com.enonic.wem.core.support.serializer.ParsingException;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.service.upload.UploadItem;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.content.mixin.MixinEditors.setMixin;

@Component
public class CreateOrUpdateMixinRpcHandler
    extends AbstractDataRpcHandler
{
    private final MixinXmlSerializer mixinXmlSerializer = new MixinXmlSerializer();

    private UploadService uploadService;

    public CreateOrUpdateMixinRpcHandler()
    {
        super( "mixin_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String mixinJson = context.param( "mixin" ).required().asString();
        final String iconReference = context.param( "iconReference" ).asString();
        final Mixin mixin;
        try
        {
            mixin = mixinXmlSerializer.toMixin( mixinJson );
        }
        catch ( ParsingException e )
        {
            context.setResult( new JsonErrorResult( "Invalid Mixin format" ) );
            return;
        }

        final Icon icon = getIconUploaded( iconReference );
        if ( !mixinExists( mixin.getQualifiedName() ) )
        {
            final CreateMixin createCommand = mixin().create().
                displayName( mixin.getDisplayName() ).
                formItem( mixin.getFormItem() ).
                moduleName( mixin.getModuleName() ).
                icon( icon );
            client.execute( createCommand );
            context.setResult( CreateOrUpdateMixinJsonResult.created() );
        }
        else
        {
            final QualifiedMixinNames names = QualifiedMixinNames.from( mixin.getQualifiedName() );
            final MixinEditor mixinEditor = setMixin( mixin.getDisplayName(), mixin.getFormItem(), icon );
            final UpdateMixins updateCommand = mixin().update().qualifiedNames( names ).editor( mixinEditor );

            client.execute( updateCommand );
            context.setResult( CreateOrUpdateMixinJsonResult.updated() );
        }
    }

    private boolean mixinExists( final QualifiedMixinName qualifiedName )
    {
        final GetMixins getMixins = mixin().get().names( QualifiedMixinNames.from( qualifiedName ) );
        return !client.execute( getMixins ).isEmpty();
    }

    private Icon getIconUploaded( final String iconReference )
        throws IOException
    {
        if ( iconReference == null )
        {
            return null;
        }
        final UploadItem uploadItem = uploadService.getItem( iconReference );
        if ( uploadItem != null )
        {
            final byte[] iconData = getUploadedImageData( uploadItem );
            return uploadItem != null ? Icon.from( iconData, uploadItem.getMimeType() ) : null;
        }
        return null;
    }

    private byte[] getUploadedImageData( final UploadItem uploadItem )
        throws IOException
    {
        if ( uploadItem != null )
        {
            final File file = uploadItem.getFile();
            if ( file.exists() )
            {
                return FileUtils.readFileToByteArray( file );
            }
        }
        return null;
    }

    @Autowired
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }

}
