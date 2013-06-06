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

        setDisabled(value:bool) {
            this.el.disabled = value;
        }

        setId(value:string) {
            this.el.id = value;
        }

        setInnerHtml(value:string) {
            this.el.innerHTML = value;
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

        hasClass(clsName:string): bool {
            return this.el.className.match(new RegExp('(\\s|^)' + clsName + '(\\s|$)')) !== null;
        }

        removeClass(clsName:string) {
            if (this.hasClass(clsName)) {
                var reg = new RegExp('(\\s|^)' + clsName + '(\\s|$)');
                this.el.className = this.el.className.replace(reg, ' ');
            }
        }

        addEventListener(eventName:string, f:() => any) {
            this.el.addEventListener(eventName, f);
        }

        appendChild(child:HTMLElement) {
            this.el.appendChild(child);
        }

    }
}
