module app.browse.action {

    export class DeleteTemplateAction extends api.ui.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                var selection = components.gridPanel.getSelection()[0];
                var siteTemplateModel = api.content.site.template.SiteTemplateSummary.fromExtModel(selection);
                new app.browse.event.DeleteSiteTemplatePromptEvent(siteTemplateModel).fire();
            });
        }
    }
}