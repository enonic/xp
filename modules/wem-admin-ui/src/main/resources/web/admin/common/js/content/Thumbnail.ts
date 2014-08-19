module api.content {

    export class Thumbnail implements api.Equitable {

        private blobKey: api.blob.BlobKey;

        private mimeType: string;

        private size: number;

        constructor(builder: ThumbnailBuilder) {
            this.blobKey = builder.blobKey;
            this.mimeType = builder.mimeType;
            this.size = builder.size;
        }

        getBlobKey(): api.blob.BlobKey {
            return this.blobKey;
        }

        getMimeType(): string {
            return this.mimeType;
        }

        getSize(): number {
            return this.size;
        }

        toJson(): api.content.ThumbnailJson {

            return {
                "blobKey": this.getBlobKey().toString(),
                "mimeType": this.getMimeType(),
                "size": this.getSize()
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Thumbnail)) {
                return false;
            }

            var other = <Thumbnail>o;

            if (!api.ObjectHelper.equals(this.blobKey, other.blobKey)){
                return false;
            }
            if (!api.ObjectHelper.stringEquals(this.mimeType, other.mimeType)) {
                return false;
            }
            if (!api.ObjectHelper.numberEquals(this.size, other.size)) {
                return false;
            }
            return true;
        }

    }

    export class ThumbnailBuilder {

        blobKey: api.blob.BlobKey;

        mimeType: string;

        size: number;

        public fromJson(json: ThumbnailJson): ThumbnailBuilder {
            this.blobKey = new api.blob.BlobKey(json.blobKey);
            this.mimeType = json.mimeType;
            this.size = json.size;
            return this;
        }

        public setBlobKey(value: api.blob.BlobKey): ThumbnailBuilder {
            this.blobKey = value;
            return this;
        }

        public setMimeType(value: string): ThumbnailBuilder {
            this.mimeType = value;
            return this;
        }

        public setSize(value: number): ThumbnailBuilder {
            this.size = value;
            return this;
        }

        public build(): Thumbnail {
            return new Thumbnail(this);
        }
    }
}
