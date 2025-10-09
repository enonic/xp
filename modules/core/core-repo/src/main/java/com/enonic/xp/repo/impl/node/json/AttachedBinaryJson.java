package com.enonic.xp.repo.impl.node.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.util.BinaryReference;

public final class AttachedBinaryJson
{
    @JsonProperty("binaryReference")
    private String binaryReference;

    @JsonProperty("blobKey")
    private String blobKey;

    public static AttachedBinary fromJson( final AttachedBinaryJson json )
    {
        return new AttachedBinary( BinaryReference.from( json.binaryReference ), json.blobKey );
    }

    public static AttachedBinaryJson toJson( final AttachedBinary attachedBinary )
    {
        final AttachedBinaryJson attachedBinaryJson = new AttachedBinaryJson();

        attachedBinaryJson.binaryReference = attachedBinary.getBinaryReference().toString();

        attachedBinaryJson.blobKey = attachedBinary.getBlobKey().toString();

        return attachedBinaryJson;
    }
}
