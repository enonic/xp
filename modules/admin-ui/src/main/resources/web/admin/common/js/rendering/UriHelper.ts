module api.rendering {

    export class UriHelper {

        public static getPortalUri(path: string, renderingMode: RenderingMode, workspace: api.content.Branch): string {
            path = api.util.UriHelper.relativePath(path);

            var workspaceName: string;

            switch (workspace) {
            case api.content.Branch.DRAFT:
                workspaceName = "draft";
                break;
            case api.content.Branch.MASTER:
                workspaceName = "master";
                break;
            default:
                workspaceName = "draft";
            }

            switch (renderingMode) {
            case RenderingMode.EDIT:
                return api.util.UriHelper.getPortalUri('edit/' + workspaceName + '/' + path);
            case RenderingMode.LIVE:
                return api.util.UriHelper.getPortalUri('live/' + workspaceName + '/' + path);
            case RenderingMode.PREVIEW:
                return api.util.UriHelper.getPortalUri('preview/' + workspaceName + '/' + path);
            }
        }

        public static getComponentUri(contentId: string, componentPath: string, renderingMode: RenderingMode,
                                      workspace: api.content.Branch): string {
            return UriHelper.getPortalUri(contentId + "/_/component/" + componentPath, renderingMode, workspace);
        }

    }
}
