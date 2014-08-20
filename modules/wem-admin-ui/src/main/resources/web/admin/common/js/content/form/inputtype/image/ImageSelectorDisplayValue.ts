module api.content.form.inputtype.image {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;

    export class ImageSelectorDisplayValue {

        private uploadItem: api.ui.uploader.UploadItem;

        private content: api.content.ContentSummary;

        constructor() {
        }

        static fromUploadItem(item: api.ui.uploader.UploadItem): ImageSelectorDisplayValue {
            return new ImageSelectorDisplayValue().setUploadItem(item);
        }

        static fromContentSummary(content: api.content.ContentSummary, item?: api.ui.uploader.UploadItem) {
            return new ImageSelectorDisplayValue().setContentSummary(content).setUploadItem(item);
        }

        private setUploadItem(item: api.ui.uploader.UploadItem): ImageSelectorDisplayValue {
            this.uploadItem = item;
            return this;
        }

        private setContentSummary(contentSummary: api.content.ContentSummary): ImageSelectorDisplayValue {
            this.content = contentSummary;
            return this;
        }

        getUploadItem(): api.ui.uploader.UploadItem {
            return this.uploadItem;
        }

        getContentSummary(): api.content.ContentSummary {
            return this.content;
        }

        getId(): string {
            return this.uploadItem ? this.uploadItem.getId() : this.content.getId();
        }

        getContentId(): api.content.ContentId {
            return this.content ? this.content.getContentId() : null;
        }

        getImageUrl(): string {
            return this.content ? new ContentIconUrlResolver().setContent(this.content).resolve() : null;
        }

        getLabel(): string {
            return this.content ? this.content.getName().toString() : this.uploadItem.getName();
        }

        getDisplayName(): string {
            return this.content ? this.content.getDisplayName() : null;
        }

        getPath(): string {
            return this.content ? this.content.getPath().toString() : null;
        }

    }
}