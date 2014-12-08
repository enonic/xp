module api.content.attachment {

    export class Attachment {

        private blobKey: api.blob.BlobKey;

        private name: AttachmentName;

        private label: string;

        private mimeType: string;

        private size: number;

        constructor(builder: AttachmentBuilder) {
            this.blobKey = builder.blobKey;
            this.name = builder.name;
            this.label = builder.label;
            this.mimeType = builder.mimeType;
            this.size = builder.size;
        }

        getBlobKey(): api.blob.BlobKey {
            return this.blobKey;
        }

        getName(): AttachmentName {
            return this.name;
        }

        getLabel(): string {
            return this.label;
        }

        getMimeType(): string {
            return this.mimeType;
        }

        getSize(): number {
            return this.size;
        }

        toJson(): api.content.attachment.AttachmentJson {
            return {
                "blobKey": this.getBlobKey().toString(),
                "name": this.getName().toString(),
                "label": this.getLabel(),
                "mimeType": this.getMimeType(),
                "size": this.getSize()
            };
        }

    }

    export class AttachmentBuilder {

        blobKey: api.blob.BlobKey;

        name: AttachmentName;

        label: string;

        mimeType: string;

        size: number;

        public setBlobKey(value: api.blob.BlobKey): AttachmentBuilder {
            this.blobKey = value;
            return this;
        }

        public setName(value: AttachmentName): AttachmentBuilder {
            this.name = value;
            return this;
        }

        public setLabel(value: string): AttachmentBuilder {
            this.label = value;
            return this;
        }

        public setMimeType(value: string): AttachmentBuilder {
            this.mimeType = value;
            return this;
        }

        public setSize(value: number): AttachmentBuilder {
            this.size = value;
            return this;
        }

        public build(): Attachment {
            return new Attachment(this);
        }
    }
}
