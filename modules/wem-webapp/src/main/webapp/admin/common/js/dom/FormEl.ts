module api.dom {

    export class FormEl extends Element {

        constructor(className?: string) {
            super(new ElementProperties().setTagName("form").setClassName(className));
        }

        preventSubmit() {

            this.getEl().addEventListener("submit", (event: Event) => {
                event.preventDefault();
            })
        }
    }
}
