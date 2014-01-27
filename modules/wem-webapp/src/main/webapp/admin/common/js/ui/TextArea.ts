module api.ui {

    export enum TextAreaSize {
        LARGE,
        MEDIUM,
        SMALL
    }

    export class TextArea extends api.dom.FormInputEl {


        private listeners:{[eventName:string]: {(event:InputEvent):void}[]} = {};

        private oldValue:string = "";

        constructor(name:string) {
            super("textarea");
            this.getEl().setAttribute("name", name);
            this.listeners[InputEvents.ValueChanged] = [];

            this.getEl().addEventListener('input', () => {
                this.notifyValueChanged(new ValueChangedEvent(this.oldValue, this.getValue()));
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

        private addListener(eventName:InputEvents, listener:(event:InputEvent)=>void) {
            if (this.listeners[eventName] ) {
                this.listeners[eventName].push(listener);
            }
        }

        onValueChanged(listener:(event:ValueChangedEvent)=>void) {
            this.addListener(InputEvents.ValueChanged, listener);
        }

        private notifyListeners(eventName:InputEvents, event:InputEvent) {
            this.listeners[eventName].forEach((listener:(event:InputEvent)=>void)=> {
                listener(event);
            });
        }

        private notifyValueChanged(event:ValueChangedEvent) {
            this.notifyListeners(InputEvents.ValueChanged, event);
        }
    }

}