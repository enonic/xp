module api.content {

    export class ContentSummaryViewer extends api.ui.Viewer<ContentSummary> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.small).
                build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(content: ContentSummary, relativePath: boolean = false) {
            super.setObject(content);
            var subName = this.resolveSubName(content, relativePath);
            var iconUrl = new ContentIconUrlResolver().
                setContent(content).
                setCrop(false).resolve();
            this.namesAndIconView.setMainName(content.getDisplayName()).
                setSubName(subName, content.getPath().toString()).
                setIconUrl(iconUrl);
            if (content.isSite()) {
                this.addClass("site");
            }
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