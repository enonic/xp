package com.enonic.wem.core.entity.relationship;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.core.entity.PatternBasedIndexConfigDocumentJson;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type(value = PatternBasedIndexConfigDocumentJson.class, name = "PatternBasedIndexConfigDocument")})
public abstract class IndexConfigDocumentJson
{
    @SuppressWarnings("UnusedDeclaration")
    public IndexConfigDocumentJson from( final IndexConfigDocument indexConfigDocument )
    {
        if ( indexConfigDocument instanceof PatternIndexConfigDocument )
        {
            return new PatternBasedIndexConfigDocumentJson( (PatternIndexConfigDocument) indexConfigDocument );
        }

        throw new RuntimeException( "Not able to translate IndexConfigDocument of type " + indexConfigDocument.getClass() );
    }

    public abstract IndexConfigDocument toEntityIndexConfig();

}
