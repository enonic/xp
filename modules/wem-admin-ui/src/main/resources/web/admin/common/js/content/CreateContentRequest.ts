module api.content {

    export class CreateContentRequest extends ContentResourceRequest<api.content.json.ContentJson, Content> {

        private draft: boolean = false;

        private name: ContentName;

        private parent: ContentPath;

        private embed: boolean = false;

        private contentType: api.schema.content.ContentTypeName;

        private form: api.form.Form;

        private contentData: ContentData;

        private metadata: Metadata[] = [];

        private displayName: string;

        private attachments: api.content.attachment.Attachment[] = [];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setDraft(value: boolean): CreateContentRequest {
            this.draft = value;
            return this;
        }

        setName(value: ContentName): CreateContentRequest {
            this.name = value;
            return this;
        }

        setParent(value: ContentPath): CreateContentRequest {
            this.parent = value;
            return this;
        }

        setEmbed(value: boolean): CreateContentRequest {
            this.embed = value;
            return this;
        }

        setContentType(value: api.schema.content.ContentTypeName): CreateContentRequest {
            this.contentType = value;
            return this;
        }

        setForm(form: api.form.Form): CreateContentRequest {
            this.form = form;
            return this;
        }

        setContentData(contentData: api.content.ContentData): CreateContentRequest {
            this.contentData = contentData;
            return this;
        }

        setMetadata(metadata: Metadata[]): CreateContentRequest {
            this.metadata = metadata;
            return this;
        }

        setDisplayName(displayName: string): CreateContentRequest {
            this.displayName = displayName;
            return this;
        }

        addAttachment(attachment: api.content.attachment.Attachment): CreateContentRequest {
            this.attachments.push(attachment);
            return this;
        }

        addAttachments(attachments: api.content.attachment.Attachment[]): CreateContentRequest {
            this.attachments = this.attachments.concat(attachments);
            return this;
        }


        getParams(): Object {
            return {
                draft: this.draft,
                name: this.name.isUnnamed() ? this.name.toUnnamed().toStringIncludingHidden() : this.name.toString(),
                parent: this.parent.toString(),
                embed: this.embed,
                contentType: this.contentType.toString(),
                form: this.form.toJson(),
                contentData: this.contentData.toJson(),
                metadata: this.metadataToJson(),
                displayName: this.displayName,
                attachments: this.attachmentsToJson()
            };
        }

        private metadataToJson(): api.content.json.MetadataJson[] {
            return this.metadata ? this.metadata.map((metadata: Metadata) => metadata.toJson()) : null;
        }

        private attachmentsToJson(): api.content.attachment.AttachmentJson[] {
            var array: api.content.attachment.AttachmentJson[] = [];
            this.attachments.forEach((attachment: api.content.attachment.Attachment)=> {
                var attachmentJsonbj: api.content.attachment.AttachmentJson = {
                    "blobKey": attachment.getBlobKey().toString(),
                    "attachmentName": attachment.getAttachmentName().toString(),
                    "mimeType": attachment.getMimeType(),
                    "size": attachment.getSize()
                };
                array.push(attachmentJsonbj);
            });
            return array;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "create");
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().
                then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {

                    return this.fromJsonToContent(response.getResult());

                });
        }

    }
}