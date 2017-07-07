module api.content.resource {

    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import ContentState = api.schema.content.ContentState;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import UploadItem = api.ui.uploader.UploadItem;

    export class ContentAndStatusTreeSelectorItem {

        private content: ContentSummaryAndCompareStatus;

        private expand: boolean;

        constructor(content: ContentSummaryAndCompareStatus, expand: boolean) {
            this.content = content;
            this.expand = expand;
        }

  /*      public static fromJson(json: ContentTreeSelectorItemJson) {
            return new ContentTreeSelectorItem(ContentSummary.fromJson(json.content), json.expand);
        }*/

        getContent(): ContentSummaryAndCompareStatus {
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
            return this.content.getContentSummary().getName();
        }

        getDisplayName(): string {
            return this.content.getDisplayName();
        }

        getContentState(): ContentState {
            return this.content.getContentSummary().getContentState();
        }

        hasChildren(): boolean {
            return this.content.hasChildren();
        }

        isValid(): boolean {
            return this.content.getContentSummary().isValid();
        }

        getIconUrl(): string {
            return this.content.getIconUrl();
        }

        getType(): ContentTypeName {
            return this.content.getType();
        }

        isImage(): boolean {
            return this.content.getContentSummary().isImage();
        }

        isSite(): boolean {
            return this.content.getContentSummary().isSite();
        }

        getCompareStatus(): CompareStatus {
            return this.content.getCompareStatus();
        }

        getPublishStatus(): PublishStatus {
            return this.content.getPublishStatus();
        }

        getUploadItem(): UploadItem<ContentSummary> {
            return this.content.getUploadItem();
        }

        getExpand(): boolean {
            return this.expand;
        }
    }
}
