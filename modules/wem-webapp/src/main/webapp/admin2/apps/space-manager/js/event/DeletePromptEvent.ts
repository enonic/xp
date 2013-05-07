module app.event {

    var DELETE_PROMPT:string = 'deletePrompt';

    export class DeletePromptEvent extends api.event.Event {
        constructor() {
            super(DELETE_PROMPT);
        }
    }

    export function onDeletePrompt(handler:(event:DeletedEvent) => void) {
        api.event.on(DELETE_PROMPT, handler);
    }

}
