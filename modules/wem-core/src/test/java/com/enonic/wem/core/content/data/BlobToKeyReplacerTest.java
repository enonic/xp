package com.enonic.wem.core.content.data;

import org.junit.Test;

import com.enonic.wem.core.content.Content;
import com.enonic.wem.core.content.datatype.DataTypes;

import com.enonic.cms.framework.blob.BlobKey;

import static org.junit.Assert.*;


public class BlobToKeyReplacerTest
{
    @Test
    public void given_data_with_blob_when_replace_then_data_contains_BlobKey_as_value()
    {
        MockBlobKeyResolver resolver = new MockBlobKeyResolver();
        BlobToKeyReplacer blobToKeyReplacer = new BlobToKeyReplacer( resolver );
        Content content = new Content();
        content.setData( "myBlob", new byte[]{1, 2, 3}, DataTypes.BLOB );
        blobToKeyReplacer.replace( content.getData() );
        assertTrue( content.getData( "myBlob" ).getValue() instanceof BlobKey );
    }
}
