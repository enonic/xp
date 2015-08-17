module api.content.form.inputtype {

    export interface ContentInputTypeViewContext extends api.form.inputtype.InputTypeViewContext {

        formContext: api.content.form.ContentFormContext;

        site: api.content.site.Site;

        contentId: api.content.ContentId;

        contentPath: api.content.ContentPath;

        parentContentPath: api.content.ContentPath;
    }
}