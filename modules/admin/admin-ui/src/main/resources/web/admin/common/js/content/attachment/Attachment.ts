module api.content.attachment {

    export class Attachment implements api.Equitable {

        private name: AttachmentName;

        private label: string;

        private mimeType: string;

        private size: number;

        constructor(builder: AttachmentBuilder) {
            this.name = builder.name;
            this.label = builder.label;
            this.mimeType = builder.mimeType;
            this.size = builder.size;
        }

        getBinaryReference(): api.util.BinaryReference {
            return new api.util.BinaryReference(this.name.toString());
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

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Attachment)) {
                return false;
            }

            var other = <Attachment>o;

            if (!api.ObjectHelper.equals(this.name, other.name)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.label, other.label)) {
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

        toJson(): api.content.attachment.AttachmentJson {
            return {
                "name": this.getName().toString(),
                "label": this.getLabel(),
                "mimeType": this.getMimeType(),
                "size": this.getSize()
            };
        }

        public static create(): AttachmentBuilder {
            return new AttachmentBuilder();
        }

    }

    export class AttachmentBuilder {

        name: AttachmentName;

        label: string;

        mimeType: string;

        size: number;

        public fromJson(json: AttachmentJson): AttachmentBuilder {
            this.setName(new AttachmentName(json.name)).
                setLabel(json.label).
                setSize(json.size).
                setMimeType(json.mimeType);
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
