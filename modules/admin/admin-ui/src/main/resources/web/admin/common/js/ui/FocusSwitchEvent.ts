module api.ui {

    import InputTypeView = api.form.inputtype.InputTypeView;

    export class FocusSwitchEvent extends api.event.Event {
        private inputTypeView: InputTypeView<any>;

        constructor(inputTypeView: InputTypeView<any>) {
            super();
            this.inputTypeView = inputTypeView;
        }

        getInputTypeView(): InputTypeView<any> {
            return this.inputTypeView;
        }

        static on(handler: (event: FocusSwitchEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: FocusSwitchEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}
