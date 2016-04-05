module api.rendering {

    export class UriHelper {

        public static getPortalUri(path: string, renderingMode: RenderingMode, workspace: api.content.Branch): string {
            var elementDivider = api.content.ContentPath.ELEMENT_DIVIDER;
            path = api.util.UriHelper.relativePath(path);

            var workspaceName: string = api.content.Branch[workspace].toLowerCase();
            var renderingModeName: string = RenderingMode[renderingMode].toLowerCase();

            return api.util.UriHelper.getPortalUri(renderingModeName + elementDivider + workspaceName + elementDivider + path);
        }

        public static getPathFromPortalPreviewUri(portalUri: string, renderingMode: RenderingMode, workspace: api.content.Branch): string {
            var workspaceName: string = api.content.Branch[workspace].toLowerCase();
            var renderingModeName: string = RenderingMode[renderingMode].toLowerCase();

            var elementDivider = api.content.ContentPath.ELEMENT_DIVIDER;
            var searchEntry = renderingModeName + elementDivider + workspaceName;

            var index = portalUri.indexOf(searchEntry);
            if (index > -1) {
                return portalUri.substring(index + searchEntry.length);
            } else {
                return null;
            }
        }

        public static getComponentUri(contentId: string, componentPath: api.content.page.region.ComponentPath, renderingMode: RenderingMode,
                                      workspace: api.content.Branch): string {
            var elementDivider = api.content.ContentPath.ELEMENT_DIVIDER,
                componentPart = elementDivider + "_" + elementDivider + "component"  + elementDivider;
            var componentPathStr = componentPath ? componentPath.toString() : "";
            return UriHelper.getPortalUri(contentId + componentPart + componentPathStr, renderingMode, workspace);
        }

        public static getAdminUri(baseUrl: string, contentPath: string): string {
            return UriHelper.getPortalUri(contentPath, RenderingMode.ADMIN, api.content.Branch.DRAFT) +
                   api.content.ContentPath.ELEMENT_DIVIDER + baseUrl;
        }
    }
}
