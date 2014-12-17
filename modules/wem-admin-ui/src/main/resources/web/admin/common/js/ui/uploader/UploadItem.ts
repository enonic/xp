module api.ui.uploader {

    export class UploadItem {

        private id: string;

        private blobKey: api.blob.BlobKey;

        private name: string;

        private mimeType: string;

        private size: number;

        private progress: number = 0;

        constructor(builder: UploadItemBuilder) {
            this.id = builder.id;
            this.blobKey = builder.blobKey;
            this.name = builder.name;
            this.mimeType = builder.mimeType;
            this.size = builder.size;
            this.progress = builder.progress;
        }

        getId(): string {
            return this.id;
        }

        getBlobKey(): api.blob.BlobKey {
            return this.blobKey;
        }

        setBlobKey(key: api.blob.BlobKey): UploadItem {
            this.blobKey = key;
            return this;
        }

        getName(): string {
            return this.name;
        }

        setName(name: string): UploadItem {
            this.name = name;
            return this;
        }

        getMimeType(): string {
            return this.mimeType;
        }

        setMimeType(type: string): UploadItem {
            this.mimeType = type;
            return this;
        }

        getSize(): number {
            return this.size;
        }

        setSize(size: number): UploadItem {
            this.size = size;
            return this;
        }

        getProgress(): number {
            return this.progress;
        }

        setProgress(progress: number): UploadItem {
            this.progress = progress;
            return this;
        }

        public static create(source?: UploadItem): UploadItemBuilder {
            return new UploadItemBuilder(source);
        }
    }

    export class UploadItemBuilder {

        id: string;

        blobKey: api.blob.BlobKey;

        name: string;

        mimeType: string;

        size: number;

        progress: number;

        constructor(source?: UploadItem) {
            if (source) {
                this.id = source.getId();
                this.blobKey = source.getBlobKey();
                this.name = source.getName();
                this.mimeType = source.getMimeType();
                this.size = source.getSize();
                this.progress = source.getProgress();
            }
        }

        setId(value: string): UploadItemBuilder {
            this.id = value;
            return this;
        }

        setBlobKey(value: api.blob.BlobKey): UploadItemBuilder {
            this.blobKey = value;
            return this;
        }

        setName(value: string): UploadItemBuilder {
            this.name = value;
            return this;
        }

        setMimeType(value: string): UploadItemBuilder {
            this.mimeType = value;
            return this;
        }

        setSize(value: number): UploadItemBuilder {
            this.size = value;
            return this;
        }

        setProgress(value: number): UploadItemBuilder {
            this.progress = value;
            return this;
        }

        build(): UploadItem {
            return new UploadItem(this);
        }
    }
}