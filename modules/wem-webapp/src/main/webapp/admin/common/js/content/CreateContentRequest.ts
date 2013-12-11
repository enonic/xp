module api_content {

    export class CreateContentRequest extends ContentResourceRequest<api_content_json.ContentJson> {

        private draft: boolean = false;

        private name: string;

        private parent: ContentPath;

        private embed: boolean = false;

        private contentType: api_schema_content.ContentTypeName;

        private form: api_form.Form;

        private contentData: ContentData;

        private displayName: string;

        private attachments: api_content.Attachment[] = [];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setDraft(value: boolean): CreateContentRequest {
            this.draft = value;
            return this;
        }

        setName(value: string): CreateContentRequest {
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

        setContentType(value: api_schema_content.ContentTypeName): CreateContentRequest {
            this.contentType = value;
            return this;
        }

        setForm(form: api_form.Form): CreateContentRequest {
            this.form = form;
            return this;
        }

        setContentData(contentData: api_content.ContentData): CreateContentRequest {
            this.contentData = contentData;
            return this;
        }

        setDisplayName(displayName: string): CreateContentRequest {
            this.displayName = displayName;
            return this;
        }

        addAttachment(attachment: api_content.Attachment): CreateContentRequest {
            this.attachments.push(attachment);
            return this;
        }

        addAttachments(attachments: api_content.Attachment[]): CreateContentRequest {
            this.attachments = this.attachments.concat(attachments);
            return this;
        }


        getParams(): Object {
            return {
                draft: this.draft,
                name: this.name,
                parent: this.parent.toString(),
                embed: this.embed,
                contentType: this.contentType.toString(),
                form: this.form.toJson(),
                contentData: this.contentData.toJson(),
                displayName: this.displayName,
                attachments: this.attachmentsToJson()
            };
        }

        private attachmentsToJson(): api_content_json.AttachmentJson[] {
            var array: api_content_json.AttachmentJson[] = [];
            this.attachments.forEach((attachment: api_content.Attachment)=> {
                var attachmentJsonbj:api_content_json.AttachmentJson = {
                    "blobKey": attachment.getBlobKey().toString(),
                    "attachmentName": attachment.getAttachmentName().toString(),
                    "mimeType": attachment.getMimeType(),
                    "size": attachment.getSize()
                };
                array.push(attachmentJsonbj);
            });
            return array;
        }

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "create");
        }

        sendAndParse(): JQueryPromise<Content> {

            var deferred = jQuery.Deferred<Content>();

            this.send().done((response: api_rest.JsonResponse<api_content_json.ContentJson>) => {
                deferred.resolve(this.fromJsonToContent(response.getResult()));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }

    }
}