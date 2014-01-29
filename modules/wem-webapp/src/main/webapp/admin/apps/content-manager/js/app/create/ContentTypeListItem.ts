module app.create {

    export class ContentTypeListItem extends api.dom.LiEl {

        private siteRoot: boolean;

        private contentType: api.schema.content.ContentTypeSummary;

        constructor(item: api.schema.content.ContentTypeSummary, siteRoot: boolean = false, markRoot?: boolean) {
            super("content-type-list-item");

            this.contentType = item;
            this.siteRoot = siteRoot;

            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize( api.app.NamesAndIconViewSize.small ).build();

            namesAndIconView
                .setIconUrl(item.getIconUrl())
                .setMainName(item.getDisplayName())
                .setSubName(item.getName());

            if (siteRoot && markRoot) {
                this.addClass('site');
                namesAndIconView.setDisplayIconLabel(true);
            }

            this.appendChild(namesAndIconView);
        }

        getName(): string {
            return this.contentType.getName();
        }

        getDisplayName(): string {
            return this.contentType.getDisplayName();
        }

        getIconUrl(): string {
            return this.contentType.getIconUrl();
        }

        getContentType(): api.schema.content.ContentTypeSummary {
            return this.contentType;
        }

        isSiteRoot(): boolean {
            return this.siteRoot;
        }
    }
}