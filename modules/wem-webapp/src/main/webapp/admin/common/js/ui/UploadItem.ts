module api_ui {

    export class UploadItem {

        private blobKey: api_blob.BlobKey;

        private name: string;

        private mimeType: string;

        private size: number;

        constructor(builder: UploadItemBuilder) {
            this.blobKey = builder.blobKey;
            this.name = builder.name;
            this.mimeType = builder.mimeType;
            this.size = builder.size;
        }

        getBlobKey(): api_blob.BlobKey {
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

        blobKey: api_blob.BlobKey;

        name: string;

        mimeType: string;

        size: number;

        public setId(value: api_blob.BlobKey): UploadItemBuilder {
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