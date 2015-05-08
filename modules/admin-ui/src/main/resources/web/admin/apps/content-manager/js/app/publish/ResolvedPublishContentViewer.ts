module app.publish {

    import ContentPath = api.content.ContentPath;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import CompareStatus = api.content.CompareStatus;

    export class ResolvedPublishContentViewer extends api.ui.NamesAndIconViewer<ContentPublishItem> {

        constructor() {
            super("content-resolved-publish-viewer");
        }

        resolveDisplayName(object: ContentPublishItem): string {
            var contentName = object.getName(),
                invalid = !object.isValid() || !object.getDisplayName() || contentName.isUnnamed(),
                pendingDelete = CompareStatus.PENDING_DELETE == object.getCompareStatus() ? true : false;
            this.toggleClass("invalid", invalid);
            this.toggleClass("pending-delete", pendingDelete);

            return object.getDisplayName();
        }

        resolveUnnamedDisplayName(object: ContentPublishItem): string {
            return object.getType() ? object.getType().getLocalName() : "";
        }

        resolveSubName(object: ContentPublishItem, relativePath: boolean = false): string {
            var contentName = object.getName();
            if (relativePath) {
                return !contentName.isUnnamed() ? object.getName().toString() :
                       api.ui.NamesAndIconViewer.EMPTY_SUB_NAME;
            } else {
                return !contentName.isUnnamed() ? object.getPath().toString() :
                       ContentPath.fromParent(object.getPath().getParentPath(),
                           api.ui.NamesAndIconViewer.EMPTY_SUB_NAME).toString();
            }
        }

        resolveSubTitle(object: ContentPublishItem): string {
            return object.getPath().toString();
        }

        resolveIconUrl(object: ContentPublishItem): string {
            return new ContentIconUrlResolver().setContent(object).resolve();
        }
    }
}