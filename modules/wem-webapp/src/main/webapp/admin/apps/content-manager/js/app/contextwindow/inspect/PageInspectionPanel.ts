module app.contextwindow.inspect {

    export class PageInspectionPanel extends BaseInspectionPanel {

        private page: api.content.Content;

        private formView: api.form.FormView;

        constructor() {
            super("live-edit-font-icon-page");
            this.formView = null;
        }

        setPage(pageContent: api.content.Content, pageTemplate: api.content.page.PageTemplate,
                pageDescriptor: api.content.page.PageDescriptor) {
            this.page = pageContent;
            if (pageContent) {
                this.setMainName(pageContent.getDisplayName());
                this.setSubName(pageContent.getPath().toString());
            } else {
                this.setMainName("[No Page given]");
                this.setSubName("");
            }

            this.setupForm(pageContent, pageTemplate, pageDescriptor);
        }

        private setupForm(pageContent: api.content.Content, pageTemplate: api.content.page.PageTemplate,
                          pageDescriptor: api.content.page.PageDescriptor) {
            if (this.formView) {
                this.removeChild(this.formView);
            }
            if (!pageContent) {
                return;
            }

            var formContext = new api.form.FormContextBuilder().build();
            var form = pageDescriptor.getConfig();
            var config: api.data.RootDataSet;
            if (pageContent.getPage().hasConfig()) {
                config = pageContent.getPage().getConfig();
            } else {
                config = pageTemplate.getConfig();
            }

            this.formView = new api.form.FormView(formContext, form, config);
            this.appendChild(this.formView);
        }

    }
}