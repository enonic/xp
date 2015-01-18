module api.rendering {

    export class UriHelper {

        public static getPortalUri(path: string, renderingMode: RenderingMode, workspace: api.content.Workspace): string {
            path = api.util.UriHelper.relativePath(path);

            var workspaceName: string;

            switch (workspace) {
            case api.content.Workspace.STAGE:
                workspaceName = "stage";
                break;
            case api.content.Workspace.PROD:
                workspaceName = "prod";
                break;
            default:
                workspaceName = "stage";
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
                                      workspace: api.content.Workspace): string {
            return UriHelper.getPortalUri(contentId + "/_/component/" + componentPath, renderingMode, workspace);
        }

    }
}
