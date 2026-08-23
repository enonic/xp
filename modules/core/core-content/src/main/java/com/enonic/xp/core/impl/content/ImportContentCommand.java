package com.enonic.xp.core.impl.content;

import java.util.Map;

import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.content.ImportContentResult;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.RefreshMode;

import static java.util.Objects.requireNonNull;

final class ImportContentCommand
    extends AbstractContentCommand
{
    private final ImportContentParams params;

    private final String orderKey;

    private ImportContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.orderKey = builder.orderKey;
    }

    static Builder create()
    {
        return new Builder();
    }

    ImportContentResult execute()
    {
        final Node importNode = ImportContentFactory.create().params( params ).orderKey( orderKey ).build().execute();

        final ImportNodeParams importNodeParams = ImportNodeParams.create()
            .importNode( importNode )
            .binaryAttachments( getAttachments() )
            .insertManualStrategy( params.getContent().getManualOrderValue() != null ? InsertManualStrategy.MANUAL : null )
            .importPermissions( params.isImportPermissions() )
            .importPermissionsOnCreate( params.isImportPermissionsOnCreate() )
            .versionAttributesResolver( ContentAttributesHelper.versionHistoryResolver( ContentAttributesHelper.SYNC_ATTR, Map.of() ) )
            .refresh( RefreshMode.ALL )
            .build();

        final ImportNodeResult result = nodeService.importNode( importNodeParams );

        return ImportContentResult.create().content( ContentNodeTranslator.fromNode( result.getNode() ) ).build();
    }

    private BinaryAttachments getAttachments()
    {
        if ( params.getAttachments() != null )
        {
            return params.getAttachments()
                .stream()
                .map( a -> new BinaryAttachment( a.getBinaryReference(), a.getByteSource() ) )
                .collect( BinaryAttachments.collector() );
        }
        else
        {
            return null;
        }
    }

    static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ImportContentParams params;

        private String orderKey;

        private Builder()
        {
        }

        Builder params( final ImportContentParams params )
        {
            this.params = params;
            return this;
        }

        Builder orderKey( final String orderKey )
        {
            this.orderKey = orderKey;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( params, "params cannot be null" );
        }

        public ImportContentCommand build()
        {
            validate();
            return new ImportContentCommand( this );
        }
    }

}
