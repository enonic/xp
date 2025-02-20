package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.content.ImportContentResult;
import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeResult;
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
        return doExecute();
    }

    private ImportContentResult doExecute()
    {
        final Node importNode = ImportContentFactory.create().
            params( params ).
            contentDataSerializer( this.translator.getContentDataSerializer() ).
            build().execute();

        final ImportNodeParams importNodeParams = ImportNodeParams.create().importNode( importNode )
            .binaryAttachments( params.getBinaryAttachments() )
            .insertManualStrategy( params.getInsertManualStrategy() )
            .importPermissions( params.isImportPermissions() )
            .importPermissionsOnCreate( params.isImportPermissionsOnCreate() )
            .refresh( RefreshMode.ALL )
            .build();

        final ImportNodeResult result = nodeService.importNode( importNodeParams );

        return ImportContentResult.create().content( translator.fromNode( result.getNode(), false ) ).build();
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
            Preconditions.checkNotNull( params, "params must be given" );
        }

        public ImportContentCommand build()
        {
            validate();
            return new ImportContentCommand( this );
        }
    }

}
