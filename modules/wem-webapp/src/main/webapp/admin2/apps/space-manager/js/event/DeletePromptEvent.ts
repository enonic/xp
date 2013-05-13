module APP.event {

    var DELETE_PROMPT:string = 'deletePrompt';

    export class DeletePromptEvent extends API.event.Event {
        private model:APP.model.SpaceModel;

        constructor(model:any) {
            this.model = model;
            super(DELETE_PROMPT);
        }

        getModel():APP.model.SpaceModel {
            return this.model;
        }
    }

    export function onDeletePrompt(handler:(event:DeletePromptEvent) => void) {
        API.event.onEvent(DELETE_PROMPT, handler);
    }

}

