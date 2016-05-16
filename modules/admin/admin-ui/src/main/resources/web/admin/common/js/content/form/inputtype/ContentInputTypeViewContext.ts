module api.content.form.inputtype {

    export interface ContentInputTypeViewContext extends api.form.inputtype.InputTypeViewContext {

        formContext: api.content.form.ContentFormContext;

        site: api.content.site.Site;

        content: api.content.ContentSummary;

        parentContentPath: api.content.ContentPath;
    }
}