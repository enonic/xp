module app_browse {

    export class DeletePromptEvent extends app_event.BaseSpaceModelEvent {

        constructor(model:api_model.SpaceModel[]) {
            super('deletePrompt', model);
        }

        static on(handler:(event:DeletePromptEvent) => void) {
            api_event.onEvent('deletePrompt', handler);
        }
    }
}

