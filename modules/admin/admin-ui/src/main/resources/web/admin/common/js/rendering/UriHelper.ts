module api.rendering {

    export class UriHelper {

        public static getPortalUri(path: string, renderingMode: RenderingMode, workspace: api.content.Branch): string {
            let elementDivider = api.content.ContentPath.ELEMENT_DIVIDER;
            path = api.util.UriHelper.relativePath(path);

            let workspaceName: string = api.content.Branch[workspace].toLowerCase();
            let renderingModeName: string = RenderingMode[renderingMode].toLowerCase();

            return api.util.UriHelper.getPortalUri(renderingModeName + elementDivider + workspaceName + elementDivider + path);
        }

        public static getPathFromPortalPreviewUri(portalUri: string, renderingMode: RenderingMode, workspace: api.content.Branch): string {
            let workspaceName: string = api.content.Branch[workspace].toLowerCase();
            let renderingModeName: string = RenderingMode[renderingMode].toLowerCase();

            let elementDivider = api.content.ContentPath.ELEMENT_DIVIDER;
            let searchEntry = renderingModeName + elementDivider + workspaceName;

            let index = portalUri.indexOf(searchEntry);
            if (index > -1) {
                return portalUri.substring(index + searchEntry.length);
            } else {
                return null;
            }
        }

        public static getComponentUri(contentId: string, componentPath: api.content.page.region.ComponentPath, renderingMode: RenderingMode,
                                      workspace: api.content.Branch): string {
            let elementDivider = api.content.ContentPath.ELEMENT_DIVIDER;
            let componentPart = elementDivider + '_' + elementDivider + 'component' + elementDivider;
            let componentPathStr = componentPath ? componentPath.toString() : '';
            return UriHelper.getPortalUri(contentId + componentPart + componentPathStr, renderingMode, workspace);
        }

        public static getAdminUri(baseUrl: string, contentPath: string): string {
            let adminUrl = UriHelper.getPortalUri(contentPath, RenderingMode.ADMIN, api.content.Branch.DRAFT);
            return adminUrl + (adminUrl.charAt(adminUrl.length - 1) == '/' ? '' : api.content.ContentPath.ELEMENT_DIVIDER) + baseUrl;
        }
    }
}
