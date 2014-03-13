module api.dom {

    export class FormEl extends Element {

        constructor(className?: string) {
            super(new ElementProperties().setTagName("form").setClassName(className));
        }

        preventSubmit() {
            this.onSubmit((event: Event) => {
                event.preventDefault();
            })
        }

        onSubmit(listener: (event: Event) => void) {
            this.getEl().addEventListener("submit", listener);
        }

        unSubmit(listener: (event: Event) => void) {
            this.getEl().removeEventListener("submit", listener);
        }
    }
}
