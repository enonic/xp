module api.ui {

    export class UploadItem {

        private blobKey: api.blob.BlobKey;

        private name: string;

        private mimeType: string;

        private size: number;

        constructor(builder: UploadItemBuilder) {
            this.blobKey = builder.blobKey;
            this.name = builder.name;
            this.mimeType = builder.mimeType;
            this.size = builder.size;
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

        blobKey: api.blob.BlobKey;

        name: string;

        mimeType: string;

        size: number;

        public setId(value: api.blob.BlobKey): UploadItemBuilder {
            this.blobKey = value;
            return this;
        }

        public setName(value: string): UploadItemBuilder {
            this.name = value;
            return this;
        }

        public setMimeType(value: string): UploadItemBuilder {
            this.mimeType = value;
            return this;
        }

        public setSize(value: number): UploadItemBuilder {
            this.size = value;
            return this;
        }

        public build(): UploadItem {
            return new UploadItem(this);
        }
    }
}