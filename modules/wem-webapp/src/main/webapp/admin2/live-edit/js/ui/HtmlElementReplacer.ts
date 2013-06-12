module LiveEdit.ui {
    var $ = $liveEdit;

    export class HtmlElementReplacer extends LiveEdit.ui.Base {
        private elementsToReplaceSpec = ['iframe', 'object'];

        constructor() {
            super();
            this.replaceElementsWithPlaceholders();

            console.log('HtmlElementReplacer instantiated. Using jQuery ' + $().jquery);
        }


        replaceElementsWithPlaceholders():void {
            var elements:JQuery = this.getElements();
            var element:JQuery;
            elements.each((i) => {
                element = $(elements[i]);
                this.replace(element);
            });
        }


        replace(element):void {
            this.hideElement(element);
            this.addPlaceholder(element);
        }


        addPlaceholder(element:JQuery):void {
            this.createPlaceholder(element).insertAfter(element);
        }


        createPlaceholder(element:JQuery):JQuery {
            var placeholder:JQuery = $('<div></div>');
            placeholder.addClass('live-edit-html-element-placeholder');
            placeholder.width(this.getElementWidth(element));
            placeholder.height(this.getElementHeight(element));

            var icon:JQuery = $('<div/>');
            icon.addClass(this.resolveIconCssClass(element));
            icon.append('<div>' + element[0].tagName.toLowerCase() + '</div>');
            placeholder.append(icon);

            return placeholder;
        }


        getElements():JQuery {
            return $('[data-live-edit-type=part] > ' + this.elementsToReplaceSpec.toString());
        }


        getElementWidth(element:JQuery):number {
            var attrWidth = parseInt(element.attr('width'), 10);
            if (!attrWidth) {
                // Return computed style width (int/pixels);
                // -2 for placeholder border
                return element.width() - 2;
            }
            return attrWidth;
        }


        getElementHeight(element:JQuery):number {
            var attrHeight = parseInt(element.attr('height'), 10);
            if (!attrHeight) {
                // Return computed style height (int/pixels);
                // -2 for placeholder border
                return element.height() - 2;
            }
            return attrHeight;
        }


        showElement(element:JQuery):void {
            element.show(null);
        }


        hideElement(element:JQuery):void {
            element.hide(null);
        }


        resolveIconCssClass(element:JQuery):string {
            var tagName = element[0].tagName.toLowerCase();
            var clsName = '';
            if (tagName === 'iframe') {
                clsName = 'live-edit-iframe';
            } else {
                clsName = 'live-edit-object';
            }
            return clsName;
        }

    }
}