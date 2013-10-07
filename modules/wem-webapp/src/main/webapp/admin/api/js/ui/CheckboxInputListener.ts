module api_ui {

    export interface CheckboxInputListener extends api_event.Listener {

        onValueChanged(oldValue:boolean, newValue:boolean);

    }

}