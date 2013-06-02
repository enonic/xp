module APP.event {

    export class DeletePromptEvent extends SpaceModelEvent {
        constructor(model:any) {
            super('deletePrompt', model);
        }

        static on(handler:(event:DeletePromptEvent) => void) {
            API_event.onEvent('deletePrompt', handler);
        }
    }

    APP_action.SpaceActions.DELETE_SPACE.addExecutionListener(() => {
        new DeletePromptEvent( APP_context.SpaceContext.get().getSelectedSpaces() ).fire();
    });
}

