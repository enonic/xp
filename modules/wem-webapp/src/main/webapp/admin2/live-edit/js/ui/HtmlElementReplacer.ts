module LiveEdit.ui {
    var $ = $liveedit;

    export class HtmlElementReplacer extends LiveEdit.ui.Base {
        private elementsToReplace = ['iframe', 'object'];

        constructor() {
            super();
            this.replaceElementsWithPlaceholders();

            console.log('HtmlElementReplacer instantiated. Using jQuery ' + $().jquery);
        }


        registerGlobalListeners() {
        }


        replaceElementsWithPlaceholders() {
            var me = this;
            me.getElements().each(function () {
                me.replace($(this));
            });
        }


        replace($element) {
            this.hideElement($element);
            this.addPlaceholder($element);
        }


        addPlaceholder($element) {
            this.createPlaceholder($element).insertAfter($element);
        }


        createPlaceholder($element) {
            var me = this;
            var $placeholder = $('<div></div>');
            $placeholder.addClass('live-edit-html-element-placeholder');
            $placeholder.width(me.getElementWidth($element));
            $placeholder.height(me.getElementHeight($element));

            var $icon = $('<div/>');
            $icon.addClass(me.resolveIconCssClass($element));
            $icon.append('<div>' + $element[0].tagName.toLowerCase() + '</div>');
            $placeholder.append($icon);

            return $placeholder;
        }


        getElements() {
            return $('[data-live-edit-type=part] > ' + this.elementsToReplace.toString());
        }


        getElementWidth($element) {
            var attrWidth = $element.attr('width');
            if (!attrWidth) {
                // Return computed style width (int/pixels);
                // -2 for placeholder border
                return $element.width() - 2;
            }
            return attrWidth;
        }


        getElementHeight($element) {
            var attrHeight = $element.attr('height');
            if (!attrHeight) {
                // Return computed style height (int/pixels);
                // -2 for placeholder border
                return $element.height() - 2;
            }
            return attrHeight;
        }


        showElement($element) {
            $element.show();
        }


        hideElement($element) {
            $element.hide();
        }


        resolveIconCssClass($element) {
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