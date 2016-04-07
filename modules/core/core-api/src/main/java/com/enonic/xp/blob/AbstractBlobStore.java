package com.enonic.xp.blob;

public class AbstractBlobStore
{
    protected final SegmentsCollectionMap segmentsCollectionMap;

    public AbstractBlobStore( final Builder builder )
    {
        this.segmentsCollectionMap = builder.map;
    }

    protected String getCollectionName( final Segment segment )
    {
        final String bucketName = this.segmentsCollectionMap.get( segment );

        if ( bucketName == null )
        {
            throw new BlobStoreException( "Bucket not found for segment [" + segment.getValue() + "]" );
        }

        return bucketName;
    }


    public static class Builder<B extends Builder>
    {
        private SegmentsCollectionMap map;

        @SuppressWarnings("unchecked")
        public B segmentCollectionMap( final SegmentsCollectionMap map )
        {
            this.map = map;
            return (B) this;
        }
    }


}
