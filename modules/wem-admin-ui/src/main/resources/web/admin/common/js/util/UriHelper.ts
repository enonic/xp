module api.util {

    /**
     * Creates an URI from supplied path.
     *
     * @param path path to append to base URI.
     * @returns {string} the URI (base + path).
     */
    export function getUri(path: string): string {
        return window['CONFIG']['baseUri'] + '/' + path;
    }

    /**
     * Creates an URI to an admin path.
     *
     * @param path path to append to base admin URI.
     * @returns {string} the URI to a admin path.
     */
    export function getAdminUri(path: string): string {
        return api.util.getUri('admin/' + path);
    }

    /**
     * Creates an URI to a rest service.
     *
     * @param path path to append to base rest URI.
     * @returns {string} the URI to a rest service.
     */
    export function getRestUri(path: string): string {
        return api.util.getAdminUri('rest/' + path);
    }

    function escapePath(path: string): string {
        return path.charAt(0) == '/' ? path.substring(1) : path;
    }

    export function getUrlLocation(url: string): string {
        return url.split(/\?|&/i)[0];
    }

    export function decodeUrlParams(url: string): {[key: string]: string
    } {
        var array = url.split(/\?|&/i);
        var params: {[name: string]: string} = {};
        var param;
        if (array.length > 1) {
            for (var i = 1; i < array.length; i++) {
                param = array[i].split('=');
                params[param[0]] = param.length > 1 ? param[1] : undefined;
            }
        }
        return params;
    }

    export function encodeUrlParams(params: Object): string {
        var url = "";
        for (var key in params) {
            if (params.hasOwnProperty(key) && params[key] != undefined) {
                url += "&" + key + "=" + params[key];
            }
        }
        return url.length > 0 ? ("?" + url.substr(1)) : url;
    }

}
