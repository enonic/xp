module api.content {

    export class ContentSummaryAndCompareStatusViewer extends api.ui.NamesAndIconViewer<ContentSummaryAndCompareStatus> {

        constructor() {
            super("content-summary-and-compare-status-viewer");
        }

        resolveDisplayName(object: ContentSummaryAndCompareStatus): string {
            var contentSummary = object.getContentSummary(),
                uploadItem = object.getUploadItem();

            if (contentSummary) {
                return contentSummary.getDisplayName();
            } else if (uploadItem) {
                return uploadItem.getName();
            }

            return "";
        }

        resolveUnnamedDisplayName(object: ContentSummaryAndCompareStatus): string {
            var contentSummary = object.getContentSummary();
            return (contentSummary && contentSummary.getType()) ? contentSummary.getType().getLocalName() : "";
        }

        resolveSubName(object: ContentSummaryAndCompareStatus, relativePath: boolean = false): string {
            var contentSummary = object.getContentSummary(),
                uploadItem = object.getUploadItem();

            if (contentSummary) {
                var contentName = contentSummary.getName(),
                    invalid = !contentSummary.isValid() || !contentSummary.getDisplayName() || contentName.isUnnamed(),
                    pendingDelete = contentSummary.getContentState().isPendingDelete();
                this.toggleClass("invalid", invalid);
                this.toggleClass("pending-delete", pendingDelete);

                if (relativePath) {
                    return !contentName.isUnnamed() ? contentName.toString() :
                           api.content.ContentUnnamed.prettifyUnnamed();
                } else {
                    return !contentName.isUnnamed() ? contentSummary.getPath().toString() :
                                                      ContentPath.fromParent(contentSummary.getPath().getParentPath(),
                                                          api.content.ContentUnnamed.prettifyUnnamed()).toString();
                }
            } else if (uploadItem) {
                return uploadItem.getName();
            }

            return "";
        }

        resolveSubTitle(object: ContentSummaryAndCompareStatus): string {
            var contentSummary = object.getContentSummary();
            return !!contentSummary ? contentSummary.getPath().toString() : "";
        }

        resolveIconClass(object: ContentSummaryAndCompareStatus): string {
            return !!object.getUploadItem() ? "icon-file-upload2" : "";
        }

        resolveIconUrl(object: ContentSummaryAndCompareStatus): string {
            var contentSummary = object.getContentSummary();
            return !!contentSummary ? new ContentIconUrlResolver().setContent(contentSummary).resolve() : "";
        }
    }
}