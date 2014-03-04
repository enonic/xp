module api.ui {

    export enum TextAreaSize {
        LARGE,
        MEDIUM,
        SMALL
    }

    export class TextArea extends api.dom.FormInputEl {


        private valueChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        private oldValue:string = "";

        constructor(name:string) {
            super("textarea");
            this.getEl().setAttribute("name", name);

            this.getEl().addEventListener('input', () => {
                this.notifyValueChanged(this.oldValue, this.getValue());
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

        onValueChanged(listener:(event:ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((currentListener: (event: ValueChangedEvent)=>void)=> {
                return currentListener != listener;
            });
        }

        private notifyValueChanged(oldValue: string, newValue: string) {
            this.valueChangedListeners.forEach((listener: (event: ValueChangedEvent)=>void)=> {
                listener.call(this, new ValueChangedEvent(oldValue, newValue));
            })
        }
    }

}