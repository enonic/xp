package com.enonic.wem.core.blobstore;

import java.io.IOException;
import java.io.InputStream;

import com.enonic.wem.api.blob.BlobKey;

/**
 * Append-only store for binary streams. A blob store consists of a number
 * of identifiable blob records that each contain a distinct binary stream.
 * New binary streams can be added to the blob store, but existing streams
 * are never removed or modified.
 */
public interface BlobStore
{
    /**
     * Check if a record for the given identifier exists, and return it if yes. If no record exists,
     * this method returns null.
     *
     * @param key blob key
     * @return the record if found, null otherwise
     * @throws IOException if an error occurred
     */
    public BlobRecord getRecord( BlobKey key )
        throws BlobStoreException;

    /**
     * Creates a new blob record. The given binary stream is consumed and
     * a binary record containing the consumed stream is created and returned.
     * If the same stream already exists in another record, then that record
     * is returned instead of creating a new one.
     *
     * @param in binary stream
     * @return blob record that contains the given stream
     * @throws IOException if an error occurred
     */
    public BlobRecord addRecord( InputStream in )
        throws BlobStoreException;

    /**
     * Get all stored keys.
     *
     * @return an iterator over all key objects
     * @throws IOException if an error occurred
     */
    public Iterable<BlobKey> getAllKeys()
        throws BlobStoreException;

    /**
     * Delete blobs from blobstore.
     *
     * @param key blob key
     * @return true if deleted, false otherwise
     * @throws IOException if an error occurred
     */
    public boolean deleteRecord( BlobKey key )
        throws BlobStoreException;

}
