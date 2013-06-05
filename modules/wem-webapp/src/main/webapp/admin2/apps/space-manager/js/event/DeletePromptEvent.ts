module app_event {

    export class DeletePromptEvent extends SpaceModelEvent {
        constructor(model:app_model.SpaceModel[]) {
            super('deletePrompt', model);
        }

        static on(handler:(event:DeletePromptEvent) => void) {
            api_event.onEvent('deletePrompt', handler);
        }
    }
}

