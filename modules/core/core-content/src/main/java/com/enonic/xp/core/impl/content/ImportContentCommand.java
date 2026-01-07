package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.content.ImportContentResult;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.RefreshMode;

final class ImportContentCommand
    extends AbstractContentCommand
{
    private final ImportContentParams params;

    private ImportContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    static Builder create()
    {
        return new Builder();
    }

    ImportContentResult execute()
    {
        final Node importNode = ImportContentFactory.create().
            params( params ).
            build().execute();

        final ImportNodeParams importNodeParams = ImportNodeParams.create().importNode( importNode )
            .binaryAttachments( getAttachments() )
            .insertManualStrategy( params.getContent().getManualOrderValue() != null ? InsertManualStrategy.MANUAL : null )
            .importPermissions( params.isImportPermissions() )
            .importPermissionsOnCreate( params.isImportPermissionsOnCreate() )
            .versionAttributes( ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.IMPORT_ATTR ) )
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

        private Builder()
        {
        }

        Builder params( final ImportContentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public ImportContentCommand build()
        {
            validate();
            return new ImportContentCommand( this );
        }
    }

}
