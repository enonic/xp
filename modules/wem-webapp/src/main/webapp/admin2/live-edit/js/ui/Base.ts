module LiveEdit.ui {
    var $ = $liveedit;
    var componentCount:number = 0;

    export class Base {

        private element:JQuery;

        constructor() {
        }

        public createElement(htmlString:string):JQuery {
            var id = componentCount++;

            this.element = $(htmlString);
            this.element.attr('id', 'live-edit-ui-cmp-' + id);

            return this.element;
        }

        public appendTo(parent:JQuery):void {
            if (parent.length > 0 && this.element.length > 0) {
                parent.append(this.element);
            }
        }

        public getEl():JQuery {
            return this.element;
        }
    }
}

/*
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view');

(function ($) {
    'use strict';

    // Class definition (constructor function)
    AdminLiveEdit.view.Base = function () {
        this.$element = $([]);
    };


    // Methods
    AdminLiveEdit.view.Base.prototype = {
        counter: 0,
        blankImage: 'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==',

        createElement: function (htmlString) {
            var id = AdminLiveEdit.view.Base.prototype.counter++;
            var $element = $(htmlString);
            $element.attr('id', 'live-edit-ui-cmp-' + id);
            this.$element = $element;

            return this.$element;
        },


        appendTo: function ($parent) {
            if ($parent.length > 0 && this.$element.length > 0) {
                $parent.append(this.$element);
            }
        },


        getEl: function ():JQuery {
            return this.$element;
        }

    };

}($liveedit));
*/