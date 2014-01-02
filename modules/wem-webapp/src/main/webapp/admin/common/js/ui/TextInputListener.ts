module api.ui {

    export interface TextInputListener extends api.event.Listener {

        onValueChanged(oldValue:string, newValue:string);

    }

}