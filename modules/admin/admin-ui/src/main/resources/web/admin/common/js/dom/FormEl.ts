module api.dom {

    export class FormEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("form").setClassName(className));
        }

        preventSubmit() {
            this.onSubmit((event: Event) => {
                event.preventDefault();
            });
        }

        onSubmit(listener: (event: Event) => void) {
            this.getEl().addEventListener("submit", listener);
        }

        unSubmit(listener: (event: Event) => void) {
            this.getEl().removeEventListener("submit", listener);
        }

        static getNextFocusable(input: Element, focusableSelector?: string, ignoreTabIndex?: boolean): Element {
            let focusableElements: NodeList = document.querySelectorAll(focusableSelector ? focusableSelector : "input, button, select");

            // find index of current input
            let index = FormEl.getIndexOfInput(focusableElements, input);

            if (index < 0) {
                return;
            }

            // set focus to the next visible input
            for (let i = index + 1; i < focusableElements.length; i++) {
                let nextFocusable = api.dom.Element.fromHtmlElement(<HTMLElement>focusableElements.item(i));
                if (!nextFocusable.isVisible() ||
                    (!ignoreTabIndex && nextFocusable.getEl().getTabIndex() && nextFocusable.getEl().getTabIndex() < 0 )) {
                    continue;
                } else {
                    return nextFocusable;
                }
            }

            return null;
        }

        static moveFocusToNextFocusable(input: Element, focusableSelector?: string) {

            let nextFocusable = FormEl.getNextFocusable(input, focusableSelector);

            if (nextFocusable) {
                nextFocusable.giveFocus();
            }
        }

        static moveFocusToPrevFocusable(input: Element, focusableSelector?: string) {
            let focusableElements: NodeList = document.querySelectorAll(focusableSelector ? focusableSelector : "input, button, select");

            // find index of current input
            let index = FormEl.getIndexOfInput(focusableElements, input);
            let nextFocusable: api.dom.Element;

            do {
                index = index - 1;
                if (0 <= index) {
                    nextFocusable = api.dom.Element.fromHtmlElement(<HTMLElement>focusableElements.item(index));
                }
            } while (nextFocusable.getEl().getTabIndex() && nextFocusable.getEl().getTabIndex() < 0);
            nextFocusable.giveFocus();
            return;
        }

        private static getIndexOfInput(elements: NodeList, el: Element) {
            let index = -1;
            let inputHTMLElement = el.getHTMLElement();
            for (let i = 0; i < elements.length; i++) {
                if (inputHTMLElement == elements.item(i)) {
                    index = i;
                    break;
                }
            }
            return index;
        }
    }
}
