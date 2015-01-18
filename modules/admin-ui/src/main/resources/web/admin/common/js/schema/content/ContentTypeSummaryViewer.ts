module api.schema.content {

    export class ContentTypeSummaryViewer extends api.ui.Viewer<ContentTypeSummary> {

        private namesAndIconView: api.app.NamesAndIconView;

        private contentTypeIconUrlResolver: ContentTypeIconUrlResolver;

        constructor() {
            super();
            this.contentTypeIconUrlResolver = new ContentTypeIconUrlResolver();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(contentType: ContentTypeSummary) {
            super.setObject(contentType);
            this.namesAndIconView.setMainName(contentType.getDisplayName()).
                setSubName(contentType.getName()).
                setIconUrl(this.contentTypeIconUrlResolver.resolve(contentType));
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}