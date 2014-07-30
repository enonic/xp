module app.browse.event {
    export class DeleteTemplatePromptEvent extends BaseTemplateModelEvent {

        constructor(templateModel: api.content.TemplateSummary) {
            super('deleteSitetemplatePrompt', [templateModel]);
        }

        getTemplate(): api.content.TemplateSummary {
            return this.getTemplates()[0];
        }

        static on(handler: (event: DeleteTemplatePromptEvent) => void) {
            api.event.onEvent('deleteSiteTemplatePrompt', handler);
        }
    }
}