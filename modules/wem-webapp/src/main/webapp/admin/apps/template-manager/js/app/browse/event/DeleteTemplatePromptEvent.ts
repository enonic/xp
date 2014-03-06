module app.browse.event {
    export class DeleteTemplatePromptEvent extends BaseTemplateModelEvent {

        constructor(templateModel: app.browse.TemplateSummary) {
            super('deleteSitetemplatePrompt', [templateModel]);
        }

        getTemplate(): app.browse.TemplateSummary {
            return this.getTemplates()[0];
        }

        static on(handler: (event: DeleteTemplatePromptEvent) => void) {
            api.event.onEvent('deleteSiteTemplatePrompt', handler);
        }
    }
}