module api.icon {

    export class Icon {

        private blobKey: api.blob.BlobKey;

        private mimeType: string;

        constructor(builder: IconBuilder) {
            api.util.assertNotNull(builder.blobKey, "blobKey cannot be null");
            api.util.assertNotNull(builder.mimeType, "mimeType cannot be null");
            this.blobKey = builder.blobKey;
            this.mimeType = builder.mimeType;
        }

        public getBlobKey(): api.blob.BlobKey {
            return this.blobKey;
        }

        public getMimeType(): string {
            return this.mimeType;
        }

        public toJson():IconJson {
            return <IconJson>{
                blobKey: this.blobKey.toString(),
                mimeType: this.mimeType
            };
        }
    }

    export class IconBuilder {

        blobKey: api.blob.BlobKey;

        mimeType: string;

        public fromJson(json:IconJson): IconBuilder {
            this.blobKey = new api.blob.BlobKey(json.blobKey);
            this.mimeType = json.mimeType;
            return this;
        }

        public setBlobKey(value: api.blob.BlobKey): IconBuilder {
            this.blobKey = value;
            return this;
        }

        public setMimeType(value: string): IconBuilder {
            this.mimeType = value;
            return this;
        }

        public build(): Icon {
            return new Icon(this);
        }
    }

}
