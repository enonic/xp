module api_ui {
    export class Dropdown extends api_dom.SelectEl {

        constructor(name:string) {
            super();
            this.getEl().setAttribute("name", name);
        }

        addOption(value:string, displayName:string) {
            var option = new DropdownOption(value, displayName);
            this.appendChild(option);
        }
    }


    export class DropdownOption extends api_dom.OptionEl {
        constructor(value:string, displayName:string) {
            super(value, displayName);
        }
    }
}