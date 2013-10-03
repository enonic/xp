module api_util {

    /**
     * Base URI for administration console. Set from the initializing html page.
     */
    export var baseUri:string = '../../..';

    /**
     * Creates an URI from supplied path.
     *
     * @param path path to append to base URI.
     * @returns {string} the URI (base + path).
     */
    export function getUri(path:string):string {
        return this.baseUri + '/' + path;
    }

    /**
     * Creates an URI to an admin path.
     *
     * @param path path to append to base admin URI.
     * @returns {string} the URI to a admin path.
     */
    export function getAdminUri(path:string):string {
        return api_util.getUri('admin/' + path);
    }

    /**
     * Creates an URI to a rest service.
     *
     * @param path path to append to base rest URI.
     * @returns {string} the URI to a rest service.
     */
    export function getRestUri(path:string):string {
        return api_util.getAdminUri('rest/' + path);
    }
}
