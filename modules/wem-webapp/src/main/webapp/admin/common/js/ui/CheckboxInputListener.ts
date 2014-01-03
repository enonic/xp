module api.ui {

    export interface CheckboxInputListener extends api.event.Listener {

        onValueChanged(oldValue:boolean, newValue:boolean);

    }

}