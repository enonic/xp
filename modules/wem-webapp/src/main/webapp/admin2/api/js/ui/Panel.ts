module api_ui {
    export class Panel extends DivEl {
        constructor(name:string) {
            super(name);
            this.getEl().addClass("panel");
        }
    }
}