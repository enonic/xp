module api.content {

    import UploadItem = api.ui.uploader.UploadItem;

    export class ContentSummaryAndCompareStatus implements api.Equitable {

        private uploadItem: UploadItem<ContentSummary>;

        private contentSummary: ContentSummary;

        private compareStatus: CompareStatus;

        constructor() {
        }

        public static fromContentSummary(contentSummary: ContentSummary) {
            return new ContentSummaryAndCompareStatus().setContentSummary(contentSummary);
        }

        public static fromContentAndCompareStatus(contentSummary: ContentSummary, compareStatus: CompareStatus) {
            return new ContentSummaryAndCompareStatus().setContentSummary(contentSummary).setCompareStatus(compareStatus);
        }

        public static fromUploadItem(item: UploadItem<ContentSummary>): ContentSummaryAndCompareStatus {
            return new ContentSummaryAndCompareStatus().setUploadItem(item);
        }

        getContentSummary(): ContentSummary {
            return this.contentSummary;
        }

        setContentSummary(contentSummary: ContentSummary): ContentSummaryAndCompareStatus {
            this.contentSummary = contentSummary;
            return this;
        }

        getCompareStatus(): CompareStatus {
            return this.compareStatus;
        }

        setCompareStatus(status: CompareStatus): ContentSummaryAndCompareStatus {
            this.compareStatus = status;
            return this;
        }

        getUploadItem(): UploadItem<ContentSummary> {
            return this.uploadItem;
        }

        setUploadItem(item: UploadItem<ContentSummary>): ContentSummaryAndCompareStatus {
            this.uploadItem = item;
            if (item.isUploaded()) {
                this.contentSummary = item.getModel();
            } else {
                item.onUploaded((contentSummary: ContentSummary) => {
                    this.contentSummary = contentSummary;
                });
            }
            return this;
        }

        getContentId(): ContentId {
            return this.contentSummary ? this.contentSummary.getContentId() : null;
        }

        getId(): string {
            return (this.contentSummary && this.contentSummary.getId()) ||
                   (this.uploadItem && this.uploadItem.getId()) ||
                   "";
        }

        getPath(): ContentPath {
            return this.contentSummary ? this.contentSummary.getPath() : null;
        }

        getType(): api.schema.content.ContentTypeName {
            return this.contentSummary ? this.contentSummary.getType() : null;
        }

        getDisplayName(): string {
            return this.contentSummary ? this.contentSummary.getDisplayName() : null;
        }

        hasChildren(): boolean {
            return !!this.contentSummary ? this.contentSummary.hasChildren() : false;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentSummaryAndCompareStatus)) {
                return false;
            }

            var other = <ContentSummaryAndCompareStatus>o;

            if (!api.ObjectHelper.equals(this.uploadItem, other.uploadItem)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.contentSummary, other.contentSummary)) {
                return false;
            }

            if (this.compareStatus != other.compareStatus) {
                return false;
            }

            return true;
        }
    }
}