module api.ui {

    export class TextArea extends api.dom.FormInputEl {

        private valueChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        private oldValue:string = "";

        private attendant: api.dom.Element;

        private clone: api.dom.Element;

        constructor(name:string) {
            super("textarea", "text-area");
            this.getEl().setAttribute("name", name);

            this.getEl().addEventListener('input', () => {
                this.notifyValueChanged(this.oldValue, this.getValue());
                this.oldValue = this.getValue();
            });

            this.clone = new api.dom.DivEl().addClass('autosize-clone').addClass(this.getEl().getAttribute('class'));
            this.attendant = new api.dom.DivEl('autosize-attendant');
            this.attendant.appendChild(this.clone);

            this.onShown((event: api.dom.ElementShownEvent) => this.updateSize());
            this.onValueChanged((event: ValueChangedEvent) => this.updateSize());
            window.addEventListener('resize', () => this.updateSize());
        }

        setValue(text:string) {
            if (this.oldValue != text) {
                super.setValue(text);
                this.notifyValueChanged(this.oldValue, text);
                this.oldValue = text;
            }
        }

        setRows(rows:number) {
            this.getEl().setAttribute("rows", rows.toString());
        }

        setColumns(columns:number) {
            this.getEl().setAttribute("cols", columns.toString());
        }

        private updateSize() {
            this.attendant.insertAfterEl(this);
            this.clone.getEl().setInnerHtml(this.getValue() + " ");
            this.getEl().setHeightPx(this.clone.getEl().getHeightWithBorder());
            this.attendant.remove();
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
            });
        }
    }

}