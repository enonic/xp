AdminLiveEdit.ui.ContextMenu = (function () {

    var buttonConfig = {
        'page'      : ['settings'],
        'region'    : ['parent', 'insert', 'reset', 'empty'],
        'window'    : ['parent', 'drag', 'settings', 'remove'],
        'content'   : ['parent', 'view'],
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
            $liveedit.publish('/page/component/deselect');
        });
    }


    function createButtons() {
        var button = AdminLiveEdit.ui.Button;

        // Hard code the buttons for now.

        var $parentButton = button.create({
            text: 'Parent',
            id: 'live-edit-button-parent',
            iconCls: 'live-edit-icon-parent',
            handler: function (event) {
                event.stopPropagation();
                $liveedit.publish('/page/component/select-parent');
            }
        });

        var $insertButton = button.create({
            text: 'Insert',
            id: 'live-edit-button-insert',
            iconCls: 'live-edit-icon-insert',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        var $resetButton = button.create({
            text: 'Reset',
            id: 'live-edit-button-reset',
            iconCls: 'live-edit-icon-reset',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        var $emptyButton = button.create({
            text: 'Empty',
            id: 'live-edit-button-empty',
            iconCls: 'live-edit-icon-empty',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        var $viewButton = button.create({
            text: 'View',
            id: 'live-edit-button-view',
            iconCls: 'live-edit-icon-view',
            handler: function (event) {
                event.stopPropagation();
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
            AdminLiveEdit.ui.DragDrop.enable();
        });

        $dragButton.on('mousemove', function (event) {
            if (this._mouseDown) {
                this._mouseDown = false;
                fadeOutAndHide();
                var highlighter = AdminLiveEdit.ui.Highlighter;
                var $selectedComponent = highlighter.getSelected();
                var evt = document.createEvent('MouseEvents');
                evt.initMouseEvent('mousedown', true, true, window, 0, event.screenX, event.screenY, event.clientX, event.clientY, false,
                    false, false, false, 0, null);

                $selectedComponent[0].dispatchEvent(evt);
            }
        });

        $dragButton.on('mouseup', function (event) {
            this._mouseDown = false;
            AdminLiveEdit.ui.DragDrop.disable();
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
        $container.append($parentButton);
        $container.append($insertButton);
        $container.append($resetButton);
        $container.append($emptyButton);
        $container.append($viewButton);
        $container.append($dragButton);
        $container.append($settingsButton);
        $container.append($removeButton);
    }


    function updateParentButtonText() {
        var $parentComponentOfSelected = $liveedit(AdminLiveEdit.ui.Highlighter.getSelected().parents('[data-live-edit-type]')[0]);
        if ($parentComponentOfSelected.length === 1) {
            var parentComponentType = AdminLiveEdit.Util.getTypeFromComponent($parentComponentOfSelected);
            $liveedit('#live-edit-button-parent').find('.live-edit-button-text').html(parentComponentType);
        }
    }


    function getAllButtons() {
        return $liveedit('#live-edit-context-menu-inner').find('.live-edit-button');
    }


    function updateButtonsDisplay($component) {
        var componentType = AdminLiveEdit.Util.getTypeFromComponent($component);
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
        var componentType = util.getTypeFromComponent($component);
        var $menu = getMenu();
        $menu.show();

        updateButtonsDisplay($component);

        var componentBoxModel = util.getBoxModel($component);
        var menuTopPos = Math.round(componentBoxModel.top);
        var menuLeftPos = Math.round(componentBoxModel.left + componentBoxModel.width);
        var documentSize = util.getDocumentSize();
        if (menuLeftPos >= documentSize.width) {
            menuLeftPos = menuLeftPos - $menu.width();
        }

        $menu.css({
            top: menuTopPos,
            left: menuLeftPos
        });
    }


    function initSubscribers() {
        $liveedit.subscribe('/page/component/select', moveMenuTo);
        $liveedit.subscribe('/page/component/deselect', hide);
        $liveedit.subscribe('/page/component/sortstart', fadeOutAndHide);
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