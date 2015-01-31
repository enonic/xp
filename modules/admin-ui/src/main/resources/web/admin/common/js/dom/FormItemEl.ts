module api.dom {
    export class FormItemEl extends Element {

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


    }
}