module api_ui {

    export interface TextInputListener extends api_event.Listener {

        onValueChanged(oldValue:string, newValue:string);

    }

}