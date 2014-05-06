module api.rendering {

    export class UriHelper {

        public static getPortalUri(path: string, renderingMode: RenderingMode): string {
            path = UriHelper.relativePath(path);

            switch (renderingMode) {
            case RenderingMode.EDIT:
                return api.util.getUri('portal/edit/' + path);
            case RenderingMode.LIVE:
                return api.util.getUri('portal/live/' + path);
            case RenderingMode.PREVIEW:
                return api.util.getUri('portal/preview/' + path);
            }

        }

        public static getComponentUri(contentId: string, componentPath: string, renderingMode: RenderingMode) {
            return UriHelper.getPortalUri(contentId + "/_/component/" + componentPath, renderingMode);
        }

        private static relativePath(path: string): string {
            return path.charAt(0) == '/' ? path.substring(1) : path;
        }
    }

}
