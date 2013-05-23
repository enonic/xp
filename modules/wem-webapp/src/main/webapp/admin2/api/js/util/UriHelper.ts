module API.util {

    /**
     * Base URI for administration console. Set from the initializing html page.
     */
    export var baseUri:string = '../../..';

    /**
     * Creates a absolute URI from supplied URI.
     *
     * @param uri uri to append to base URI.
     * @returns {string} the absolute URI (base + uri).
     */
    export function getAbsoluteUri(uri:string):string {
        return this.baseUri + '/' + uri;
    }
}
