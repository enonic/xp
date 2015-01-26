module api.dom {

    export class ArticleEl extends Element {

        constructor(className?: string, editable?: boolean) {
            super(new NewElementBuilder().setTagName("article").setClassName(className));
            this.setEditable(editable);
        }

        setEditable(flag: boolean): ArticleEl {
            this.getEl().setAttribute('contenteditable', flag ? 'true' : 'false');
            return this;
        }

        isEditable(): boolean {
            return this.getEl().getAttribute('contenteditable') == 'true';
        }
    }
}
