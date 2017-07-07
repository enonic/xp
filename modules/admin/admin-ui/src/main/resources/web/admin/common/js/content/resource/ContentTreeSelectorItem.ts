module api.content.resource {

    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import ContentState = api.schema.content.ContentState;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ContentTreeSelectorItemJson {

        content: ContentSummaryJson;

        expand: boolean;
    }

    export class ContentTreeSelectorItem {

        private content: ContentSummary;

        private expand: boolean;

        constructor(content: ContentSummary, expand: boolean) {
            this.content = content;
            this.expand = expand;
        }

        public static fromJson(json: ContentTreeSelectorItemJson) {
            return new ContentTreeSelectorItem(ContentSummary.fromJson(json.content), json.expand);
        }

        getContent(): ContentSummary {
            return this.content;
        }

        getId(): string {
            return this.content.getId();
        }

        getContentId(): ContentId {
            return this.content.getContentId();
        }

        getPath(): ContentPath {
            return this.content.getPath();
        }

        getName(): ContentName {
            return this.content.getName();
        }

        getDisplayName(): string {
            return this.content.getDisplayName();
        }

        getContentState(): ContentState {
            return this.content.getContentState();
        }

        hasChildren(): boolean {
            return this.content.hasChildren();
        }

        isValid(): boolean {
            return this.content.isValid();
        }

        getIconUrl(): string {
            return this.content.getIconUrl();
        }

        getType(): ContentTypeName {
            return this.content.getType();
        }

        isImage(): boolean {
            return this.content.isImage();
        }

        isSite(): boolean {
            return this.content.isSite();
        }

        getExpand(): boolean {
            return this.expand;
        }
    }
}
