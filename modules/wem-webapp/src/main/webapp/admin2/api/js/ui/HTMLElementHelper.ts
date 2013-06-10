module api_ui {

    export class HTMLElementHelper {

        private el:HTMLElement;

        static fromName(name:string):HTMLElementHelper {
            return new HTMLElementHelper(document.createElement(name));
        }

        constructor(element:HTMLElement) {
            this.el = element;
        }

        getHTMLElement():HTMLElement {
            return this.el;
        }

        setDisabled(value:bool):HTMLElementHelper {
            this.el.disabled = value;
            return this;
        }

        setId(value:string):HTMLElementHelper {
            this.el.id = value;
            return this;
        }

        setInnerHtml(value:string):HTMLElementHelper {
            this.el.innerHTML = value;
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

        appendChild(child:HTMLElement) {
            this.el.appendChild(child);
        }

        setDisplay(value:string):HTMLElementHelper {
            this.el.style.display = value;
            return this;
        }

        setPosition(value:string):HTMLElementHelper {
            this.el.style.position = value;
            return this;
        }

        setWidth(value:string):HTMLElementHelper {
            this.el.style.width = value;
            return this;
        }

        setHeight(value:string):HTMLElementHelper {
            this.el.style.height = value;
            return this;
        }

        setTop(value:string):HTMLElementHelper {
            this.el.style.top = value;
            return this;
        }

        setLeft(value:string):HTMLElementHelper {
            this.el.style.left = value;
            return this;
        }

        setMarginLeft(value:string):HTMLElementHelper {
            this.el.style.marginLeft = value;
            return this;
        }

        setMarginRight(value:string):HTMLElementHelper {
            this.el.style.marginRight = value;
            return this;
        }

        setMarginTop(value:string):HTMLElementHelper {
            this.el.style.marginTop = value;
            return this;
        }

        setMarginBottom(value:string):HTMLElementHelper {
            this.el.style.marginBottom = value;
            return this;
        }

        setZindex(value:number):HTMLElementHelper {
            this.el.style.zIndex = value.toString();
            return this;
        }

        remove() {
            var parent = this.el.parentElement;
            parent.removeChild(this.el);
        }

    }
}
