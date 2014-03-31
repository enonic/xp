module api.util {

    export enum RenderingMode {
        EDIT,
        PREVIEW,
        LIVE
    }

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

    export function getPortalUri(path: string, renderingMode: RenderingMode): string {
        path = escapePath(path);
        switch (renderingMode) {
        case RenderingMode.EDIT:
            return api.util.getUri('portal/edit/' + path);
        case RenderingMode.LIVE:
            return api.util.getUri('portal/live/' + path);
        case RenderingMode.PREVIEW:
            return api.util.getUri('portal/preview/' + path);
        }

    }

    export function getComponentUri(contentId: string, componentPath: string, renderingMode: RenderingMode) {
        return api.util.getPortalUri(contentId + "/_/component/" + componentPath, renderingMode);
    }

    export function escapePath(path: string): string {
        return path.charAt(0) == '/' ? path.substring(1) : path;
    }

}
