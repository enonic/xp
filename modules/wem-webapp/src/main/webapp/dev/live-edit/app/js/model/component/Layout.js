AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.model.component');

(function ($) {
    'use strict';

    var layout = AdminLiveEdit.model.component.Layout = function () {
        this.cssSelector = '[data-live-edit-type=layout]';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    layout.prototype = new AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    layout.constructor = layout;

    var proto = layout.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


}($liveedit));