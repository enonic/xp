package com.enonic.xp.repo.impl.node.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.util.BinaryReference;

final class AttachedBinaryJson
{
    @JsonProperty("binaryReference")
    private String binaryReference;

    @JsonProperty("blobKey")
    private String blobKey;

    public AttachedBinary fromJson()
    {
        return new AttachedBinary( BinaryReference.from( binaryReference ), blobKey );
    }

    public static AttachedBinaryJson toJson( final AttachedBinary attachedBinary )
    {
        final AttachedBinaryJson attachedBinaryJson = new AttachedBinaryJson();

        attachedBinaryJson.binaryReference = attachedBinary.getBinaryReference().toString();

        attachedBinaryJson.blobKey = attachedBinary.getBlobKey();

        return attachedBinaryJson;
    }
}
