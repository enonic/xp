module admin.lib.uri {

    /**
     * Base URI for administration console. Set from the initializing html page.
     */
    export var baseUri:String;

    /**
     * Creates a absolute URI from supplied URI.
     *
     * @param uri uri to append to base URI.
     * @returns {string} the absolute URI (base + uri).
     */
    export function getAbsoluteUri(uri:String):String {
        return this.baseUri + '/' + uri;
    }
}
