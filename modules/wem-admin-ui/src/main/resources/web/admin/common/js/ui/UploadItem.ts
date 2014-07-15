module api.ui {

    export class UploadItem {

        private id: string;

        private blobKey: api.blob.BlobKey;

        private name: string;

        private mimeType: string;

        private size: number;

        constructor(builder: UploadItemBuilder) {
            this.id = builder.id;
            this.blobKey = builder.blobKey;
            this.name = builder.name;
            this.mimeType = builder.mimeType;
            this.size = builder.size;
        }

        getId(): string {
            return this.id;
        }

        getBlobKey(): api.blob.BlobKey {
            return this.blobKey;
        }

        getName(): string {
            return this.name;
        }

        getMimeType(): string {
            return this.mimeType;
        }

        getSize(): number {
            return this.size;
        }

    }

    export class UploadItemBuilder {

        id: string;

        blobKey: api.blob.BlobKey;

        name: string;

        mimeType: string;

        size: number;

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

        build(): UploadItem {
            return new UploadItem(this);
        }
    }
}