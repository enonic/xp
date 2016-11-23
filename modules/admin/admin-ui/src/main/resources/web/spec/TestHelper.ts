module FormOptionSetSpec {

    import Element = api.dom.Element;

    export function hasElement(el: Element, query: string): boolean {
        return el.getEl().getHTMLElement().querySelector(query) !== null;
    }

    export function hasElementByClassName(el: Element, className: string): boolean {
        return el.getEl().getElementsByClassName(className).length > 1;
    }
}