module app.browse.action {

    export class EditTemplateAction extends api.ui.Action {

        constructor() {
            super("Edit");

            this.addExecutionListener(() => {
                var selection = components.gridPanel.getSelection();
                var siteTemplateModel = api.content.site.template.SiteTemplateSummary.fromExtModelArray(selection);
                new app.browse.event.EditTemplateEvent(siteTemplateModel).fire();
            });
        }

    }
}