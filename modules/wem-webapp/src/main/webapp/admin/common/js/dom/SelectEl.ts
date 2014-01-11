module api.dom {

    export class SelectEl extends FormInputEl {

        constructor(generateId?:boolean, className?:string) {
            super("select", generateId, className);
        }
    }
}
