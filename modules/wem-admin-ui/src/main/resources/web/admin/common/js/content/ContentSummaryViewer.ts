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
            var subName = relativePath ? content.getPath().getLastElement() : content.getPath().toString();
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

        getPreferredHeight(): number {
            return 50;
        }
    }
}