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

        resolveSubName(object: ContentSummaryAndCompareStatus, relativePath: boolean = false): string {
            var contentSummary = object.getContentSummary(),
                uploadItem = object.getUploadItem();

            if (contentSummary) {
                this.toggleClass("invalid", !contentSummary.isValid());

                var contentName = contentSummary.getName();
                if (relativePath) {
                    return !contentName.isUnnamed() ? contentName.toString() :
                                                      api.ui.NamesAndIconViewer.EMPTY_SUB_NAME;
                } else {
                    return !contentName.isUnnamed() ? contentSummary.getPath().toString() :
                                                      ContentPath.fromParent(contentSummary.getPath().getParentPath(),
                                                                             api.ui.NamesAndIconViewer.EMPTY_SUB_NAME).toString();
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
            return !!contentSummary ? new ContentIconUrlResolver().setContent(contentSummary).setCrop(false).resolve() : "";
        }
    }
}