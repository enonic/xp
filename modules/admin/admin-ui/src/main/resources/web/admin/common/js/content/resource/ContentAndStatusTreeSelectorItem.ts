module api.content.resource {

    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import ContentState = api.schema.content.ContentState;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import UploadItem = api.ui.uploader.UploadItem;

    export class ContentAndStatusTreeSelectorItem extends ContentTreeSelectorItem {

        private compareStatus: CompareStatus;

        private publishStatus: PublishStatus;

        constructor(content: ContentSummaryAndCompareStatus, expand: boolean) {

            super(content.getContentSummary(), expand);

            this.compareStatus = content.getCompareStatus();
            this.publishStatus = content.getPublishStatus();
        }

        getPublishStatus(): PublishStatus {
            return this.publishStatus;
        }

        getCompareStatus(): CompareStatus {
            return this.compareStatus;
        }
    }
}
