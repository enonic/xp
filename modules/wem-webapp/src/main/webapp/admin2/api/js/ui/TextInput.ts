module api_ui {

    // TODO: @alb - Why do we need extend InputEl? Why don't we can improve InputEl?
    export class TextInput extends api_dom.InputEl {

        static MIDDLE:string = 'middle';
        static LARGE:string = 'large';

        constructor(idPrefix?:string, className?:string, size?:string = TextInput.MIDDLE) {
            super(idPrefix, className);

            this.getEl().setAttribute('type', 'text');
            this.addClass('text-input').addClass(size);
        }

        static large(idPrefix?:string, className?:string):TextInput {
            return new TextInput(idPrefix, className, TextInput.LARGE);
        }

        static middle(idPrefix?:string, className?:string):TextInput {
            return new TextInput(idPrefix, className, TextInput.MIDDLE);
        }

        setValue(value:string):TextInput {
            this.getEl().setValue(value);
            return this;
        }

        getValue():string {
            return this.getEl().getValue();
        }

        setName(value:string):TextInput {
            this.getEl().setAttribute('name', value);
            return this;
        }

        getName():string {
            return this.getEl().getAttribute('name');
        }

        setPlaceholder(value:string):TextInput {
            this.getEl().setAttribute('placeholder', value);
            return this;
        }

        getPlaceholder():string {
            return this.getEl().getAttribute('placeholder');
        }
    }
}