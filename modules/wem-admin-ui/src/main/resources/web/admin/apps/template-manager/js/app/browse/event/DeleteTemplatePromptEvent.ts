module app.browse.event {

    import Event2 = api.event.Event2;

    export class DeleteTemplatePromptEvent extends Event2 {

        private template: api.content.TemplateSummary;

        constructor(template: api.content.TemplateSummary) {
            this.template = template;
            super('deleteSiteTemplatePrompt');
        }

        getTemplate(): api.content.TemplateSummary {
            return this.template;
        }

        static on(handler: (event: DeleteTemplatePromptEvent) => void, contextWindow: Window = window) {
            Event2.bind("deleteSiteTemplatePrompt", handler, contextWindow);
        }

        static un(handler: (event: DeleteTemplatePromptEvent) => void, contextWindow: Window = window) {
            Event2.unbind("deleteSiteTemplatePrompt", handler, contextWindow);
        }
    }

}