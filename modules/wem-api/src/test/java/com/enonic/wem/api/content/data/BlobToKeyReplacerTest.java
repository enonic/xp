package com.enonic.wem.api.content.data;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.datatype.DataTypes;

import static com.enonic.wem.api.content.Content.newContent;


public class BlobToKeyReplacerTest
{
    @Test
    public void given_data_with_blob_when_replace_then_data_contains_BlobKey_as_value()
    {
        MockBlobKeyResolver resolver = new MockBlobKeyResolver();
        BlobToKeyReplacer blobToKeyReplacer = new BlobToKeyReplacer( resolver );
        Content content = newContent().build();
        content.setData( "myBlob", new byte[]{1, 2, 3}, DataTypes.BLOB );
        blobToKeyReplacer.replace( content.getData() );
        Assert.assertTrue( content.getData( "myBlob" ).getValue() instanceof BlobKey );
    }
}
