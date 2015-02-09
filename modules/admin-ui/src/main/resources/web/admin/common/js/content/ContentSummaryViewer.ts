module api.content {

    export class ContentSummaryViewer extends api.ui.NamesAndIconViewer<ContentSummary> {

        constructor() {
            super("content-summary-viewer");
        }

        resolveDisplayName(object: ContentSummary): string {
            var contentName = object.getName(),
                invalid = !object.isValid() || !object.getDisplayName() || contentName.isUnnamed();
            this.toggleClass("invalid", invalid);

            return object.getDisplayName();
        }

        resolveUnnamedDisplayName(object: ContentSummary): string {
            return object.getType() ? object.getType().getLocalName() : "";
        }

        resolveSubName(object: ContentSummary, relativePath: boolean = false): string {
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

        resolveSubTitle(object: ContentSummary): string {
            return object.getPath().toString();
        }

        resolveIconUrl(object: ContentSummary): string {
            return new ContentIconUrlResolver().setContent(object).setCrop(false).resolve();
        }
    }
}