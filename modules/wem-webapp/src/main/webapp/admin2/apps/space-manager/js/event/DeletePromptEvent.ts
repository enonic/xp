module APP.event {

    export class DeletePromptEvent extends SpaceModelEvent {
        constructor(model:APP.model.SpaceModel[]) {
            super('deletePrompt', model);
        }

        static on(handler:(event:DeletePromptEvent) => void) {
            API_event.onEvent('deletePrompt', handler);
        }
    }
}

