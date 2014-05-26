module api.dom {

    export class ElementProperties {

        private tagName: string;
        private generateId: boolean;
        private className: string;
        private helper: ElementHelper;
        private loadExistingChildren: boolean;

        constructor() {
        }

        setTagName(name: string): ElementProperties {
            api.util.assert(!api.util.isStringEmpty(name), 'Tag name shouldn\'t be empty');
            this.tagName = name;
            return this;
        }

        setGenerateId(flag: boolean): ElementProperties {
            this.generateId = flag;
            return this;
        }

        setClassName(name: string): ElementProperties {
            this.className = name;
            return this;
        }

        setHelper(helper: ElementHelper): ElementProperties {
            this.helper = helper;
            return this;
        }

        setLoadExistingChildren(value: boolean): ElementProperties {
            this.loadExistingChildren = value;
            return this;
        }

        isLoadExistingChildren(): boolean {
            return this.loadExistingChildren;
        }

        getTagName(): string {
            return this.tagName;
        }

        isGenerateId(): boolean {
            return this.generateId;
        }

        getClassName(): string {
            return this.className;
        }

        getHelper(): ElementHelper {
            return this.helper;
        }
    }

    export class Element {

        private el: ElementHelper;

        private parentElement: Element;

        private children: Element[];

        private rendered: boolean;

        private addedListeners: {(event: ElementAddedEvent) : void}[] = [];
        private removedListeners: {(event: ElementRemovedEvent) : void}[] = [];
        private renderedListeners: {(event: ElementRenderedEvent) : void}[] = [];
        private shownListeners: {(event: ElementShownEvent) : void}[] = [];
        private hiddenListeners: {(event: ElementHiddenEvent) : void}[] = [];
        private resizedListeners: {(event: ElementResizedEvent) : void}[] = [];

        constructor(properties: ElementProperties) {
            this.children = [];
            this.rendered = false;
            if (properties.getHelper()) {
                this.el = properties.getHelper();
                if (properties.isLoadExistingChildren()) {
                    this.loadExistingChildren();
                }
            } else if (properties.getTagName()) {
                this.el = ElementHelper.fromName(properties.getTagName());
            } else {
                throw new Error("Either tag name or helper should be present");
            }

            var moduleName = api.util.getModuleName(this);
            if (properties.isGenerateId() || (properties.isGenerateId() == undefined && moduleName != "api.dom")) {
                var id = ElementRegistry.registerElement(this);
                this.setId(id);
            }
            if (properties.getClassName()) {
                this.setClass(properties.getClassName());
            }
            this.onRemoved((event: ElementRemovedEvent) => {
                if (this.getId()) {
                    ElementRegistry.unregisterElement(this);
                }
            })
        }

        private loadExistingChildren() {

            var children = this.getHTMLElement().children;
            for (var i = 0; i < children.length; i++) {
                var child = children[i];
                var childAsElement = api.dom.Element.fromHtmlElement(<HTMLElement>child, true);
                this.children.push(childAsElement);
            }
        }

        init() {
            this.children.forEach((child: Element) => {
                child.init();
            });
            if (!this.isRendered()) {
                this.render(false);
            }
            if (this.isVisible()) {
                this.notifyShown();
            }
        }

        render(deep: boolean = true) {
            if (deep) {
                this.children.forEach((child: Element) => {
                    child.render();
                });
            }
            this.rendered = true;
            this.notifyRendered();
        }

        isRendered(): boolean {
            return this.rendered;
        }

        show() {
            // Using jQuery to show, since it seems to contain some smartness
            wemjq(this.el.getHTMLElement()).show();
            this.notifyShown(this);
        }

        hide() {
            // Using jQuery to hide, since it seems to contain some smartness
            wemjq(this.el.getHTMLElement()).hide();
            this.notifyHidden(this);
        }

        setVisible(value: boolean) {
            if (value) {
                this.show();
            } else {
                this.hide();
            }
        }

        isVisible() {
            return wemjq(this.el.getHTMLElement()).is(':visible');
        }

        setClass(className: string): api.dom.Element {
            api.util.assert(!api.util.isStringEmpty(className), 'Class name shouldn\'t be empty');
            this.el.setClass(className);
            return this;
        }

        addClass(className: string): api.dom.Element {
            api.util.assert(!api.util.isStringEmpty(className), 'Class name shouldn\'t be empty');
            this.el.addClass(className);
            return this;
        }

        toggleClass(className: string): api.dom.Element {
            if (this.hasClass(className)) {
                this.removeClass(className);
            } else {
                this.addClass(className);
            }
            return this;
        }

        hasClass(className: string): boolean {
            return this.el.hasClass(className);
        }

        removeClass(className: string): api.dom.Element {
            api.util.assert(!api.util.isStringEmpty(className), 'Class name shouldn\'t be empty');
            this.el.removeClass(className);
            return this;
        }

        getId(): string {
            return this.el.getId();
        }

        setId(value: string): api.dom.Element {
            this.el.setId(value);
            return this;
        }

        getEl(): ElementHelper {
            return this.el;
        }

        giveFocus(): boolean {
            if (!this.isVisible()) {
                return false;
            }
            if (this.el.isDisabled()) {
                return false;
            }
            this.el.focuse();
            var gotFocus: boolean = document.activeElement == this.el.getHTMLElement();
            if (!gotFocus) {
                console.log("Element.giveFocus(): Failed to give focus to Element: class = " + api.util.getClassName(this) + ", id = " +
                            this.getId());
            }
            return gotFocus;
        }

        giveBlur(): boolean {
            if (!this.isVisible()) {
                return false;
            }
            if (this.el.isDisabled()) {
                return false;
            }
            this.el.blur();
            var gotBlur: boolean = document.activeElement != this.el.getHTMLElement();
            if (!gotBlur) {
                console.log("Element.giveBlur(): Failed to give blur to Element: class = " + api.util.getClassName(this) + ", id = " +
                            this.getId());
            }
            return gotBlur;
        }

        getHTMLElement(): HTMLElement {
            return this.el.getHTMLElement();
        }

        appendChild<T extends api.dom.Element>(child: T): Element {
            this.el.appendChild(child.getEl().getHTMLElement());
            this.insert(child, this, this.children.length);
            return this;
        }

        prependChild(child: api.dom.Element) {
            api.util.assertNotNull(child, 'Child shouldn\'t be null');
            this.el.getHTMLElement().insertBefore(child.getHTMLElement(), this.el.getHTMLElement().firstChild);
            this.insert(child, this, 0);
        }

        insertAfterEl(existingEl: Element) {
            api.util.assertNotNull(existingEl, 'Existing element shouldn\'t be null');
            this.el.insertAfterEl(existingEl);
            var parent = existingEl.getParentElement();
            var index = parent.getChildren().indexOf(existingEl) + 1;
            this.insert(this, parent, index);
        }

        insertBeforeEl(existingEl: Element) {
            api.util.assertNotNull(existingEl, 'Existing element shouldn\'t be null');
            this.el.insertBeforeEl(existingEl);
            var parent = existingEl.getParentElement();
            var index = parent.getChildren().indexOf(existingEl);
            this.insert(this, parent, index);
        }

        wrapWithElement(wrapperElement: Element) {
            api.util.assertNotNull(wrapperElement, 'Wrapper element shouldn\'t be null');
            var parent = this.getParentElement();
            if (!parent) {
                return;
            }

            var childPos = parent.children.indexOf(this);
            parent.removeChild(this);
            wrapperElement.appendChild(this);
            // add wrapper to parent in the same position of the current element
            parent.el.appendChild(wrapperElement.getEl().getHTMLElement());
            parent.insert(wrapperElement, this, childPos);
        }

        private insert(child: Element, parent: Element, index: number) {
            api.util.assertNotNull(child, 'Child element shouldn\'t be null');
            api.util.assertNotNull(parent, 'Parent element shouldn\'t be null');
            child.setParentElement(parent);
            parent.getChildren().splice(index, 0, child);
            if (parent.isRendered()) {
                child.init();
            }
            child.notifyAdded();
        }

        removeChild(child: api.dom.Element) {
            var index = this.children.indexOf(child);
            if (index > -1) {
                this.children.splice(index, 1);
                child.getEl().remove();
                child.setParentElement(null);
                child.notifyRemoved();
            }
        }

        removeChildren() {
            // copy children because it can be modified inside the loop
            var children = this.children.slice(0);
            // to remove text nodes etc
            this.el.setInnerHtml('');
            this.children.length = 0;
            children.forEach((child: Element) => {
                child.setParentElement(null);
                child.notifyRemoved();
            });
        }

        remove() {
            var parent = this.getParentElement();
            if (parent) {
                parent.removeChild(this);
            } else {
                this.getEl().remove();
                this.notifyRemoved();
            }
        }

        private setParentElement(parent: Element) {
            this.parentElement = parent;
        }

        getParentElement(): Element {
            return this.parentElement;
        }

        getChildren(): Element[] {
            return this.children;
        }

        getLastChild(): Element {
            return this.children[this.children.length - 1];
        }

        getFirstChild(): Element {
            return this.children[0];
        }

        getNextElement(): Element {
            var nextSiblingHtmlElement = this.getHTMLElement().nextElementSibling;
            if (!nextSiblingHtmlElement) {
                return null;
            }
            return Element.fromHtmlElement(<HTMLElement>nextSiblingHtmlElement);
        }

        getPreviousElement(): Element {
            var previousSiblingHtmlElement = this.getHTMLElement().previousElementSibling;
            if (!previousSiblingHtmlElement) {
                return null;
            }
            return Element.fromHtmlElement(<HTMLElement>previousSiblingHtmlElement);
        }

        /**
         * Returns the index of this element among it's siblings. Returns 0 if first or only child.
         */
        getSiblingIndex(): number {

            var i = 0;
            var prev: HTMLElement = this.getHTMLElement();
            while ((prev = <HTMLElement>prev.previousSibling) != null) {
                i++;
            }
            return i;
        }

        toString(): string {
            return wemjq('<div>').append(wemjq(this.getHTMLElement()).clone()).html();
        }

        onMouseEnter(handler: (e: MouseEvent)=>any) {
            this.mouseEnterLeave('mouseenter', handler);
        }

        onMouseLeave(handler: (e: MouseEvent)=>any) {
            this.mouseEnterLeave('mouseleave', handler);
        }

        setBackgroundImgUrl(backgroundImgUrl: string) {
            this.getHTMLElement().style.backgroundImage = "url('" + backgroundImgUrl + "')";
        }

        private mouseEnterLeave(type: string, handler: (e: MouseEvent)=>any) {
            var mouseEnter = type === 'mouseenter',
                containerEl = this.getEl(),
                ie = mouseEnter ? 'fromElement' : 'toElement',
                mouseEventHandler = (e: any) => { //Had use any since window.event isn't of type MouseEvent and caused compiler to bug
                    e = e || window.event;
                    var target: HTMLElement = <HTMLElement> (e.target || e.srcElement),
                        related: HTMLElement = <HTMLElement> (e.relatedTarget || e[ie]);
                    if ((this.getHTMLElement() === target || containerEl.contains(target)) && !containerEl.contains(related)) {
                        handler(e);
                    }
                };
            type = mouseEnter ? 'mouseover' : 'mouseout';

            containerEl.addEventListener(type, mouseEventHandler);
            return mouseEventHandler;
        }

        contains(element: api.dom.Element) {
            return this.getEl().contains(element.getHTMLElement());
        }

        onAdded(listener: (event: ElementAddedEvent) => void) {
            this.addedListeners.push(listener);
        }

        unAdded(listener: (event: ElementAddedEvent) => void) {
            this.addedListeners = this.addedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyAdded() {
            var addedEvent = new ElementAddedEvent(this);
            this.addedListeners.forEach((listener) => {
                listener(addedEvent);
            });
            // Each child throw its own added
        }

        onRemoved(listener: (event: ElementRemovedEvent) => void) {
            this.removedListeners.push(listener);
        }

        unRemoved(listener: (event: ElementRemovedEvent) => void) {
            this.removedListeners = this.removedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyRemoved(target?: Element) {
            var removedEvent = new ElementRemovedEvent(this, target);
            this.removedListeners.forEach((listener) => {
                listener(removedEvent);
            });
            this.children.forEach((child: Element) => {
                child.notifyRemoved(removedEvent.getTarget());
            })
        }

        onRendered(listener: (event: ElementRenderedEvent) => void) {
            this.renderedListeners.push(listener);
        }

        unRendered(listener: (event: ElementRenderedEvent) => void) {
            this.renderedListeners = this.renderedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyRendered() {
            var renderedEvent = new ElementRenderedEvent(this);
            this.renderedListeners.forEach((listener) => {
                listener(renderedEvent);
            });
            // Each child throw its own rendered
        }

        onShown(listener: (event: ElementShownEvent) => void) {
            this.shownListeners.push(listener);
        }

        unShown(listener: (event: ElementShownEvent) => void) {
            this.shownListeners = this.shownListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyShown(target?: Element) {
            var shownEvent = new ElementShownEvent(this, target);
            this.shownListeners.forEach((listener) => {
                listener(shownEvent);
            });
            this.children.forEach((child: Element) => {
                if (child.isVisible()) {
                    child.notifyShown(shownEvent.getTarget());
                }
            })
        }

        onHidden(listener: (event: ElementHiddenEvent) => void) {
            this.hiddenListeners.push(listener);
        }

        unHidden(listener: (event: ElementHiddenEvent) => void) {
            this.hiddenListeners = this.hiddenListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyHidden(target?: Element) {
            var hiddenEvent = new ElementHiddenEvent(this, target);
            this.hiddenListeners.forEach((listener) => {
                listener(hiddenEvent);
            });
            this.children.forEach((child: Element) => {
                child.notifyHidden(hiddenEvent.getTarget());
            })
        }

        onResized(listener: (event: ElementResizedEvent) => void) {
            this.resizedListeners.push(listener);

            if (this.resizedListeners.length == 1) {
                var handler = (event) => {
                    this.notifyResized();
                }
                if (this.isVisible()) {
                    wemjq(this.getHTMLElement()).resize(handler);
                } else {
                    var firstShowListener = (event: ElementShownEvent) => {
                        wemjq(this.getHTMLElement()).resize(handler);
                        this.unShown(firstShowListener);
                    }
                    this.onShown(firstShowListener);
                }
            }
        }

        unResized(listener: (event: ElementResizedEvent) => void) {
            this.resizedListeners = this.resizedListeners.filter((curr) => {
                return curr !== listener;
            });

            if (this.resizedListeners.length == 0) {
                wemjq(this.getHTMLElement()).removeResize((event) => {
                    this.notifyResized();
                });
            }
        }

        private notifyResized() {
            var width = this.getEl().getWidth();
            var height = this.getEl().getHeight();
            var event = new ElementResizedEvent(width, height, this);
            this.resizedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onClicked(listener: (event: MouseEvent) => void) {
            this.getEl().addEventListener("click", listener);
        }

        unClicked(listener: (event: MouseEvent) => void) {
            this.getEl().removeEventListener("click", listener);
        }

        onDblClicked(listener: (event: MouseEvent) => void) {
            this.getEl().addEventListener("dblclick", listener);
        }

        unDblClicked(listener: (event: MouseEvent) => void) {
            this.getEl().removeEventListener("dblclick", listener);
        }

        onMouseDown(listener: (event: MouseEvent) => void) {
            this.getEl().addEventListener("mousedown", listener);
        }

        unMouseDown(listener: (event: MouseEvent) => void) {
            this.getEl().removeEventListener("mousedown", listener);
        }

        onMouseUp(listener: (event: MouseEvent) => void) {
            this.getEl().addEventListener("mouseup", listener);
        }

        unMouseUp(listener: (event: MouseEvent) => void) {
            this.getEl().removeEventListener("mouseup", listener);
        }

        onMouseMove(listener: (event: MouseEvent) => void) {
            this.getEl().addEventListener("mousemove", listener);
        }

        unMouseMove(listener: (event: MouseEvent) => void) {
            this.getEl().removeEventListener("mousemove", listener);
        }

        onKeyUp(listener: (event: KeyboardEvent) => void) {
            this.getEl().addEventListener("keyup", listener);
        }

        unKeyUp(listener: (event: KeyboardEvent) => void) {
            this.getEl().removeEventListener("keyup", listener);
        }

        onKeyDown(listener: (event: KeyboardEvent) => void) {
            this.getEl().addEventListener("keydown", listener);
        }

        unKeyDown(listener: (event: KeyboardEvent) => void) {
            this.getEl().removeEventListener("keydown", listener);
        }

        onKeyPressed(listener: (event: KeyboardEvent) => void) {
            this.getEl().addEventListener("keypress", listener);
        }

        unKeyPressed(listener: (event: KeyboardEvent) => void) {
            this.getEl().removeEventListener("keypress", listener);
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.getEl().addEventListener("focus", listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.getEl().removeEventListener("focus", listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.getEl().addEventListener("blur", listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.getEl().removeEventListener("blur", listener);
        }

        onScroll(listener: (event: Event) => void) {
            this.getEl().addEventListener("scroll", listener);
        }

        unScroll(listener: (event: Event) => void) {
            this.getEl().removeEventListener("scroll", listener);
        }

        static fromHtmlElement(element: HTMLElement, loadExistingChildren: boolean = false): Element {
            return new Element(new ElementProperties().
                setHelper(new ElementHelper(element)).
                setLoadExistingChildren(loadExistingChildren));
        }
    }
}
