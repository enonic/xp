module api.content {

    export class ContentSummaryViewer extends api.ui.Viewer<ContentSummary> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(content: ContentSummary) {
            super.setObject(content);
            this.namesAndIconView.setMainName(content.getDisplayName()).
                setSubName(content.getPath().toString()).
                setIconUrl(content.getIconUrl() + '?crop=false');
            if (content.isSite()) {
                this.addClass("site");
            }
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}