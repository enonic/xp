package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.content.ImportContentResult;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.Node;

final class ImportContentCommand
    extends AbstractContentCommand
{
    private final ImportContentParams params;

    private final ContentDataSerializer contentDataSerializer;

    private ImportContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.contentDataSerializer = builder.contentDataSerializer;
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
            contentDataSerializer( contentDataSerializer ).
            build().execute();

        final ImportNodeParams importNodeParams = ImportNodeParams.create().
            importNode( importNode ).
            binaryAttachments( params.getBinaryAttachments() ).
            insertManualStrategy( params.getInsertManualStrategy() ).
            dryRun( params.isDryRun() ).
            importPermissions( params.isImportPermissions() ).
            importPermissionsOnCreate( params.isImportPermissionsOnCreate() ).
            build();

        final ImportNodeResult result = nodeService.importNode( importNodeParams );

        return ImportContentResult.create().
            content( translator.fromNode( result.getNode(), false ) ).
            build();
    }

    static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ImportContentParams params;

        private ContentDataSerializer contentDataSerializer;

        private Builder()
        {
        }

        Builder params( final ImportContentParams params )
        {
            this.params = params;
            return this;
        }

        Builder contentDataSerializer( final ContentDataSerializer contentDataSerializer )
        {
            this.contentDataSerializer = contentDataSerializer;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params, "params must be given" );
            Preconditions.checkNotNull( contentDataSerializer, "contentDataSerializer must be given" );
        }

        public ImportContentCommand build()
        {
            validate();
            return new ImportContentCommand( this );
        }
    }

}
