module api.dom {

    export class SelectEl extends FormInputEl {

        constructor(className?:string) {
            super('select', className);
        }

    }
}
