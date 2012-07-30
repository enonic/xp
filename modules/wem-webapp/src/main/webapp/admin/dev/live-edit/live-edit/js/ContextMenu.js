AdminLiveEdit.ContextMenu = (function () {

    var buttonConfig = {
        'region'    : [],
        'window'    : ['parent', 'drag', 'settings', 'remove'],
        'content'   : ['parent'],
        'paragraph' : ['parent']
    };


    function createMenu() {
        $liveedit('body')
            .append('<div id="live-edit-context-menu" style="top:-5000px; left:-5000px;"><div id="live-edit-context-menu-inner"></div></div>');
    }


    function getMenu() {
        return $liveedit('#live-edit-context-menu');
    }


    function hide() {
        var $menu = getMenu();
        $menu.css({
            top: '-5000px',
            left: '-5000px',
            right: ''

        });
        $liveedit('body').append($menu);
    }


    function fadeOutAndHide() {
        var $menu = getMenu();
        $menu.fadeOut(500, function () {
            AdminLiveEdit.Highlighter.deselect();
        });
    }


    function createButtons() {
        var button = AdminLiveEdit.Button;

        // Hard code the buttons for now.
        var $selectParentButton = button.create({
            text: 'Parent',
            id: 'live-edit-button-parent',
            iconCls: 'live-edit-icon-parent',
            handler: function (event) {
                event.stopPropagation();
                AdminLiveEdit.Highlighter.selectParent();

            }
        });

        var $settingsButton = button.create({
            text: 'Settings',
            id: 'live-edit-button-settings',
            iconCls: 'live-edit-icon-settings',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        var $dragButton = button.create({
            text: 'Drag',
            id: 'live-edit-button-drag',
            iconCls: 'live-edit-icon-drag',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        $dragButton.on('mousedown', function (event) {
            this._mouseDown = true;
            AdminLiveEdit.DragDrop.enable();
        });

        $dragButton.on('mousemove', function (event) {
            if (this._mouseDown) {
                this._mouseDown = false;
                fadeOutAndHide();
                var highlighter = AdminLiveEdit.Highlighter;
                var $selectedComponent = highlighter.getSelected();
                var evt = document.createEvent('MouseEvents');
                evt.initMouseEvent('mousedown', true, true, window, 0, event.screenX, event.screenY, event.clientX, event.clientY, false,
                    false, false, false, 0, null);

                $selectedComponent[0].dispatchEvent(evt);
            }
        });

        $dragButton.on('mouseup', function (event) {
            this._mouseDown = false;
            AdminLiveEdit.DragDrop.disable();
        });

        var $removeButton = button.create({
            text: 'Remove',
            id: 'live-edit-button-remove',
            iconCls: 'live-edit-icon-remove',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        var $container = $liveedit('#live-edit-context-menu-inner');
        $container.append($selectParentButton);
        $container.append($dragButton);
        $container.append($settingsButton);
        $container.append($removeButton);
    }


    function updateButtonTexts() {
        var $parentComponentOfSelected = $liveedit(AdminLiveEdit.Highlighter.getSelected().parents('[data-live-edit-type]')[0]);
        var parentComponentType = AdminLiveEdit.Util.getPageComponentType($parentComponentOfSelected);
        $liveedit('#live-edit-button-parent').find('.live-edit-button-text').html(parentComponentType);
    }


    function getAllButtons() {
        return $liveedit('#live-edit-context-menu-inner').find('.live-edit-button');
    }


    function updateButtonsDisplay($component) {
        var componentType = AdminLiveEdit.Util.getPageComponentType($component);
        if (buttonConfig.hasOwnProperty(componentType)) {
            var buttonArray = buttonConfig[componentType];
            getAllButtons().each(function (i) {
                var button = $liveedit(this);
                var id = button.attr('id');
                var subStr = id.substring(id.lastIndexOf('-') + 1, id.length);
                if (buttonArray.indexOf(subStr) > -1) {
                    button.show();
                } else {
                    button.hide();
                }
            });
        }
    }


    function moveMenuTo(event, $component) {
        var util = AdminLiveEdit.Util;
        var componentType = util.getPageComponentType($component);
        var $menu = getMenu();
        $menu.show();

        updateButtonsDisplay($component);

        var componentBoxModel = AdminLiveEdit.Util.getBoxModel($component);
        var topPos = Math.round(componentBoxModel.top);
        var leftPos = Math.round(componentBoxModel.left + componentBoxModel.width - $menu.width());

        $menu.css({
            top: topPos,
            left: leftPos
        });

        updateButtonTexts();
    }


    function initSubscribers() {
        $liveedit.subscribe('/page/component/select', moveMenuTo);
        $liveedit.subscribe('/page/component/deselect', hide);
    }


    function init() {
        createMenu();
        createButtons();
        initSubscribers();
    }

    // ***********************************************************************************************************************************//
    // Define public methods

    return {
        init: init
    };

}());