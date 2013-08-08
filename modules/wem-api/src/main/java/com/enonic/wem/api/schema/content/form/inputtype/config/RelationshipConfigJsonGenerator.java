package com.enonic.wem.api.schema.content.form.inputtype.config;

import com.enonic.wem.api.schema.content.form.inputtype.RelationshipConfig;

public class RelationshipConfigJsonGenerator
    extends AbstractInputTypeConfigJsonGenerator<RelationshipConfig>
{
    public static final RelationshipConfigJsonGenerator DEFAULT = new RelationshipConfigJsonGenerator();

    @Override
    public AbstractInputTypeConfigJson generate( final RelationshipConfig config )
    {
        return new RelationshipConfigJson( config );
    }
}
