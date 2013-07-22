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

        setDisabled(value:bool):ElementHelper {
            this.el.disabled = value;
            return this;
        }

        isDisabled():bool {
            return this.el.disabled;
        }

        setId(value:string):ElementHelper {
            this.el.id = value;
            return this;
        }

        setInnerHtml(value:string):ElementHelper {
            this.el.innerHTML = value;
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

        hasAttribute(name:string):bool {
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

        hasClass(clsName:string):bool {
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

        appendChild(child:HTMLElement):ElementHelper {
            this.el.appendChild(child);
            return this;
        }

        setData(name:string, value:string):ElementHelper {
            var any = <any>this.el;
            any._data[name] = value;
            return this;
        }

        getData(name:string):string {
            var any = <any>this.el;
            return any._data[name];
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
            return this.el.offsetHeight;
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

        getPaddingLeft(): number {
            var stringValue = window.getComputedStyle(this.getHTMLElement(), null).getPropertyValue('padding-left');
            return +stringValue.replace('px', '');
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

    }
}
