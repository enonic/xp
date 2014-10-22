package com.enonic.wem.core.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.DuplicateContentParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;

final class DuplicateContentCommand
    extends AbstractContentCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( DuplicateContentCommand.class );

    private final DuplicateContentParams params;

    private DuplicateContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    Content execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        final Node createdNode = nodeService.duplicate( NodeId.from( params.getContentId() ) );

        return translator.fromNode( createdNode );
    }

    public static Builder create( final DuplicateContentParams params )
    {
        return new Builder( params );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private DuplicateContentParams params;

        public Builder( final DuplicateContentParams params )
        {
            super();
            this.params = params;
        }

        void validate()
        {
            super.validate();
        }

        public DuplicateContentCommand build()
        {
            validate();
            return new DuplicateContentCommand( this );
        }
    }

}
