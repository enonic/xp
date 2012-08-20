(function () {
    'use strict';

    // Class definition (constructor function)
    var button = AdminLiveEdit.ui.Button = function () {
    };

    // Inherits ui.Base
    button.prototype = new AdminLiveEdit.ui.Base();

    // Fix constructor as it now is Base
    button.constructor = button;

    // Shorthand ref to the prototype
    var p = button.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.createButton = function (config) {
        var id = config.id || '';
        var text = config.text || '';
        var iconCls = config.iconCls || '';
        var html = '<div data-live-edit-ui-cmp-id="' + id + '" class="live-edit-button">';
        if (iconCls !== '') {
            html += '<span class="live-edit-button-icon ' + iconCls + '"></span>';
        }
        html += '<span class="live-edit-button-text">' + text + '</span></div>';
        var $button = this.createElement(html);

        if (config.handler) {
            $button.on('click', function (event) {
                config.handler.call(this, event);
            });
        }
        return $button;
    };

}());