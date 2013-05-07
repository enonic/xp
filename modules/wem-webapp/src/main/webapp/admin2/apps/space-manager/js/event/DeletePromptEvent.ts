module app.event {

    var DELETE_PROMPT:string = 'deletePrompt';

    export class DeletePromptEvent extends api.event.Event {
        constructor() {
            super(DELETE_PROMPT);
        }
    }

    export function onDeletePrompt(handler:(event:DeletePromptEvent) => void) {
        api.event.onEvent(DELETE_PROMPT, handler);
    }

}

