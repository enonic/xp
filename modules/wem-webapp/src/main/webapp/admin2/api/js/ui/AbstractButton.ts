module api_ui{

    export class AbstractButton extends api_ui.Component {

        private label:string;

        constructor(name:string, label:string) {
            super(name, "button");
            this.label = label;

            this.getEl().setInnerHtml(this.label);
        }

        setEnable(value:bool) {
            this.getEl().setDisabled(!value);
        }

    }
}
