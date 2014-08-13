module api.content.inputtype {

    export interface ContentInputTypeViewContext<INPUT_CONFIG> extends api.form.inputtype.InputTypeViewContext<INPUT_CONFIG> {

        contentId: api.content.ContentId;

        contentPath: api.content.ContentPath;

        parentContentPath: api.content.ContentPath;

        attachments:api.content.attachment.Attachments;
    }
}