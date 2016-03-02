module api.dom {

    export interface ElementDimensions {
        top: number;
        left: number;
        width: number;
        height: number;
    }

    export class ElementHelper {

        private el: HTMLElement;

        static fromName(name: string): ElementHelper {
            api.util.assert(!api.util.StringHelper.isEmpty(name), 'Tag name cannot be empty');
            return new ElementHelper(document.createElement(name));
        }

        constructor(element: HTMLElement) {
            api.util.assertNotNull(element, 'Element cannot be null');
            this.el = element;
        }

        getHTMLElement(): HTMLElement {
            return this.el;
        }

        insertBefore(newEl: Element, existingEl: Element) {
            api.util.assertNotNull(newEl, 'New element cannot be null');
            api.util.assertNotNull(existingEl, 'Existing element cannot be null');
            this.el.insertBefore(newEl.getHTMLElement(), existingEl ? existingEl.getHTMLElement() : null);
        }

        insertBeforeEl(existingEl: ElementHelper) {
            existingEl.el.parentElement.insertBefore(this.el, existingEl.el);
        }

        insertAfterEl(existingEl: ElementHelper) {
            api.util.assertNotNull(existingEl, 'Existing element cannot be null');
            api.util.assertNotNull(existingEl.el.parentElement, 'Existing element\'s parentElement cannot be null');
            existingEl.el.parentElement.insertBefore(this.el, existingEl.el.nextElementSibling);
        }

        insertAfterThisEl(toInsert: ElementHelper) {
            api.util.assertNotNull(toInsert, 'Existing element cannot be null');
            this.el.parentElement.insertBefore(toInsert.el, this.el.nextElementSibling);
        }

        /*
         * @returns {api.dom.ElementHelper} ElementHelper for previous node of this element.
         */
        getPrevious(): ElementHelper {
            var previous = this.el.previousSibling;
            while (previous && previous.nodeType != Node.ELEMENT_NODE) {
                previous = previous.previousSibling;
            }
            return previous ? new ElementHelper(<HTMLElement> previous) : null;
        }

        getNext(): ElementHelper {
            var next = this.el.nextSibling;
            while (next && next.nodeType != Node.ELEMENT_NODE) {
                next = next.nextSibling;
            }
            return next ? new ElementHelper(<HTMLElement> next) : null;
        }

        getParent(): ElementHelper {
            var parent = this.el.parentElement;
            return parent ? new ElementHelper(<HTMLElement> parent) : null;
        }

        setDisabled(value: boolean): ElementHelper {
            this.el["disabled"] = value;
            return this;
        }

        isDisabled(): boolean {
            return this.el["disabled"];
        }

        getId(): string {
            return this.el.id;
        }

        setId(value: string): ElementHelper {
            this.el.id = value;
            return this;
        }

        simulate(value: string): ElementHelper {
            wemjq(<HTMLElement>this.el).simulate(value);
            return this;
        }

        setInnerHtml(value: string, escapeHtml: boolean = true): ElementHelper {
            wemjq(this.el).html(escapeHtml ? api.util.StringHelper.escapeHtml(value) : value);
            return this;
        }

        getInnerHtml(): string {
            return this.el.innerHTML;
        }

        setText(value: string): ElementHelper {
            wemjq(this.el).text(value);
            return this;
        }

        getText(): string {
            return this.el.innerText || this.el.textContent;
        }

        setAttribute(name: string, value: string): ElementHelper {
            this.el.setAttribute(name, value);
            return this;
        }

        getAttribute(name: string): string {
            return this.el.getAttribute(name);
        }

        hasAttribute(name: string): boolean {
            return this.el.hasAttribute(name);
        }

        removeAttribute(name: string): ElementHelper {
            this.el.removeAttribute(name);
            return this;
        }

        setData(name: string, value: string): ElementHelper {
            api.util.assert(!api.util.StringHelper.isEmpty(name), 'Name cannot be empty');
            api.util.assert(!api.util.StringHelper.isEmpty(value), 'Value cannot be empty');
            this.el.setAttribute('data-' + name, value);
            wemjq(this.el).data(name, value);
            return this;
        }

        getData(name: string): string {
            var data = wemjq(this.el).data(name);
            return data ? data.toString() : undefined;
        }

        getValue(): string {
            return this.el['value'];
        }

        setValue(value: string): ElementHelper {
            this.el['value'] = value;
            return this;
        }

        addClass(clsName: string): ElementHelper {
            api.util.assert(!api.util.StringHelper.isEmpty(clsName), 'Class name cannot be empty');
            // spaces are not allowed
            var classList: string[] = clsName.split(" ");
            classList.forEach((classItem: string) => {
                if (this.el.classList && !this.hasClass(classItem)) {
                    this.el.classList.add(classItem);
                }
            });
            return this;
        }

        setClass(value: string): ElementHelper {
            this.el.className = value;
            return this;
        }

        getClass(): string {
            return this.el.className;
        }

        setTitle(value: string): ElementHelper {
            this.el.title = value;
            return this;
        }

        getTitle(): string {
            return this.el.title;
        }

        hasClass(clsName: string): boolean {
            api.util.assert(!api.util.StringHelper.isEmpty(clsName), 'Class name cannot be empty');
            // spaces are not allowed
            var classList: string[] = clsName.split(" ");
            for (var i = 0; i < classList.length; i++) {
                var classItem = classList[i];
                if (!this.el.classList || !this.el.classList.contains(classItem)) {
                    return false;
                }
            }
            return true;
        }

        removeClass(clsName: string): ElementHelper {
            api.util.assert(!api.util.StringHelper.isEmpty(clsName), 'Class name cannot be empty');
            // spaces are not allowed
            var classList: string[] = clsName.split(" ");
            classList.forEach((classItem: string) => {
                if (this.el.classList) {
                    this.el.classList.remove(classItem);
                }
            });
            return this;
        }

        addEventListener(eventName: string, f: (event: Event) => any): ElementHelper {
            this.el.addEventListener(eventName, f);
            return this;
        }

        removeEventListener(eventName: string, f: (event: Event) => any): ElementHelper {
            this.el.removeEventListener(eventName, f);
            return this;
        }

        appendChild(child: Node): ElementHelper {
            return this.insertChild(child, this.countChildren());
        }

        appendChildren(children: Node[]): ElementHelper {
            children.forEach((child: Node) => {
                this.el.appendChild(child);
            });
            return this;
        }

        insertChild(child: Node, index: number): ElementHelper {
            if (index > this.countChildren() - 1) {
                this.el.appendChild(child);
            } else {
                this.el.insertBefore(child, this.getChild(index));
            }
            return this;
        }

        getTagName(): string {
            return this.el.tagName;
        }

        getDisplay(): string {
            return this.el.style.display;
        }

        setDisplay(value: string): ElementHelper {
            this.el.style.display = value;
            return this;
        }

        getVisibility(): string {
            return this.el.style.visibility;
        }

        setVisibility(value: string): ElementHelper {
            this.el.style.visibility = value;
            return this;
        }

        getPosition(): string {
            return this.getComputedProperty('position');
        }

        setPosition(value: string): ElementHelper {
            this.el.style.position = value;
            return this;
        }

        setWidth(value: string): ElementHelper {
            this.el.style.width = value;
            return this;
        }

        setWidthPx(value: number): ElementHelper {
            this.setWidth(value + "px");
            return this;
        }

        setMaxWidth(value: string): ElementHelper {
            this.el.style.maxWidth = value;
            return this;
        }

        setMaxWidthPx(value: number): ElementHelper {
            this.setMaxWidth(value + "px");
            return this;
        }

        getWidth(): number {
            return wemjq(this.el).innerWidth();
        }

        getWidthWithoutPadding(): number {
            return wemjq(this.el).width();
        }

        getWidthWithBorder(): number {
            return wemjq(this.el).outerWidth();
        }

        getWidthWithMargin(): number {
            return wemjq(this.el).outerWidth(true);
        }

        getMinWidth(): number {
            return parseFloat(this.getComputedProperty('min-width')) || 0;
        }

        setHeight(value: string): ElementHelper {
            this.el.style.height = value;
            return this;
        }

        setHeightPx(value: number): ElementHelper {
            this.setHeight(value + "px");
            return this;
        }

        getHeight(): number {
            return wemjq(this.el).innerHeight();
        }

        setMaxHeight(value: string): ElementHelper {
            this.el.style.maxHeight = value;
            return this;
        }

        setMaxHeightPx(value: number): ElementHelper {
            this.setMaxHeight(value + "px");
            return this;
        }

        getHeightWithoutPadding(): number {
            return wemjq(this.el).height();
        }

        getHeightWithBorder(): number {
            return wemjq(this.el).outerHeight();
        }

        getHeightWithMargin(): number {
            return wemjq(this.el).outerHeight(true);
        }

        setTop(value: string): ElementHelper {
            this.el.style.top = value;
            return this;
        }

        setTopPx(value: number): ElementHelper {
            return this.setTop(value + "px");
        }

        getTopPx(): number {
            return parseFloat(this.getTop());
        }

        getTop(): string {
            return this.el.style.top;
        }

        setBottom(value: string): ElementHelper {
            this.el.style.bottom = value;
            return this;
        }

        setBottomPx(value: number): ElementHelper {
            return this.setBottom(value + "px");
        }

        getLeft(): string {
            return this.el.style.left;
        }

        getLeftPx(): number {
            return parseFloat(this.getLeft());
        }

        setLeftPx(value: number): ElementHelper {
            return this.setLeft(value + "px");
        }

        setLeft(value: string): ElementHelper {
            this.el.style.left = value;
            return this;
        }

        setRight(value: string): ElementHelper {
            this.el.style.right = value;
            return this;
        }

        setRightPx(value: number): ElementHelper {
            return this.setRight(value + "px");
        }

        getMarginLeft(): number {
            return parseFloat(this.getComputedProperty('margin-left')) || 0;
        }

        setMarginLeft(value: string): ElementHelper {
            this.el.style.marginLeft = value;
            return this;
        }

        getMarginRight(): number {
            return parseFloat(this.getComputedProperty('margin-right'));
        }

        setMarginRight(value: string): ElementHelper {
            this.el.style.marginRight = value;
            return this;
        }

        getMarginTop(): number {
            return parseFloat(this.getComputedProperty('margin-top'));
        }

        setMarginTop(value: string): ElementHelper {
            this.el.style.marginTop = value;
            return this;
        }

        getMarginBottom(): number {
            return parseFloat(this.getComputedProperty('margin-bottom'));
        }

        setMarginBottom(value: string): ElementHelper {
            this.el.style.marginBottom = value;
            return this;
        }

        setStroke(value: string): ElementHelper {
            this.el.style.stroke = value;
            return this;
        }

        getStroke(): string {
            return this.getComputedProperty('stroke');
        }

        setStrokeDasharray(value: string): ElementHelper {
            this.el.style.strokeDasharray = value;
            return this;
        }

        getStrokeDasharray(): string {
            return this.getComputedProperty('stroke-dasharray');
        }

        setFill(value: string): ElementHelper {
            this.el.style.fill = value;
            return this;
        }

        getFill(): string {
            return this.getComputedProperty('fill');
        }

        getPaddingLeft(): number {
            return parseFloat(this.getComputedProperty('padding-left')) || 0;
        }

        setPaddingLeft(value: string): ElementHelper {
            this.el.style.paddingLeft = value;
            return this;
        }

        getPaddingRight(): number {
            return parseFloat(this.getComputedProperty('padding-right'));
        }

        setPaddingRight(value: string): ElementHelper {
            this.el.style.paddingRight = value;
            return this;
        }

        getPaddingTop(): number {
            return parseFloat(this.getComputedProperty('padding-top'));
        }

        setPaddingTop(value: string): ElementHelper {
            this.el.style.paddingTop = value;
            return this;
        }

        getPaddingBottom(): number {
            return parseFloat(this.getComputedProperty('padding-bottom'));
        }

        setPaddingBottom(value: string): ElementHelper {
            this.el.style.paddingBottom = value;
            return this;
        }

        getBorderTopWidth(): number {
            return parseFloat(this.getComputedProperty('border-top-width'));
        }

        getBorderBottomWidth(): number {
            return parseFloat(this.getComputedProperty('border-bottom-width'));
        }

        getBorderRightWidth(): number {
            return parseFloat(this.getComputedProperty('border-right-width'));
        }

        getBorderLeftWidth(): number {
            return parseFloat(this.getComputedProperty('border-left-width'));
        }

        setZindex(value: number): ElementHelper {
            this.el.style.zIndex = value.toString();
            return this;
        }

        getBoundingClientRect(): ClientRect {
            return this.el.getBoundingClientRect();
        }

        scrollIntoView(top?: boolean): ElementHelper {
            this.el.scrollIntoView(top);
            return this;
        }

        getScrollTop(): number {
            return this.el.scrollTop;
        }

        setScrollTop(top: number): ElementHelper {
            this.el.scrollTop = top;
            return this;
        }

        getTabIndex(): number {
            return this.el.tabIndex;
        }

        setTabIndex(tabIndex: number): ElementHelper {
            this.el.tabIndex = tabIndex;
            return this;
        }

        getFontSize(): string {
            return this.getComputedProperty('font-size');
        }

        setFontSize(value: string): ElementHelper {
            this.el.style.fontSize = value;
            return this;
        }

        setBackgroundImage(value: string): ElementHelper {
            this.el.style.backgroundImage = value;
            return this;
        }

        setCursor(value: string): ElementHelper {
            this.el.style.cursor = value;
            return this;
        }

        getCursor(): string {
            return this.el.style.cursor;
        }

        getElementsByClassName(className: string): ElementHelper[] {
            var items: ElementHelper[] = [];
            if (className) {
                var nodeList = this.el.getElementsByClassName(className);
                for (var i = 0; i < nodeList.length; i++) {
                    items.push(new ElementHelper(<HTMLElement>nodeList.item(i)));
                }
            }
            return items;
        }

        remove() {
            var parent = this.el.parentElement;
            if (parent) {
                parent.removeChild(this.el);
            }
        }

        contains(element: HTMLElement): boolean {
            return this.el.contains ? this.el.contains(element) : !!(this.el.compareDocumentPosition(element) & 16);
        }

        /**
         * Calculate offset relative to document
         * @returns {{left: number, top: number}}
         */
        getOffset(): { top:number; left:number;
        } {
            return wemjq(this.el).offset();
        }

        setOffset(offset: { top:number; left:number; }): ElementHelper {
            wemjq(this.el).offset(offset);
            return this;
        }

        getDimensions(): ElementDimensions {
            var offset = this.getOffset();

            return {
                top: offset.top,
                left: offset.left,
                width: this.getWidthWithBorder(),
                height: this.getHeightWithBorder()
            };
        }

        getDimensionsTopRelativeToParent(): ElementDimensions {
            var offsetToParent = this.getOffsetToParent();
            var offsetToDocument = this.getOffset();

            return {
                top: offsetToParent.top,
                left: offsetToDocument.left,
                width: this.getWidthWithBorder(),
                height: this.getHeightWithBorder()
            };
        }

        /**
         * Goes up the hierarchy and returns first non-statically positioned parent
         * @returns {HTMLElement}
         */
        getOffsetParent(): HTMLElement {
            return wemjq(this.el).offsetParent()[0];
        }

        /**
         * Calculates offset relative to first positioned parent ( element with position: relative, absolute or fixed )
         * @returns {{top: number, left: number}}
         */
        getOffsetToParent(): { top:number; left:number;
        } {
            return wemjq(this.el).position();
        }

        getOffsetTop(): number {
            return this.getOffset().top;
        }

        getOffsetTopRelativeToParent(): number {
            return this.el.offsetTop;
        }

        getOffsetLeft(): number {
            return this.getOffset().left;
        }

        getOffsetLeftRelativeToParent(): number {
            return this.el.offsetLeft;
        }

        getComputedProperty(name: string, pseudoElement: string = null): string {
            return window.getComputedStyle(this.el, pseudoElement).getPropertyValue(name);
        }

        focus() {
            this.el.focus();
        }

        blur() {
            this.el.blur();
        }

        /**
         * Returns the index of this element among it's siblings. Returns 0 if first or only child.
         */
        getSiblingIndex(): number {

            var i = 0;
            var prev: HTMLElement = this.el;
            while ((prev = <HTMLElement>prev.previousElementSibling) != null) {
                i++;
            }
            return i;
        }

        isVisible(): boolean {
            return wemjq(this.el).is(':visible');
        }

        countChildren(): number {
            return this.getChildren().length;
        }

        getChild(index: number): Node {
            return this.getChildren()[index];
        }

        getChildren(): Node[] {

            return this.el.children || //children property not supported for IE SVGelement, Document and DocumentFragment
                   Array.prototype.slice.call(this.el.childNodes).filter((childNode: Node) => {
                       return (childNode.nodeType == Node.ELEMENT_NODE);
                   });
        }
    }
}
