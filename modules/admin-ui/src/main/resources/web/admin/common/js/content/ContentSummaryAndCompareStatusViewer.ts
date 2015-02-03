module api.content {

    export class ContentSummaryAndCompareStatusViewer extends api.ui.Viewer<ContentSummaryAndCompareStatus> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super("content-summary-and-compare-status-viewer");
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.small).
                build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(contentSummaryAndCompareStatus: ContentSummaryAndCompareStatus, relativePath: boolean = false) {
            super.setObject(contentSummaryAndCompareStatus);

            var contentSummary = contentSummaryAndCompareStatus.getContentSummary();
            var uploadItem = contentSummaryAndCompareStatus.getUploadItem();

            var subName,
                subTitle,
                displayName,
                iconUrl;

            if (!!contentSummary) {
                iconUrl = new ContentIconUrlResolver().
                    setContent(contentSummary).
                    setCrop(false).resolve();

                this.namesAndIconView.setIconUrl(iconUrl);

                displayName = contentSummary.getDisplayName();
                subName = this.resolveSubName(contentSummary, relativePath);
                subTitle = contentSummary.getPath().toString();

                this.toggleClass("invalid", !contentSummary.isValid());
            } else if (!!uploadItem) {
                this.namesAndIconView.setIconClass('icon-file-upload2');

                displayName = uploadItem.getName();
                subName = uploadItem.getName();
            }

            this.namesAndIconView.setMainName(displayName).
                setSubName(subName, subTitle);

        }

        private resolveSubName(content: ContentSummary, relativePath: boolean): string {

            var contentName = content.getName();
            if (relativePath) {
                if (contentName.isUnnamed()) {
                    return ContentUnnamed.PRETTY_UNNAMED;
                }
                else {
                    return content.getName().toString()
                }
            }
            else {
                if (contentName.isUnnamed()) {
                    var parentPath = content.getPath().getParentPath();
                    return ContentPath.fromParent(parentPath, ContentUnnamed.PRETTY_UNNAMED).toString();
                }
                else {
                    return content.getPath().toString();
                }
            }
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}