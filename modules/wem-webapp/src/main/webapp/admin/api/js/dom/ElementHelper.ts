module api_dom {

    export class ElementHelper {

        private el:HTMLElement;

        static fromName(name:string):ElementHelper {
            return new ElementHelper(document.createElement(name));
        }

        constructor(element:HTMLElement) {
            this.el = element;
        }

        getHTMLElement():HTMLElement {
            return this.el;
        }

        insertBefore(newEl:Element, existingEl:Element) {
            this.el.insertBefore(newEl.getHTMLElement(), existingEl ? existingEl.getHTMLElement() : null);
        }

        insertBeforeEl(existingEl:Element) {
            existingEl.getHTMLElement().parentNode.insertBefore(this.el, existingEl.getHTMLElement());
        }

        insertAfterEl(existingEl:Element) {
            existingEl.getHTMLElement().parentNode.insertBefore(this.el, existingEl.getHTMLElement().nextSibling);
        }

        /*
         * @returns {api_dom.ElementHelper} ElementHelper for previous node of this element.
         */
        getPrevious():ElementHelper {
            var previous = this.el.previousSibling;
            while (previous && previous.nodeType != Node.ELEMENT_NODE) {
                previous = previous.previousSibling;
            }
            return previous ? new ElementHelper(<HTMLElement> previous) : null;
        }

        setDisabled(value:boolean):ElementHelper {
            this.el.disabled = value;
            return this;
        }

        isDisabled():boolean {
            return this.el.disabled;
        }

        setId(value:string):ElementHelper {
            this.el.id = value;
            return this;
        }

        setInnerHtml(value:string):ElementHelper {
            jQuery(this.el).html(value);
            return this;
        }

        getInnerHtml():string {
            return this.el.innerHTML;
        }

        setAttribute(name:string, value:string):ElementHelper {
            this.el.setAttribute(name, value);
            return this;
        }

        getAttribute(name:string):string {
            return this.el.getAttribute(name);
        }

        hasAttribute(name:string):boolean {
            return this.el.hasAttribute(name);
        }

        removeAttribute(name:string):ElementHelper {
            this.el.removeAttribute(name);
            return this;
        }

        getValue():string {
            return this.el['value'];
        }

        setValue(value:string):ElementHelper {
            this.el['value'] = value;
            return this;
        }

        addClass(clsName:string):ElementHelper {
            if (!this.hasClass(clsName)) {
                if (this.el.className === '') {
                    this.el.className += clsName;
                }
                else {
                    this.el.className += ' ' + clsName;
                }
            }
            return this;
        }

        setClass(value:string):ElementHelper {
            this.el.className = value;
            return this;
        }

        hasClass(clsName:string):boolean {
            return this.el.className.match(new RegExp('(\\s|^)' + clsName + '(\\s|$)')) !== null;
        }

        removeClass(clsName:string):ElementHelper {
            if (this.hasClass(clsName)) {
                var reg = new RegExp('(\\s|^)' + clsName + '(\\s|$)');
                this.el.className = this.el.className.replace(reg, '');
            }
            return this;
        }

        addEventListener(eventName:string, f:(event:Event) => any):ElementHelper {
            this.el.addEventListener(eventName, f);
            return this;
        }

        removeEventListener(eventName:string, f:(event:Event) => any):ElementHelper {
            this.el.removeEventListener(eventName, f);
            return this;
        }

        appendChild(child:Node):ElementHelper {
            this.el.appendChild(child);
            return this;
        }

        setData(name:string, value:string):ElementHelper {
            jQuery(this.el).attr('data-' + name, value);
            return this;
        }

        getData(name:string):string {
            return jQuery(this.el).data(name);
        }

        getDisplay():string {
            return this.el.style.display;
        }

        setDisplay(value:string):ElementHelper {
            this.el.style.display = value;
            return this;
        }

        getVisibility():string {
            return this.el.style.visibility;
        }

        setVisibility(value:string):ElementHelper {
            this.el.style.visibility = value;
            return this;
        }

        setPosition(value:string):ElementHelper {
            this.el.style.position = value;
            return this;
        }

        setWidth(value:string):ElementHelper {
            this.el.style.width = value;
            return this;
        }

        getWidth():number {
            return this.el.offsetWidth;
        }

        setHeight(value:string):ElementHelper {
            this.el.style.height = value;
            return this;
        }

        getHeight():number {
            return parseFloat(this.getComputedProperty('height'));
        }

        setTop(value:string):ElementHelper {
            this.el.style.top = value;
            return this;
        }

        setTopPx(value:number):ElementHelper {
            return this.setTop(value + "px");
        }

        setLeft(value:string):ElementHelper {
            this.el.style.left = value;
            return this;
        }

        setRight(value:string):ElementHelper {
            this.el.style.right = value;
            return this;
        }

        setMarginLeft(value:string):ElementHelper {
            this.el.style.marginLeft = value;
            return this;
        }

        setMarginRight(value:string):ElementHelper {
            this.el.style.marginRight = value;
            return this;
        }

        setMarginTop(value:string):ElementHelper {
            this.el.style.marginTop = value;
            return this;
        }

        setMarginBottom(value:string):ElementHelper {
            this.el.style.marginBottom = value;
            return this;
        }

        getPaddingLeft():number {
            return parseFloat(this.getComputedProperty('padding-left'));
        }

        getBorderTopWidth():number {
            return parseFloat(this.getComputedProperty('border-top-width'));
        }

        getBorderBottomWidth():number {
            return parseFloat(this.getComputedProperty('border-bottom-width'));
        }

        setZindex(value:number):ElementHelper {
            this.el.style.zIndex = value.toString();
            return this;
        }

        setBackgroundImage(value:string):ElementHelper {
            this.el.style.backgroundImage = value;
            return this;
        }

        remove() {
            var parent = this.el.parentElement;
            parent.removeChild(this.el);
        }

        getOffset():{ top:number; left:number; } {
            var el = this.el;
            var x = 0,
                y = 0;
            while (el && !isNaN(el.offsetLeft) && !isNaN(el.offsetTop)) {
                x += el.offsetLeft - el.scrollLeft;
                y += el.offsetTop - el.scrollTop;
                el = <HTMLElement> el.offsetParent;
            }
            return { top: y, left: x };
        }

        getOffsetTop():number {
            return this.el.offsetTop;
        }

        getComputedProperty(name:string, pseudoElement: string = null):string {
            return window.getComputedStyle(this.el, pseudoElement).getPropertyValue(name);
        }

    }
}
