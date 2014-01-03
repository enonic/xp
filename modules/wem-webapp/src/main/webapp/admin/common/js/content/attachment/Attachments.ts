module api.content.attachment {

    export class Attachments {

        private attachmentByName: {[s:string] : Attachment;} = {};

        private attachments: Attachment[] = [];

        private size: number;

        constructor(builder: AttachmentsBuilder) {

            var count:number = 0;
            builder.attachments.forEach((attachment: Attachment) => {
                this.attachmentByName[attachment.getAttachmentName().toString()] = attachment;
                count++;
                this.attachments.push(attachment);
            });
            this.size = count;
        }

        getAttachment(name: string): Attachment {
            return this.attachmentByName[name];
        }

        getAttachments(): Attachment[] {
            return this.attachments;
        }

        getSize(): number {
            return this.size;
        }

    }

    export class AttachmentsBuilder {

        attachments: Attachment[] = [];

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
