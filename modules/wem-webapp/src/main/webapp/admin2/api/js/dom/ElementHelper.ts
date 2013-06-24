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

        setDisabled(value:bool):ElementHelper {
            this.el.disabled = value;
            return this;
        }

        setId(value:string):ElementHelper {
            this.el.id = value;
            return this;
        }

        setInnerHtml(value:string):ElementHelper {
            this.el.innerHTML = value;
            return this;
        }

        setValue(value:string):ElementHelper {
            this.el.setAttribute("value", value);
            return this;
        }

        addClass(clsName:string) {
            if (!this.hasClass(clsName)) {
                if (this.el.className === '') {
                    this.el.className += clsName;
                }
                else {
                    this.el.className += ' ' + clsName;
                }
            }
        }

        hasClass(clsName:string):bool {
            return this.el.className.match(new RegExp('(\\s|^)' + clsName + '(\\s|$)')) !== null;
        }

        removeClass(clsName:string) {
            if (this.hasClass(clsName)) {
                var reg = new RegExp('(\\s|^)' + clsName + '(\\s|$)');
                this.el.className = this.el.className.replace(reg, '');
            }
        }

        addEventListener(eventName:string, f:(event:Event) => any) {
            this.el.addEventListener(eventName, f);
        }

        removeEventListener(eventName:string, f:(event:Event) => any) {
            this.el.removeEventListener(eventName, f);
        }

        appendChild(child:HTMLElement) {
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

        getVisibility() {
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

        setLeft(value:string):ElementHelper {
            this.el.style.left = value;
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

        getOffset() {
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
