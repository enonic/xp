module api.ui {
    export class Dropdown extends api.dom.SelectEl {

        constructor(name:string) {
            super();
            this.getEl().setAttribute("name", name);
        }

        addOption(value:string, displayName:string) {
            var option = new DropdownOption(value, displayName);
            this.appendChild(option);
        }
    }


    export class DropdownOption extends api.dom.OptionEl {
        constructor(value:string, displayName:string) {
            super(value, displayName);
        }
    }
}