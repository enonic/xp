module LiveEdit.ui {
    var $ = $liveedit;

    export class HtmlElementReplacer extends LiveEdit.ui.Base {
        private elementsToReplaceSpec = ['iframe', 'object'];

        constructor() {
            super();
            this.replaceElementsWithPlaceholders();

            console.log('HtmlElementReplacer instantiated. Using jQuery ' + $().jquery);
        }


        replaceElementsWithPlaceholders():void {
            var elements = this.getElements();
            elements.each((i) => {
                this.replace(elements[i]);
            });
        }


        replace($element):void {
            this.hideElement($element);
            this.addPlaceholder($element);
        }


        addPlaceholder($element):void {
            this.createPlaceholder($element).insertAfter($element);
        }


        createPlaceholder($element:JQuery):JQuery {
            var $placeholder = $('<div></div>');
            $placeholder.addClass('live-edit-html-element-placeholder');
            $placeholder.width(this.getElementWidth($element));
            $placeholder.height(this.getElementHeight($element));

            var $icon = $('<div/>');
            $icon.addClass(this.resolveIconCssClass($element));
            $icon.append('<div>' + $element[0].tagName.toLowerCase() + '</div>');
            $placeholder.append($icon);

            return $placeholder;
        }


        getElements():JQuery {
            return $('[data-live-edit-type=part] > ' + this.elementsToReplaceSpec.toString());
        }


        getElementWidth($element):number {
            var attrWidth = $element.attr('width');
            if (!attrWidth) {
                // Return computed style width (int/pixels);
                // -2 for placeholder border
                return $element.width() - 2;
            }
            return attrWidth;
        }


        getElementHeight($element):number {
            var attrHeight = $element.attr('height');
            if (!attrHeight) {
                // Return computed style height (int/pixels);
                // -2 for placeholder border
                return $element.height() - 2;
            }
            return attrHeight;
        }


        showElement($element):void {
            $element.show();
        }


        hideElement($element):void {
            $element.hide();
        }


        resolveIconCssClass($element):string {
            var tagName = $element[0].tagName.toLowerCase();
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