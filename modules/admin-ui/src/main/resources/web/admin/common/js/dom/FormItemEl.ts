module api.dom {
    export class FormItemEl extends Element {

        private validityChangedListeners: {(event: ValidityChangedEvent):void}[] = [];

        constructor(tagName: string, className?: string) {
            super(new NewElementBuilder().
                setTagName(tagName).
                setClassName(className));
        }

        getName(): string {
            return this.getEl().getAttribute("name");
        }

        setName(name: string): FormItemEl {
            this.getEl().setAttribute("name", name);
            return this;
        }

        onValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityChangedListeners = this.validityChangedListeners.filter((curr) => {
                return curr != listener;
            });
        }

        notifyValidityChanged(valid: boolean) {
            this.validityChangedListeners.forEach((listener: (event: ValidityChangedEvent)=>void)=> {
                listener.call(this, new ValidityChangedEvent(valid));
            });
        }

    }
}