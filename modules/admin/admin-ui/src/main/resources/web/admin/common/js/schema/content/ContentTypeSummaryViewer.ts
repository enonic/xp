module api.schema.content {

    export class ContentTypeSummaryViewer extends api.ui.NamesAndIconViewer<ContentTypeSummary> {

        private contentTypeIconUrlResolver: ContentTypeIconUrlResolver;

        constructor() {
            super();
            this.contentTypeIconUrlResolver = new ContentTypeIconUrlResolver();
        }

        resolveDisplayName(object: ContentTypeSummary): string {
            return object.getDisplayName();
        }

        resolveSubName(object: ContentTypeSummary, relativePath: boolean = false): string {
            return object.getName();
        }

        resolveIconUrl(object: ContentTypeSummary): string {
            return this.contentTypeIconUrlResolver.resolve(object);
        }
    }
}