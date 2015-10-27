module api.content.attachment {

    export class Attachments implements api.Equitable {

        private attachmentByName: {[s:string] : Attachment;} = {};

        private attachmentByLabel: {[s:string] : Attachment;} = {};

        private attachments: Attachment[] = [];

        private size: number;

        constructor(builder: AttachmentsBuilder) {

            var count: number = 0;
            builder.attachments.forEach((attachment: Attachment) => {
                this.attachmentByName[attachment.getName().toString()] = attachment;
                this.attachmentByLabel[attachment.getLabel()] = attachment;
                count++;
                this.attachments.push(attachment);
            });
            this.size = count;
        }

        forEach(callBack: {(attachment: Attachment, index: number): void;}) {
            this.attachments.forEach((attachment: Attachment, index: number) => {
                callBack(attachment, index);
            });
        }

        getAttachmentByName(name: string): Attachment {
            return this.attachmentByName[name];
        }

        getAttachmentByLabel(label: string): Attachment {
            return this.attachmentByLabel[name];
        }

        getAttachment(index: number): Attachment {
            return this.attachments[index];
        }

        getSize(): number {
            return this.size;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Attachments)) {
                return false;
            }

            var other = <Attachments>o;

            if (!api.ObjectHelper.arrayEquals(this.attachments, other.attachments)) {
                return false;
            }

            return true;
        }

        public static create(): AttachmentsBuilder {
            return new AttachmentsBuilder();
        }
    }

    export class AttachmentsBuilder {

        attachments: Attachment[] = [];

        public fromJson(jsons: AttachmentJson[]): AttachmentsBuilder {
            jsons.forEach((json: AttachmentJson) => {
                this.attachments.push(new AttachmentBuilder().fromJson(json).build());
            });
            return this;
        }

        public add(value: Attachment): AttachmentsBuilder {
            this.attachments.push(value);
            return this;
        }

        public addAll(value: Attachment[]): AttachmentsBuilder {
            value.forEach((attachment: Attachment) => {
                this.attachments.push(attachment);
            });
            return this;
        }

        public build(): Attachments {
            return new Attachments(this);
        }
    }
}
