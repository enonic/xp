module api.util {

    export class UriHelper {

        private static DEFAULT_URI = '/';

        /**
         * Creates an URI from supplied path.
         * Expects window.CONFIG to be present.
         *
         * @param path path to append to base URI.
         * @returns {string} the URI (base + path).
         */
        static getUri(path: string): string {
            var basePath = window['CONFIG'] && window['CONFIG']['baseUri'] || UriHelper.DEFAULT_URI;
            return UriHelper.joinPath(basePath, UriHelper.relativePath(path));
        }

        /**
         * Creates an URI to an admin path.
         *
         * @param path path to append to base admin URI.
         * @returns {string} the URI to a admin path.
         */
        static getAdminUri(path: string): string {
            return UriHelper.getUri(UriHelper.joinPath('admin', UriHelper.relativePath(path)));
        }

        /**
         * Creates an URI to a rest service.
         *
         * @param path path to append to base rest URI.
         * @returns {string} the URI to a rest service.
         */
        static getRestUri(path: string): string {
            return UriHelper.getAdminUri(UriHelper.joinPath('rest', UriHelper.relativePath(path)));
        }

        /**
         * Creates an URI to a portal path.
         *
         * @param path path to append to base portal URI.
         * @returns {string} the URI to a portal path.
         */
        static getPortalUri(path: string): string {
            return UriHelper.getUri(UriHelper.joinPath('admin', '/portal', UriHelper.relativePath(path)));
        }

        static relativePath(path: string): string {
            if (StringHelper.isBlank(path)) {
                return StringHelper.EMPTY_STRING;
            }
            return path.charAt(0) == '/' ? path.substring(1) : path;
        }

        static joinPath(...paths: string[]): string {
            // using grouping here in order to not replace :// because js doesn't support lookbehinds
            return StringHelper.removeEmptyStrings(paths).join('/').replace(/(^|[^:])\/{2,}/g, '$1/');
        }

        static getUrlLocation(url: string): string {
            return StringHelper.isBlank(url) ? StringHelper.EMPTY_STRING : url.split(/\?|&/i)[0];
        }

        static decodeUrlParams(url: string): {[key: string]: string} {
            if (StringHelper.isBlank(url)) {
                return {};
            }
            var array = url.split(/\?|&/i);
            var params: {[name: string]: string} = {};
            var param;
            if (array.length > 1) {
                for (var i = 1; i < array.length; i++) {
                    param = array[i].split('=');
                    params[param[0]] = param.length > 1 ? decodeURIComponent(param[1]) : undefined;
                }
            }
            return params;
        }

        /**
         * Serializes an object to query string params.
         * Supports nested objects and arrays.
         *
         * @param params
         * @param prefix
         * @returns {string}
         */
        static encodeUrlParams(params: Object, prefix?: string): string {
            if (!params) {
                return StringHelper.EMPTY_STRING;
            }
            var urlArray = [];
            for (var key in params) {
                if (params.hasOwnProperty(key) && params[key] != undefined) {
                    var value = params[key];
                    var prefixedKey = prefix ? prefix + "[" + key + "]" : key;
                    if (typeof value == "object") {
                        urlArray.push(this.encodeUrlParams(value, prefixedKey));
                    } else {
                        urlArray.push(encodeURIComponent(prefixedKey) + "=" + encodeURIComponent(value));
                    }
                }
            }
            return urlArray.join("&");
        }
    }
}
