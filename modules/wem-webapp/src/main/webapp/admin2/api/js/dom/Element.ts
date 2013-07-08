module api_dom {

    export class Element {

        private static constructorCounter:number = 0;

        private el:ElementHelper;

        private id:string;

        private parent:Element;

        private children:Element[];

        constructor(elementName:string, idPrefix?:string, className?:string, elHelper?:ElementHelper) {
            if (elHelper == null) {
                this.el = ElementHelper.fromName(elementName);
            } else {
                this.el = elHelper;
            }
            if (idPrefix != null) {
                this.id = idPrefix + '-' + (++Element.constructorCounter);
                this.el.setId(this.id);
            }
            if (className != null) {
                this.el.setClass(className);
            }
            this.children = [];
        }

        init() {
            this.afterRender();
            this.children.forEach((child) => {
                child.init();
            })
        }

        afterRender() {

        }

        className(value:string):Element {
            this.getHTMLElement().className = value;
            return this;
        }

        show() {
            // Using jQuery to show, since it seems to contain some smartness
            jQuery(this.el.getHTMLElement()).show();
        }

        hide() {
            // Using jQuery to hide, since it seems to contain some smartness
            jQuery(this.el.getHTMLElement()).hide();
        }

        isVisible() {
            var displayed = this.el.getDisplay() != "none";
            var visible = this.el.getVisibility() != "hidden";
            var sized = this.el.getWidth() != 0 || this.el.getHeight() != 0;
            return displayed && visible && sized;
        }
        
        empty() {
            this.el.setInnerHtml("");
        }

        setClass(className:string):api_dom.Element {
            this.el.setClass(className);
            return this;
        }

        addClass(className:string):api_dom.Element {
            this.el.addClass(className);
            return this;
        }

        hasClass(className:string):bool {
            return this.el.hasClass(className);
        }

        removeClass(className:string):api_dom.Element {
            this.el.removeClass(className);
            return this;
        }

        getId():string {
            return this.id;
        }

        getEl():ElementHelper {
            return this.el;
        }

        getHTMLElement():HTMLElement {
            return this.el.getHTMLElement();
        }

        appendChild(child:api_dom.Element) {
            this.el.appendChild(child.getEl().getHTMLElement());
            child.setParent(this);
            this.children.push(child);
        }

        prependChild(child:api_dom.Element) {
            this.el.getHTMLElement().insertBefore(child.getHTMLElement(), this.el.getHTMLElement().firstChild);
        }

        removeChild(child:api_dom.Element) {
            if (this.el.getHTMLElement().contains(child.getHTMLElement())) {
                this.el.getHTMLElement().removeChild(child.getHTMLElement());
            }
        }

        insertAfterEl(existingEl:Element) {
            this.el.insertAfterEl(existingEl);
        }

        insertBeforeEl(existingEl:Element) {
            this.el.insertBeforeEl(existingEl);
        }

        removeChildren() {
            var htmlEl = this.el.getHTMLElement();
            while (htmlEl.firstChild) {
                htmlEl.removeChild(htmlEl.firstChild);
            }
        }

        private setParent(parent:Element) {
            this.parent = parent;
        }

        getParent():Element {
            return this.parent;
        }

        getChildren():Element[] {
            return this.children;
        }

        remove() {
            var htmlEl = this.el.getHTMLElement();
            htmlEl.parentNode.removeChild(htmlEl);
        }
    }
}
