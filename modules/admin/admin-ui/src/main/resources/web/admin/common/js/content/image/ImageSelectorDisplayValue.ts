module api.content.image {

    import ContentSummary = api.content.ContentSummary;
    import UploadItem = api.ui.uploader.UploadItem;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ImageSelectorDisplayValue {

        private uploadItem: UploadItem<ContentSummary>;

        private content: ContentSummary;

        private empty: boolean;

        static fromUploadItem(item: UploadItem<ContentSummary>): ImageSelectorDisplayValue {
            return new ImageSelectorDisplayValue().setUploadItem(item);
        }

        static fromContentSummary(content: ContentSummary) {
            return new ImageSelectorDisplayValue().setContentSummary(content);
        }

        static makeEmpty() {
            return new ImageSelectorDisplayValue().setEmpty(true);
        }

        isEmptyContent(): boolean {
            return this.empty;
        }

        setEmpty(value: boolean): ImageSelectorDisplayValue {
            this.empty = value;
            return this;
        }

        setUploadItem(item: UploadItem<ContentSummary>): ImageSelectorDisplayValue {
            this.uploadItem = item;
            return this;
        }

        setContentSummary(contentSummary: ContentSummary): ImageSelectorDisplayValue {
            this.content = contentSummary;
            return this;
        }

        getUploadItem(): UploadItem<ContentSummary> {
            return this.uploadItem;
        }

        getContentSummary(): ContentSummary {
            return this.content;
        }

        getId(): string {
            return this.content ? this.content.getId() : this.uploadItem.getId();
        }

        getContentId(): api.content.ContentId {
            return this.content ? this.content.getContentId() : null;
        }

        getContentPath(): api.content.ContentPath {
            return this.content ? this.content.getPath() : null;
        }

        getImageUrl(): string {
            return this.content ? new api.content.util.ContentIconUrlResolver().setContent(this.content).resolve() : null;
        }

        getIconUrl(): string {
            return this.content ? new api.content.util.ContentIconUrlResolver().setContent(this.content).resolve() : null;
        }

        getLabel(): string {
            return this.content ? this.content.getName().toString() : this.uploadItem.getName();
        }

        getDisplayName(): string {
            return this.content ? this.content.getDisplayName() : null;
        }

        getType(): ContentTypeName {
            return this.content ? this.content.getType() : null;
        }

        getTypeLocaleName(): string {
            return (this.content && this.content.getType()) ? this.content.getType().getLocalName() : null;
        }

        getPath(): ContentPath {
            return this.content ? this.content.getPath() : null;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, api.ClassHelper.getClass(this))) {
                return false;
            }

            let other = <ImageSelectorDisplayValue>o;

            if (!ObjectHelper.equals(this.uploadItem, other.uploadItem)) {
                return false;
            }

            if (!ObjectHelper.equals(this.content, other.content)) {
                return false;
            }

            if (this.empty != other.empty) {
                return false;
            }

            return true;
        }
    }
}
