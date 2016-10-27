module api.util {

    export class UriHelper {

        private static DEFAULT_URI = '/';
        private static DEFAULT_ADMIN_URI = '/admin';

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
            var adminUri = UriHelper.getAdminUriPrefix();
            return UriHelper.getUri(UriHelper.joinPath(adminUri, UriHelper.relativePath(path)));
        }

        /**
         * Gets the URI prefix of an admin path.
         *
         * @param path path to append to base admin URI.
         * @returns {string} the URI to a admin path.
         */
        static getAdminUriPrefix(): string {
            return window['CONFIG'] && window['CONFIG']['adminUrl'] || UriHelper.DEFAULT_ADMIN_URI;
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
         * Creates an URI to an admin tool.
         *
         * @param path path to append to base rest URI.
         * @returns {string} the URI to a rest service.
         */
        static getToolUri(path: string): string {
            return UriHelper.getAdminUri(UriHelper.joinPath('tool', UriHelper.relativePath(path)));
        }

        /**
         * Creates an URI to a portal path.
         *
         * @param path path to append to base portal URI.
         * @returns {string} the URI to a portal path.
         */
        static getPortalUri(path: string): string {
            return UriHelper.getAdminUri(UriHelper.joinPath('portal', UriHelper.relativePath(path)));
        }

        static relativePath(path: string): string {
            if (StringHelper.isBlank(path)) {
                return StringHelper.EMPTY_STRING;
            }
            return path.charAt(0) == '/' ? path.substring(1) : path;
        }

        static isNavigatingOutsideOfXP(href: string, contentWindow: Window): boolean {
            // href should start with '/' or after replacing window's protocol and host not be equal to basic href value
            return href.charAt(0) == '/' ? false : UriHelper.trimWindowProtocolAndPortFromHref(href, contentWindow) == href;
        }

        static trimWindowProtocolAndPortFromHref(href: string, contentWindow: Window) {
            var location: Location = contentWindow.location;
            return UriHelper.relativePath(href.replace(location.protocol + "//" + location.host, ""));
        }

        static trimAnchor(trimMe: string): string {
            var index = trimMe.lastIndexOf("#");
            return index >= 0 ? UriHelper.relativePath(trimMe.substring(0, index)) : UriHelper.relativePath(trimMe);
        }

        static trimUrlParams(trimMe: string): string {
            var index = trimMe.lastIndexOf("?");
            return index >= 0 ? trimMe.substring(0, index) : trimMe;
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
        static encodeUrlParams(params: {[name: string]: any}, prefix?: string): string {
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

        static appendUrlParams(url: string, params: {[name: string]: any}): string {
            if (!params || Object.keys(params).length == 0) {
                return url;
            }

            let urlParams = UriHelper.decodeUrlParams(url),
                hasParams = Object.keys(urlParams).length > 0;

            return url + (hasParams ? '&' : '?') + UriHelper.encodeUrlParams(params);
        }
    }
}
