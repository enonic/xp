module api.ui {

    export enum TextAreaSize {
        LARGE,
        MEDIUM,
        SMALL
    }

    export class TextArea extends api.dom.FormInputEl {


        private listeners:{[eventName:string]: {(event:any):void}[]} = {};

        private oldValue:string = "";

        constructor(name:string) {
            super("textarea");
            this.getEl().setAttribute("name", name);
            this.listeners[InputEvents.ValueChange] = [];

            this.getEl().addEventListener('input', () => {
                this.notifyListeners(InputEvents.ValueChange, { "oldValue": this.oldValue, "newValue": this.getValue()});
                this.oldValue = this.getValue();
            });
        }

        setValue(text:string) {
            this.getEl().setValue(text);
        }

        setRows(rows:number) {
            this.getEl().setAttribute("rows", rows.toString());
        }

        setColumns(columns:number) {
            this.getEl().setAttribute("cols", columns.toString());
        }

        setSize(size:TextAreaSize) {
            var sizeClass;
            switch (size) {
            case TextAreaSize.LARGE:
                sizeClass = "large";
                break;
            case TextAreaSize.MEDIUM:
                sizeClass = "medium";
                break;
            case TextAreaSize.SMALL:
                sizeClass = "small";
                break;
            default:
                break;
            }
            this.addClass(sizeClass);
        }

        addListener(eventName:InputEvents, listener:(event:any)=>void) {
            if (this.listeners[eventName] ) {
                this.listeners[eventName].push(listener);
            }
        }

        notifyListeners(eventName:InputEvents, event:any) {
            this.listeners[eventName].forEach((listener:(event:any)=>void)=> {
                listener(event);
            });
        }
    }

}