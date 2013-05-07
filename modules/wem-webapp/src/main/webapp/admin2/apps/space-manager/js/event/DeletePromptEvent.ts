module APP.event {

    var DELETE_PROMPT:string = 'deletePrompt';

    export class DeletePromptEvent extends API.event.Event {
        constructor() {
            super(DELETE_PROMPT);
        }
    }

    export function onDeletePrompt(handler:(event:DeletePromptEvent) => void) {
        API.event.onEvent(DELETE_PROMPT, handler);
    }

}

