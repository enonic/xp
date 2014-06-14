module api.dom {

    export class FormEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("form").setClassName(className));
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

        static moveFocusToNextFocusable(input: InputEl) {
            var focusableElements: NodeList = document.querySelectorAll("input, button, select");

            // find index of current input
            var index = -1;
            var inputHTMLElement = input.getHTMLElement();
            for (var i = 0; i < focusableElements.length; i++) {
                if (inputHTMLElement == focusableElements.item(i)) {
                    index = i;
                    break;
                }
            }
            if (index < 0) {
                return;
            }

            // set focus to the next visible input
            for (var i = index + 1; i < focusableElements.length; i++) {
                var nextFocusable = api.dom.Element.fromHtmlElement(<HTMLElement>focusableElements.item(i));
                if (nextFocusable.giveFocus()) {
                    return;
                }
            }
        }
    }
}
