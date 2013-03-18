AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.htmleditor');


AdminLiveEdit.view.htmleditor.InPlaceEditor = (function ($) {
    'use strict';

    function activate($paragraph) {
        $paragraph.get(0).contentEditable = true;
        $paragraph.get(0).focus();
    }


    function deActivate($paragraph) {
        $paragraph.get(0).contentEditable = false;
        $paragraph.get(0).blur();
    }


    function init() {
        $(window).on('component:paragraph:edit:init', function (event, $paragraph) {
            activate($paragraph);
        });
        $(window).on('component:paragraph:edit:leave', function (event, $paragraph) {
            deActivate($paragraph);
        });
    }


    // ********************************************************************************************************************************** //
    // Public methods

    return {
        initialize: init
    };

}($liveedit));