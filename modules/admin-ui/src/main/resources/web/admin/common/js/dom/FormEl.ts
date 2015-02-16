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

        static moveFocusToNextFocusable(input: Element, focusableSelector?: string) {
            var focusableElements: NodeList = document.querySelectorAll(focusableSelector ? focusableSelector : "input, button, select");

            // find index of current input
            var index = FormEl.getIndexOfInput(focusableElements, input);

            if (index < 0) {
                return;
            }

            // set focus to the next visible input
            for (var i = index + 1; i < focusableElements.length; i++) {
                var nextFocusable = api.dom.Element.fromHtmlElement(<HTMLElement>focusableElements.item(i));
                if (nextFocusable.getEl().getTabIndex() && nextFocusable.getEl().getTabIndex() < 0) {
                    continue;
                } else {
                    nextFocusable.giveFocus();
                    return;
                }
            }
        }

        static moveFocusToPrevFocusable(input: Element, focusableSelector?: string) {
            var focusableElements: NodeList = document.querySelectorAll(focusableSelector ? focusableSelector : "input, button, select");

            // find index of current input
            var index = FormEl.getIndexOfInput(focusableElements, input);

            do {
                index = index - 1;
                if (0 <= index) {
                    var nextFocusable = api.dom.Element.fromHtmlElement(<HTMLElement>focusableElements.item(index));
                }
            } while (nextFocusable.getEl().getTabIndex() && nextFocusable.getEl().getTabIndex() < 0);
            nextFocusable.giveFocus();
            return;
        }


        private static getIndexOfInput(elements: NodeList, el: Element) {
            var index = -1;
            var inputHTMLElement = el.getHTMLElement();
            for (var i = 0; i < elements.length; i++) {
                if (inputHTMLElement == elements.item(i)) {
                    index = i;
                    break;
                }
            }
            return index;
        }
    }
}
