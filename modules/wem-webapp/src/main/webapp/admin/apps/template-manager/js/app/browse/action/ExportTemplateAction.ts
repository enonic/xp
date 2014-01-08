module app.browse.action {
    export class ExportTemplateAction extends api.ui.Action {

        constructor() {
            super("Export");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                var selection = components.gridPanel.getSelection()[0];
                var siteTemplateModel = api.content.site.template.SiteTemplateSummary.fromExtModel(selection);
                new app.browse.event.ExportTemplateEvent(siteTemplateModel).fire();
            });
        }

    }
}