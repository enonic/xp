package com.enonic.wem.core.entity.relationship;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.index.IndexConfigDocumentNew;
import com.enonic.wem.api.index.PatternBasedIndexConfigDocument;
import com.enonic.wem.core.entity.PatternBasedIndexConfigDocumentJson;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type(value = PatternBasedIndexConfigDocumentJson.class, name = "PatternBasedIndexConfigDocument")})
public abstract class IndexConfigDocumentJson
{
    public IndexConfigDocumentJson from( final IndexConfigDocumentNew indexConfigDocument )
    {
        if ( indexConfigDocument instanceof PatternBasedIndexConfigDocument )
        {
            return new PatternBasedIndexConfigDocumentJson( (PatternBasedIndexConfigDocument) indexConfigDocument );
        }

        throw new RuntimeException( "Not able to translate IndexConfigDocument of type " + indexConfigDocument.getClass() );
    }

    public abstract IndexConfigDocumentNew toEntityIndexConfig();

}
