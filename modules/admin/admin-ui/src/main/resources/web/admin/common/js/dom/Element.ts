module api.dom {

    export class ElementBuilder {

        generateId: boolean;

        className: string;

        parentElement: Element;

        private getParsedClass(cls: string): string {
            return cls.trim().split(/\s+/)
                .filter((elem, index, arr) => {
                    return arr.indexOf(elem) === index;
                }).join(" ");
        }

        setGenerateId(value: boolean): ElementBuilder {
            this.generateId = value;
            return this;
        }

        setClassName(cls: string, prefix?: string): ElementBuilder {
            // Ensure class has only one entry
            if (cls) {
                cls = this.getParsedClass(cls);
                if (prefix) {
                    cls = api.StyleHelper.getCls(cls, prefix);
                }
            }
            this.className = cls;
            return this;
        }

        setParentElement(element: Element): ElementBuilder {
            this.parentElement = element;
            return this;
        }

    }

    export class ElementFromElementBuilder extends ElementBuilder {

        element: Element;

        setElement(element: Element): ElementFromElementBuilder {
            this.element = element;
            return this;
        }
    }

    export class ElementFromHelperBuilder extends ElementBuilder {

        helper: ElementHelper;

        loadExistingChildren: boolean;

        setHelper(helper: ElementHelper): ElementFromHelperBuilder {
            this.helper = helper;
            return this;
        }

        setLoadExistingChildren(value: boolean): ElementFromHelperBuilder {
            this.loadExistingChildren = value;
            return this;
        }
    }

    export class NewElementBuilder extends ElementBuilder {

        tagName: string;

        helper: ElementHelper;

        setTagName(name: string): NewElementBuilder {
            api.util.assert(!api.util.StringHelper.isEmpty(name), 'Tag name cannot be empty');
            this.tagName = name;
            return this;
        }

        setHelper(helper: ElementHelper): NewElementBuilder {
            this.helper = helper;
            return this;
        }
    }

    export class Element {

        private el: ElementHelper;

        private parentElement: Element;

        private children: Element[];

        private rendered: boolean;

        public static debug: boolean = false;

        private addedListeners: {(event: ElementAddedEvent) : void}[] = [];
        private removedListeners: {(event: ElementRemovedEvent) : void}[] = [];
        private renderedListeners: {(event: ElementRenderedEvent) : void}[] = [];
        private shownListeners: {(event: ElementShownEvent) : void}[] = [];
        private hiddenListeners: {(event: ElementHiddenEvent) : void}[] = [];
        private resizedListeners: {(event: ElementResizedEvent) : void}[] = [];

        constructor(builder: ElementBuilder) {
            this.children = [];
            this.rendered = false;

            if (api.ObjectHelper.iFrameSafeInstanceOf(builder, ElementFromElementBuilder)) {
                var fromElementBuilder = <ElementFromElementBuilder>builder;
                var sourceElement = fromElementBuilder.element;
                if (sourceElement) {
                    this.parentElement = fromElementBuilder.parentElement ? fromElementBuilder.parentElement : sourceElement.parentElement;
                    if (this.parentElement) {
                        this.parentElement.replaceChildElement(this, sourceElement);
                    }
                    this.children = sourceElement.children;
                    this.el = sourceElement.el;
                }
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(builder, ElementFromHelperBuilder)) {
                var fromHelperBuilder = <ElementFromHelperBuilder>builder;

                this.el = fromHelperBuilder.helper;
                if (fromHelperBuilder.loadExistingChildren) {
                    this.loadExistingChildren();
                }
                if (fromHelperBuilder.parentElement) {
                    this.parentElement = fromHelperBuilder.parentElement;
                }
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(builder, NewElementBuilder)) {
                var newElementBuilder = <NewElementBuilder>builder;
                if (!newElementBuilder.tagName) {
                    throw new Error("tagName cannot be null");
                }
                if (newElementBuilder.helper) {
                    this.el = newElementBuilder.helper;
                }
                else {
                    this.el = ElementHelper.fromName(newElementBuilder.tagName);
                }

                if (newElementBuilder.parentElement) {
                    this.parentElement = newElementBuilder.parentElement;
                }
            }
            else {
                throw new Error("Unsupported builder: " + api.ClassHelper.getClassName(builder));
            }

            if (this.parentElement && this.el.getHTMLElement().parentElement) {
                if (!(this.parentElement.getHTMLElement() === this.el.getHTMLElement().parentElement )) {
                    throw new Error("Illegal state: HTMLElement in parent Element is not the as the HTMLElement parent to this HTMLElement");
                }
            }
            // Do not generate id unless the distance to Element in the class hierarchy of this is larger than 1
            // This should result in that no id's are generated for new Element or classes extending Element directly
            // (which should prevent id-generation of direct instances of most api.dom classes)
            var distance = api.ClassHelper.distanceTo(this, Element);
            if (builder.generateId || distance > 1) {
                var id = ElementRegistry.registerElement(this);
                this.setId(id);
            }

            if (builder.className) {
                this.setClass(builder.className);
            }
            this.onRemoved((event: ElementRemovedEvent) => {
                if (this.getId()) {
                    ElementRegistry.unregisterElement(this);
                }
            });
        }

        private replaceChildElement(replacementChild: Element, existingChild: Element) {
            var index = this.children.indexOf(existingChild);
            this.children[index] = replacementChild;
        }

        public loadExistingChildren(): Element {

            var children = this.el.getChildren();
            for (var i = 0; i < children.length; i++) {
                var childAsElement = Element.fromHtmlElement(<HTMLElement>children[i], true, this);
                this.children.push(childAsElement);
            }

            return this;
        }


        public findChildById(id: string, deep: boolean = false): Element {
            for (var i = 0; i < this.children.length; i++) {
                var child = this.children[i];
                if (child.getId() == id) {
                    return child;
                } else if (deep) {
                    var found = child.findChildById(id, deep);
                    if (found) {
                        return found;
                    }
                }
            }
            return null;
        }

        init() {
            if (!this.isRendered()) {
                this.render(false);
            }
            this.children.forEach((child: Element) => {
                child.init();
            });
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
            this.rendered = this.doRender();
            this.notifyRendered();
        }

        isRendered(): boolean {
            return this.rendered;
        }

        doRender(): boolean {
            return true;
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

        setVisible(value: boolean): Element {
            if (value) {
                this.show();
            } else {
                this.hide();
            }
            return this;
        }

        isVisible() {
            return this.el.isVisible();
        }

        setClass(className: string): Element {
            api.util.assert(!api.util.StringHelper.isEmpty(className), 'Class name cannot be empty');
            this.el.setClass(className);
            return this;
        }

        setClassEx(className: string): Element {
            var cls = api.StyleHelper.getCls(className);
            return this.setClass(cls);
        }

        addClass(className: string): Element {
            api.util.assert(!api.util.StringHelper.isEmpty(className), 'Class name cannot be empty');
            this.el.addClass(className);
            return this;
        }

        addClassEx(className: string): Element {
            var cls = api.StyleHelper.getCls(className);
            return this.addClass(cls);
        }

        toggleClass(className: string, condition?: boolean): Element {
            if (condition == false || condition == undefined && this.hasClass(className)) {
                this.removeClass(className);
            } else {
                this.addClass(className);
            }
            return this;
        }

        toggleClassEx(className: string, condition?: boolean): Element {
            var cls = api.StyleHelper.getCls(className);
            return this.toggleClass(cls, condition);
        }

        hasClass(className: string): boolean {
            return this.el.hasClass(className);
        }

        hasClassEx(className: string): boolean {
            var cls = api.StyleHelper.getCls(className);
            return this.hasClass(cls);
        }

        removeClass(className: string): Element {
            api.util.assert(!api.util.StringHelper.isEmpty(className), 'Class name cannot be empty');
            this.el.removeClass(className);
            return this;
        }

        removeClassEx(className: string): Element {
            var cls = api.StyleHelper.getCls(className);
            return this.removeClass(cls);
        }

        getClass(): string {
            return this.el.getClass();
        }

        getId(): string {
            return this.el.getId();
        }

        setId(value: string): Element {
            this.el.setId(value);
            return this;
        }

        getEl(): ElementHelper {
            return this.el;
        }

        traverse(handler: (el: Element) => void) {
            this.getChildren().forEach((el: Element)=> {
                handler(el);
                el.traverse(handler);
            });
        }

        setDraggable(value: boolean) {
            if (value) {
                this.getEl().setAttribute("draggable", value.toString());
            } else {
                this.getEl().removeAttribute("draggable");
            }
        }

        isDraggable(): boolean {
            return this.getEl().getAttribute('draggable') == 'true';
        }

        setContentEditable(flag: boolean): ArticleEl {
            this.getEl().setAttribute('contenteditable', flag ? 'true' : 'false');
            return this;
        }

        isContentEditable(): boolean {
            return this.getEl().getAttribute('contenteditable') == 'true';
        }

        giveFocus(): boolean {
            if (!this.isVisible()) {
                return false;
            }
            if (this.el.isDisabled()) {
                return false;
            }
            this.el.focus();
            var gotFocus: boolean = document.activeElement == this.el.getHTMLElement();
            if (!gotFocus && Element.debug) {
                console.log("Element.giveFocus(): Failed to give focus to Element: class = " + api.ClassHelper.getClassName(this) +
                            ", id = " +
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
            if (!gotBlur && Element.debug) {
                console.log("Element.giveBlur(): Failed to give blur to Element: class = " + api.ClassHelper.getClassName(this) +
                            ", id = " +
                            this.getId());
            }
            return gotBlur;
        }

        getHTMLElement(): HTMLElement {
            return this.el.getHTMLElement();
        }


        /*
         *      Child manipulations
         */

        insertChild<T extends Element>(child: T, index: number): Element {
            api.util.assertNotNull(child, 'Child cannot be null');

            this.el.insertChild(child.getEl().getHTMLElement(), index);

            this.insertChildElement(child, this, index);
            return this;
        }

        appendChild<T extends Element>(child: T): Element {
            return this.insertChild(child, this.children.length);
        }

        appendChildren<T extends Element>(...children: T[]): Element {
            children.forEach((child: T) => {
                this.appendChild(child);
            });
            return this;
        }

        prependChild(child: Element): Element {
            return this.insertChild(child, 0);
        }

        insertAfterEl(existing: Element): Element {
            api.util.assertNotNull(existing, 'Existing element cannot be null');
            // get index before insertion !
            var existingIndex = existing.getSiblingIndex();
            this.el.insertAfterEl(existing.el);

            return this.insertChildElement(this, existing.parentElement, existingIndex + 1);
        }

        insertBeforeEl(existing: Element): Element {
            api.util.assertNotNull(existing, 'Existing element cannot be null');
            // get index before insertion !
            var existingIndex = existing.getSiblingIndex();
            this.el.insertBeforeEl(existing.el);

            return this.insertChildElement(this, existing.getParentElement(), existingIndex);
        }

        hasChild(child: Element) {
            return this.children.indexOf(child) > -1;
        }

        removeChild(child: Element): Element {
            api.util.assertNotNull(child, "Child element to remove cannot be null");

            child.getEl().remove();
            this.removeChildElement(child);

            return this;
        }

        removeChildren(): Element {
            // iterate through copy of children array
            // because original array is changed when any child is deleted
            this.children.slice(0).forEach((child: Element) => {
                child.remove();
            });

            // remove text nodes etc
            this.el.setInnerHtml('');
            return this;
        }

        private insertChildElement(child: Element, parent: Element, index?: number): Element {
            api.util.assertNotNull(child, 'Child element to insert cannot be null');
            api.util.assertNotNull(parent, 'Parent element cannot be null');

            parent.registerChildElement(child, index);

            if (parent.isRendered()) {
                child.init();
            }
            child.notifyAdded();
            return this;
        }

        private removeChildElement(child: Element): Element {
            api.util.assertNotNull(child, 'Child element to insert cannot be null');

            this.unregisterChildElement(child);

            child.notifyRemoved(this);
            return this;
        }

        private registerChildElement(child: Element, index?: number) {
            // first unregister element from parent if it has one
            // except when it equals to the one we're registering it in
            // that happens when parentElement has been set on Element in constructor, which is evil >:)
            // no need to do it with dom nodes because html takes care of this
            if (child.parentElement) {
                if (child.parentElement != this) {
                    child.parentElement.unregisterChildElement(child);
                } else if (this.children.indexOf(child) > -1) {
                    // is already registered
                    return;
                }
            }

            var parentNode = child.getHTMLElement().parentNode;
            // check for parentNode because if parent is not a HtmlElement but a Node ( i.e SVG )
            // then parentElement will be null but parentNode will not
            if (parentNode && parentNode !== this.getHTMLElement()) {
                throw new Error("Given child must be a child of this Element in DOM before it can be registered");
            }
            if (!index) {
                index = child.el.getSiblingIndex();
            }
            this.children.splice(index, 0, child);
            child.parentElement = this;
        }

        private unregisterChildElement(child: Element): number {
            var childIndex = this.children.indexOf(child);
            if (childIndex < 0) {
                throw new Error("Child element to remove not found");
            }
            this.children.splice(childIndex, 1);
            child.parentElement = null;
            return childIndex;
        }


        /*
         *      Self actions
         */

        contains(element: Element) {
            return this.getEl().contains(element.getHTMLElement());
        }

        remove(): Element {
            if (this.parentElement) {
                this.parentElement.removeChild(this);
            } else {
                this.getEl().remove();
                this.notifyRemoved(null);
            }
            return this;
        }

        replaceWith(replacement: Element) {
            api.util.assertNotNull(replacement, 'replacement element cannot be null');

            // Do the actual DOM replacement
            replacement.el.insertAfterEl(this.el);
            replacement.notifyAdded();

            // during these operation this.parentElement will become unavailable
            var parent = this.parentElement;
            var index = parent.unregisterChildElement(this);
            parent.registerChildElement(replacement, index);

            // Run init of replacement if parent is rendered
            if (parent.isRendered()) {
                replacement.init();
            }

            // Remove this from DOM completely
            this.getEl().remove();
            this.notifyRemoved(parent, this);
        }

        wrapWithElement(wrapperElement: Element) {
            api.util.assertNotNull(wrapperElement, 'wrapperElement cannot be null');
            var parent = this.parentElement;
            if (!parent) {
                return;
            }

            var childPos = parent.children.indexOf(this);
            parent.removeChild(this);
            wrapperElement.appendChild(this);
            // add wrapper to parent in the same position of the current element
            parent.el.appendChild(wrapperElement.getEl().getHTMLElement());
            parent.insertChildElement(this, wrapperElement, childPos);
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
            var indexFromDOM = this.el.getSiblingIndex();
            if (this.parentElement) {
                var indexFromElement = this.parentElement.children.indexOf(this);
                api.util.assertState(indexFromElement == indexFromDOM, "index of Element in parentElement.children" +
                                                                       " [" + indexFromElement + "] does not correspond with" +
                                                                       " the actual index [" + indexFromDOM +
                                                                       "] of the HTMLElement in DOM");
            }
            return indexFromDOM;
        }

        getTabbableElements(): Element[] {
            let selected = wemjq(this.getHTMLElement()).find(":tabbable");
            let elements = [];
            for (let i = 0; i < selected.length; i++) {
                elements.push(Element.fromHtmlElement(selected[i]));
            }
            return elements;
        }

        toString(): string {
            return wemjq('<div>').append(wemjq(this.getHTMLElement()).clone()).html();
        }

        getHtml(): string {
            return this.getEl().getInnerHtml();
        }

        setHtml(value: string, escapeHtml: boolean = true): Element {
            this.getEl().setInnerHtml(value, escapeHtml);
            return this;
        }


        /*
         *      Event listeners
         */
        private mouseEnterByHandler = {};

        onMouseEnter(handler: (e: MouseEvent) => any) {
            if (typeof this.getHTMLElement().onmouseenter != "undefined") {
                this.getEl().addEventListener('mouseenter', handler);
            } else {
                this.mouseEnterByHandler[<any> handler] = (e: MouseEvent) => {
                    // execute handler only if mouse came from outside
                    if (!this.getEl().contains(<HTMLElement> (e.relatedTarget || e.fromElement))) {
                        handler(e);
                    }
                };
                this.getEl().addEventListener('mouseover', this.mouseEnterByHandler[<any> handler]);
            }
        }

        unMouseEnter(handler: (e: MouseEvent) => any) {
            if (typeof this.getHTMLElement().onmouseenter != "undefined") {
                this.getEl().removeEventListener('mouseenter', handler);
            } else {
                this.getEl().removeEventListener('mouseover', this.mouseEnterByHandler[<any> handler]);
            }
        }

        private mouseLeaveByHandler = {};

        onMouseLeave(handler: (e: MouseEvent) => any) {
            if (typeof this.getHTMLElement().onmouseleave != "undefined") {
                this.getEl().addEventListener('mouseleave', handler);
            } else {
                this.mouseLeaveByHandler[<any> handler] = (e: MouseEvent) => {
                    // execute handler only if mouse moves outside
                    if (!this.getEl().contains(<HTMLElement> (e.relatedTarget || e.toElement))) {
                        handler(e);
                    }
                };
                this.getEl().addEventListener('mouseout', this.mouseLeaveByHandler[<any> handler]);
            }
        }

        unMouseLeave(handler: (e: MouseEvent) => any) {
            if (typeof this.getHTMLElement().onmouseleave != "undefined") {
                this.getEl().removeEventListener('mouseleave', handler);
            } else {
                this.getEl().removeEventListener('mouseout', this.mouseLeaveByHandler[<any> handler]);
            }
        }

        onMouseOver(listener: (e: MouseEvent)=>any) {
            this.getEl().addEventListener('mouseover', listener);
        }

        unMouseOver(listener: (event: MouseEvent) => void) {
            this.getEl().removeEventListener("mouseover", listener);
        }

        onMouseOut(listener: (e: MouseEvent)=>any) {
            this.getEl().addEventListener('mouseout', listener);
        }

        unMouseOut(listener: (event: MouseEvent) => void) {
            this.getEl().removeEventListener("mouseout", listener);
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

        private notifyRemoved(parent: Element, target?: Element) {
            var removedEvent = new ElementRemovedEvent(this, parent, target);
            this.removedListeners.forEach((listener) => {
                listener(removedEvent);
            });
            this.children.forEach((child: Element) => {
                child.notifyRemoved(removedEvent.getParent(), removedEvent.getTarget());
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

        onScrolled(listener: (event: WheelEvent) => void) {
            // IE9, Chrome, Safari, Opera
            this.getEl().addEventListener("mousewheel", listener);
            // Firefox
            this.getEl().addEventListener("DOMMouseScroll", listener);
        }

        unScrolled(listener: (event: WheelEvent) => void) {
            // IE9, Chrome, Safari, Opera
            this.getEl().removeEventListener("mousewheel", listener);
            // Firefox
            this.getEl().removeEventListener("DOMMouseScroll", listener);
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

        onContextMenu(listener: (event: MouseEvent) => void) {
            this.getEl().addEventListener("contextmenu", listener);
        }

        unContextMenu(listener: (event: MouseEvent) => void) {
            this.getEl().removeEventListener("contextmenu", listener);
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

        onMouseWheel(listener: (event: WheelEvent) => void) {
            // http://www.javascriptkit.com/javatutors/onmousewheel.shtml
            // FF doesn't recognize mousewheel as of FF3.x
            var eventName = (/Firefox/i.test(navigator.userAgent)) ? "wheel" : "mousewheel";
            this.getEl().addEventListener(eventName, listener);
        }

        unMouseWheel(listener: (event: MouseEvent) => void) {
            var eventName = (/Firefox/i.test(navigator.userAgent)) ? "wheel" : "mousewheel";
            this.getEl().removeEventListener(eventName, listener);
        }

        onTouchStart(listener: (event: MouseEvent) => void) {
            this.getEl().addEventListener("touchstart", listener);
        }

        unTouchStart(listener: (event: MouseEvent) => void) {
            this.getEl().removeEventListener("touchstart", listener);
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

        onFocusIn(listener: (event: FocusEvent) => void) {
            this.getEl().addEventListener("focusin", listener);
        }

        unFocusIn(listener: (event: FocusEvent) => void) {
            this.getEl().removeEventListener("focusin", listener);
        }

        onFocusOut(listener: (event: FocusEvent) => void) {
            this.getEl().addEventListener("focusout", listener);
        }

        unFocusOut(listener: (event: FocusEvent) => void) {
            this.getEl().removeEventListener("focusout", listener);
        }

        onScroll(listener: (event: Event) => void) {
            this.getEl().addEventListener("scroll", listener);
        }

        unScroll(listener: (event: Event) => void) {
            this.getEl().removeEventListener("scroll", listener);
        }

        onDrag(listener: (event: DragEvent) => void) {
            this.getEl().addEventListener("drag", listener);
        }

        unDrag(listener: (event: DragEvent) => void) {
            this.getEl().removeEventListener("drag", listener);
        }

        onDragStart(listener: (event: DragEvent) => void) {
            this.getEl().addEventListener("dragstart", listener);
        }

        unDragStart(listener: (event: DragEvent) => void) {
            this.getEl().removeEventListener("dragstart", listener);
        }

        onDragEnter(listener: (event: DragEvent) => void) {
            this.getEl().addEventListener("dragenter", listener);
        }

        unDragEnter(listener: (event: DragEvent) => void) {
            this.getEl().removeEventListener("dragenter", listener);
        }

        onDragOver(listener: (event: DragEvent) => void) {
            this.getEl().addEventListener("dragover", listener);
        }

        unDragOver(listener: (event: DragEvent) => void) {
            this.getEl().removeEventListener("dragover", listener);
        }

        onDragOut(listener: (event: DragEvent) => void) {
            this.getEl().addEventListener("dragout", listener);
        }

        unDragOut(listener: (event: DragEvent) => void) {
            this.getEl().removeEventListener("dragout", listener);
        }

        onDragLeave(listener: (event: DragEvent) => void) {
            this.getEl().addEventListener("dragleave", listener);
        }

        unDragLeave(listener: (event: DragEvent) => void) {
            this.getEl().removeEventListener("dragleave", listener);
        }

        onDrop(listener: (event: DragEvent) => void) {
            this.getEl().addEventListener("drop", listener);
        }

        unDrop(listener: (event: DragEvent) => void) {
            this.getEl().removeEventListener("drop", listener);
        }

        onDragEnd(listener: (event: DragEvent) => void) {
            this.getEl().addEventListener("dragend", listener);
        }

        unDragEnd(listener: (event: DragEvent) => void) {
            this.getEl().removeEventListener("dragend", listener);
        }


        static fromHtmlElement(element: HTMLElement, loadExistingChildren: boolean = false, parent?: Element): Element {
            return new Element(new ElementFromHelperBuilder().
                setHelper(new ElementHelper(element)).
                setLoadExistingChildren(loadExistingChildren).
                setParentElement(parent));
        }

        static fromString(s: string, loadExistingChildren: boolean = true): Element {
            var htmlEl = wemjq(s).get(0);
            var parentEl;
            if (htmlEl && htmlEl.parentElement) {
                parentEl = Element.fromHtmlElement(htmlEl.parentElement);
            }
            return this.fromHtmlElement(htmlEl, loadExistingChildren, parentEl);
        }

        static fromSelector(s: string, loadExistingChildren: boolean = true): Element[] {
            return wemjq(s).map((index, elem) => {
                var htmlEl = <HTMLElement> elem,
                    parentEl;
                if (htmlEl && htmlEl.parentElement) {
                    parentEl = Element.fromHtmlElement(htmlEl.parentElement);
                }
                return Element.fromHtmlElement(htmlEl, loadExistingChildren, parentEl);
            }).get();
        }
    }
}
