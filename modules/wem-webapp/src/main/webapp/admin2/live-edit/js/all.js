var AdminLiveEdit = AdminLiveEdit || function () {
};
AdminLiveEdit.namespace = AdminLiveEdit.namespace || function () {
};
AdminLiveEdit.namespace.prototype.constructor = new AdminLiveEdit();
AdminLiveEdit.namespace.useNamespace = function (namespace, container) {
    if(namespace === undefined || namespace === '') {
        return;
    }
    var separator = '.';
    var ns = namespace.split(separator);
    var o = container || window;
    var i;
    var len;
    if(ns.length > 0) {
        o[ns[0]] = o[ns[0]] || function () {
        };
        if(o[ns[0]].prototype.constructor == undefined) {
            o[ns[0]].prototype.constructor = new o();
        }
    }
    var remainingNs = '';
    for(i = 1 , len = ns.length; i < len; i++) {
        if(i === 1) {
            remainingNs = ns[1];
        } else {
            remainingNs = remainingNs + separator + ns[i];
        }
    }
    return AdminLiveEdit.namespace.useNamespace(remainingNs, o[ns[0]]);
};
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.Util');
AdminLiveEdit.Util = ((function ($) {
    'use strict';
    return {
        getDocumentSize: function () {
            var $document = $(document);
            return {
                width: $document.width(),
                height: $document.height()
            };
        },
        getViewPortSize: function () {
            var $window = $(window);
            return {
                width: $window.width(),
                height: $window.height()
            };
        },
        getDocumentScrollTop: function () {
            return $(document).scrollTop();
        },
        getBoxModel: function ($element, contentOnly) {
            var $el = $($element);
            var offset = $el.offset();
            var top = offset.top;
            var left = offset.left;
            var width = $el.outerWidth();
            var height = $el.outerHeight();
            var mt = parseInt($el.css('marginTop'), 10);
            var mr = parseInt($el.css('marginRight'), 10);
            var mb = parseInt($el.css('marginBottom'), 10);
            var ml = parseInt($el.css('marginLeft'), 10);
            var bt = parseInt($el.css('borderTopWidth'), 10);
            var br = parseInt($el.css('borderRightWidth'), 10);
            var bb = parseInt($el.css('borderBottomWidth'), 10);
            var bl = parseInt($el.css('borderLeftWidth'), 10);
            var pt = parseInt($el.css('paddingTop'), 10);
            var pr = parseInt($el.css('paddingRight'), 10);
            var pb = parseInt($el.css('paddingBottom'), 10);
            var pl = parseInt($el.css('paddingLeft'), 10);
            if(contentOnly) {
                top = top + pt;
                left = left + pl;
                width = width - (pl + pr);
                height = height - (pt + pb);
            }
            return {
                top: top,
                left: left,
                width: width,
                height: height,
                borderTop: bt,
                borderRight: br,
                borderBottom: bb,
                borderLeft: bl,
                paddingTop: pt,
                paddingRight: pr,
                paddingBottom: pb,
                paddingLeft: pl
            };
        },
        getIconForComponent: function (componentType) {
            var icon = '';
            switch(componentType) {
                case 'region':
                    icon = '../../../admin2/live-edit/images/layout_vertical.png';
                    break;
                case 'part':
                    icon = '../../../admin2/live-edit/images/component_blue.png';
                    break;
                case 'content':
                    icon = '../../../admin2/live-edit/images/data_blue.png';
                    break;
                case 'paragraph':
                    icon = '../../../admin2/live-edit/images/text_rich_marked.png';
                    break;
                default:
                    icon = '../../../admin2/live-edit/images/component_blue.png';
            }
            return icon;
        },
        getPagePositionForComponent: function ($component) {
            return $($component).position();
        },
        getComponentInfo: function ($component) {
            var t = this;
            return {
                type: t.getComponentType($component),
                key: t.getComponentKey($component),
                name: t.getComponentName($component),
                tagName: t.getTagNameForComponent($component)
            };
        },
        getComponentType: function ($component) {
            return $component.data('live-edit-type');
        },
        getComponentKey: function ($component) {
            return $component.data('live-edit-key');
        },
        getComponentName: function ($component) {
            return $component.data('live-edit-name') || '[No Name]';
        },
        getTagNameForComponent: function ($component) {
            return $component[0].tagName.toLowerCase();
        },
        supportsTouch: function () {
            return document.hasOwnProperty('ontouchend');
        }
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.MutationObserver');
((function ($) {
    'use strict';
    var mutationObserver = AdminLiveEdit.MutationObserver = function () {
        this.mutationObserver = null;
        this.$observedComponent = null;
        this.registerGlobalListeners();
    };
    var proto = mutationObserver.prototype;
    proto.registerGlobalListeners = function () {
        var me = this;
        $(window).on('component.onParagraphEdit', $.proxy(me.observe, me));
        $(window).on('shader.onClick', $.proxy(me.disconnect, me));
    };
    proto.observe = function (event, $component) {
        var me = this;
        var isAlreadyObserved = me.$observedComponent && me.$observedComponent[0] === $component[0];
        if(isAlreadyObserved) {
            return;
        }
        me.disconnect(event);
        me.$observedComponent = $component;
        me.mutationObserver = new LiveEditMutationSummary({
            callback: function (summaries) {
                me.onMutate(summaries, event);
            },
            rootNode: $component[0],
            queries: [
                {
                    all: true
                }
            ]
        });
    };
    proto.onMutate = function (summaries, event) {
        if(summaries && summaries[0]) {
            var $targetComponent = $(summaries[0].target), targetComponentIsSelected = $targetComponent.hasClass('live-edit-selected-component'), componentIsNotSelectedAndMouseIsOver = !targetComponentIsSelected && event.type === 'component.mouseOver', componentIsParagraphAndBeingEdited = $targetComponent.attr('contenteditable');
            if(componentIsParagraphAndBeingEdited) {
                $(window).trigger('component.onParagraphEdit', [
                    $targetComponent
                ]);
            } else if(componentIsNotSelectedAndMouseIsOver) {
                $(window).trigger('component.mouseOver', [
                    $targetComponent
                ]);
            } else {
                $(window).trigger('component.onSelect', [
                    $targetComponent
                ]);
            }
        }
    };
    proto.disconnect = function (event) {
        var targetComponentIsSelected = (this.$observedComponent && this.$observedComponent.hasClass('live-edit-selected-component'));
        var componentIsSelectedAndUserMouseOut = event.type === 'component.mouseOut' && targetComponentIsSelected;
        if(componentIsSelectedAndUserMouseOut) {
            return;
        }
        this.$observedComponent = null;
        if(this.mutationObserver) {
            this.mutationObserver.disconnect();
            this.mutationObserver = null;
        }
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.DragDropSort');
AdminLiveEdit.DragDropSort = ((function ($) {
    'use strict';
    var util = AdminLiveEdit.Util;
    var isDragging = false;
    var cursorAt = AdminLiveEdit.Util.supportsTouch() ? {
        left: 15,
        top: 70
    } : {
        left: -10,
        top: -15
    };
    var regionSelector = '[data-live-edit-type=region]';
    var layoutSelector = '[data-live-edit-type=layout]';
    var partSelector = '[data-live-edit-type=part]';
    var paragraphSelector = '[data-live-edit-type=paragraph]';
    var itemsToSortSelector = layoutSelector + ',' + partSelector + ',' + paragraphSelector;
    function enableDragDrop() {
        $(regionSelector).sortable('enable');
    }
    function disableDragDrop() {
        $(regionSelector).sortable('disable');
    }
    function getDragHelperHtml(text) {
        return '<div id="live-edit-drag-helper" style="width: 150px; height: 16px;">' + '    <img id="live-edit-drag-helper-status-icon" src="../../../admin2/live-edit/images/drop-no.gif"/>' + '    <span id="live-edit-drag-helper-text" style="width: 134px;">' + text + '</span>' + '</div>';
    }
    function setDragHelperText(text) {
        $('#live-edit-drag-helper-text').text(text);
    }
    function createComponentBarDraggables() {
        var $componentBarComponents = $('.live-edit-component');
        var draggableOptions = {
            connectToSortable: regionSelector,
            addClasses: false,
            cursor: 'move',
            appendTo: 'body',
            zIndex: 5100000,
            revert: function (validDrop) {
            },
            cursorAt: cursorAt,
            helper: function () {
                return getDragHelperHtml('');
            },
            start: function (event, ui) {
                $(window).trigger('component.onDragStart', [
                    event, 
                    ui
                ]);
                setDragHelperText($(event.target).data('live-edit-component-name'));
                isDragging = true;
            },
            stop: function (event, ui) {
                $(window).trigger('component.onDragStop', [
                    event, 
                    ui
                ]);
                isDragging = false;
            }
        };
        $componentBarComponents.draggable(draggableOptions);
    }
    function createDragHelper(event, helper) {
        return $(getDragHelperHtml(util.getComponentName(helper)));
    }
    function refreshSortable() {
        $(regionSelector).sortable('refresh');
    }
    function updateHelperStatusIcon(status) {
        $('#live-edit-drag-helper-status-icon').attr('src', '../../../admin2/live-edit/images/drop-' + status + '.gif');
    }
    function targetIsPlaceholder($target) {
        return $target.hasClass('live-edit-drop-target-placeholder');
    }
    function handleSortStart(event, ui) {
        isDragging = true;
        var componentIsSelected = ui.item.hasClass('live-edit-selected-component');
        ui.item.data('live-edit-selected-on-sort-start', componentIsSelected);
        var targetComponentName = AdminLiveEdit.Util.getComponentName($(event.target));
        ui.placeholder.html('Drop component here' + '<div style="font-size: 10px;">' + targetComponentName + '</div>');
        refreshSortable();
        $(window).trigger('component.onSortStart', [
            event, 
            ui
        ]);
    }
    function handleDragOver(event, ui) {
        event.stopPropagation();
        var draggedItemIsLayoutComponent = ui.item.data('live-edit-component-type') === 'layout' || ui.item.data('live-edit-type') === 'layout', isDraggingOverLayoutComponent = ui.placeholder.closest(layoutSelector).length > 0;
        if(draggedItemIsLayoutComponent && isDraggingOverLayoutComponent) {
            updateHelperStatusIcon('no');
            ui.placeholder.hide();
        } else {
            updateHelperStatusIcon('yes');
            $(window).trigger('component.onSortOver', [
                event, 
                ui
            ]);
        }
    }
    function handleDragOut(event, ui) {
        if(targetIsPlaceholder($(event.srcElement))) {
            removePaddingFromLayoutComponent();
        }
        updateHelperStatusIcon('no');
        $(window).trigger('component.onSortOut', [
            event, 
            ui
        ]);
    }
    function handleSortChange(event, ui) {
        addPaddingToLayoutComponent($(event.target));
        updateHelperStatusIcon('yes');
        ui.placeholder.show();
        $(window).trigger('component.onSortChange', [
            event, 
            ui
        ]);
    }
    function handleSortUpdate(event, ui) {
        $(window).trigger('component.onSortUpdate', [
            event, 
            ui
        ]);
    }
    function handleSortStop(event, ui) {
        isDragging = false;
        removePaddingFromLayoutComponent();
        var draggedItemIsLayoutComponent = ui.item.data('live-edit-component-type') === 'layout' || ui.item.data('live-edit-type') === 'layout', targetIsInLayoutComponent = $(event.target).closest(layoutSelector).length > 0;
        if(draggedItemIsLayoutComponent && targetIsInLayoutComponent) {
            ui.item.remove();
        }
        if(AdminLiveEdit.Util.supportsTouch()) {
            $(window).trigger('component.mouseOut');
        }
        var wasSelectedOnDragStart = ui.item.data('live-edit-selected-on-drag-start');
        $(window).trigger('component.onSortStop', [
            event, 
            ui, 
            wasSelectedOnDragStart
        ]);
        ui.item.removeData('live-edit-selected-on-drag-start');
    }
    function itemIsDraggedFromComponentBar(item) {
        return item.hasClass('live-edit-component');
    }
    function handleReceive(event, ui) {
        if(itemIsDraggedFromComponentBar(ui.item)) {
            var $componentBarComponent = $(this).children('.live-edit-component');
            var componentKey = $componentBarComponent.data('live-edit-component-key');
            var componentType = $componentBarComponent.data('live-edit-component-type');
            var url = '../../../admin2/live-edit/data/mock-component-' + componentKey + '.html';
            $componentBarComponent.hide();
            $.ajax({
                url: url,
                cache: false
            }).done(function (html) {
                $componentBarComponent.replaceWith(html);
                if(componentType === 'layout') {
                    createSortable();
                }
                $(window).trigger('component.onSortUpdate');
            });
        }
    }
    function addPaddingToLayoutComponent($component) {
        $component.closest(layoutSelector).addClass('live-edit-component-padding');
    }
    function removePaddingFromLayoutComponent() {
        $('.live-edit-component-padding').removeClass('live-edit-component-padding');
    }
    function registerGlobalListeners() {
        $(window).on('componentBar.dataLoaded', function () {
            createComponentBarDraggables();
        });
        $(window).on('component.onSelect', function (event, $component) {
        });
        $(window).on('component.onDeselect', function () {
            if(AdminLiveEdit.Util.supportsTouch() && !isDragging) {
                disableDragDrop();
            }
        });
        $(window).on('component.onParagraphSelect', function () {
            $(regionSelector).sortable('option', 'cancel', '[data-live-edit-type=paragraph]');
        });
        $(window).on('component.onParagraphEditLeave', function () {
            $(regionSelector).sortable('option', 'cancel', '');
        });
    }
    function createSortable() {
        $(regionSelector).sortable({
            revert: false,
            connectWith: regionSelector,
            items: itemsToSortSelector,
            distance: 1,
            delay: 150,
            tolerance: 'pointer',
            cursor: 'move',
            cursorAt: cursorAt,
            scrollSensitivity: Math.round(AdminLiveEdit.Util.getViewPortSize().height / 8),
            placeholder: 'live-edit-drop-target-placeholder',
            helper: createDragHelper,
            zIndex: 1001000,
            start: handleSortStart,
            over: handleDragOver,
            out: handleDragOut,
            change: handleSortChange,
            receive: handleReceive,
            update: handleSortUpdate,
            stop: handleSortStop
        });
    }
    function init() {
        createSortable();
        registerGlobalListeners();
    }
    return {
        initialize: init,
        enable: enableDragDrop,
        disable: disableDragDrop,
        isDragging: function () {
            return isDragging;
        }
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.model.component');
((function ($) {
    'use strict';
    AdminLiveEdit.model.component.Base = function () {
        this.cssSelector = '';
    };
    AdminLiveEdit.model.component.Base.prototype = {
        attachMouseOverEvent: function () {
            var me = this;
            $(document).on('mouseover', me.cssSelector, function (event) {
                var $component = $(this);
                var targetIsUiComponent = me.isLiveEditUiComponent($(event.target));
                var cancelEvents = targetIsUiComponent || me.hasComponentSelected() || AdminLiveEdit.DragDropSort.isDragging();
                if(cancelEvents) {
                    return;
                }
                event.stopPropagation();
                $(window).trigger('component.mouseOver', [
                    $component
                ]);
            });
        },
        attachMouseOutEvent: function () {
            var me = this;
            $(document).on('mouseout', function () {
                if(me.hasComponentSelected()) {
                    return;
                }
                $(window).trigger('component.mouseOut');
            });
        },
        attachClickEvent: function () {
            var me = this;
            $(document).on('click contextmenu touchstart', me.cssSelector, function (event) {
                if(me.isLiveEditUiComponent($(event.target))) {
                    return;
                }
                event.stopPropagation();
                event.preventDefault();
                var $component = $(event.currentTarget), componentIsSelected = $component.hasClass('live-edit-selected-component'), pageHasComponentSelected = $('.live-edit-selected-component').length > 0;
                if(componentIsSelected || pageHasComponentSelected) {
                    $(window).trigger('component.onDeselect');
                } else {
                    var pagePosition = {
                        x: event.pageX,
                        y: event.pageY
                    };
                    $(window).trigger('component.onSelect', [
                        $component, 
                        pagePosition
                    ]);
                }
            });
        },
        hasComponentSelected: function () {
            return $('.live-edit-selected-component').length > 0;
        },
        isLiveEditUiComponent: function ($target) {
            return $target.is('[id*=live-edit-ui-cmp]') || $target.parents('[id*=live-edit-ui-cmp]').length > 0;
        },
        getAll: function () {
            return $(this.cssSelector);
        }
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.model.component');
((function ($) {
    'use strict';
    var page = AdminLiveEdit.model.component.Page = function () {
        this.cssSelector = '[data-live-edit-type=page]';
        this.attachClickEvent();
    };
    page.prototype = new AdminLiveEdit.model.component.Base();
    var proto = page.prototype;
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.model.component');
((function ($) {
    'use strict';
    var regions = AdminLiveEdit.model.component.Region = function () {
        this.cssSelector = '[data-live-edit-type=region]';
        this.renderEmptyPlaceholders();
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
        this.registerGlobalListeners();
    };
    regions.prototype = new AdminLiveEdit.model.component.Base();
    var proto = regions.prototype;
    var util = AdminLiveEdit.Util;
    proto.registerGlobalListeners = function () {
        $(window).on('component.onSortUpdate', $.proxy(this.renderEmptyPlaceholders, this));
        $(window).on('component.onSortOver', $.proxy(this.renderEmptyPlaceholders, this));
        $(window).on('component.onRemove', $.proxy(this.renderEmptyPlaceholders, this));
    };
    proto.renderEmptyPlaceholders = function () {
        var me = this;
        me.removeAllRegionPlaceholders();
        var $regions = me.getAll();
        $regions.each(function (index) {
            var $region = $(this);
            var regionIsEmpty = me.isRegionEmpty.call(me, $region);
            if(regionIsEmpty) {
                me.appendEmptyPlaceholder.call(me, $region);
            }
        });
    };
    proto.appendEmptyPlaceholder = function ($region) {
        var html = '<div>Drag components here</div>';
        html += '<div style="font-size: 10px;">' + util.getComponentName($region) + '</div>';
        var $placeholder = $('<div/>', {
            'class': 'live-edit-empty-region-placeholder',
            'html': html
        });
        $region.append($placeholder);
    };
    proto.isRegionEmpty = function ($region) {
        var hasNotParts = $region.children('[data-live-edit-type]' + ':not(:hidden)').length === 0;
        var hasNotDropTargetPlaceholder = $region.children('.live-edit-drop-target-placeholder').length === 0;
        return hasNotParts && hasNotDropTargetPlaceholder;
    };
    proto.removeAllRegionPlaceholders = function () {
        $('.live-edit-empty-region-placeholder').remove();
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.model.component');
((function ($) {
    'use strict';
    var layout = AdminLiveEdit.model.component.Layout = function () {
        this.cssSelector = '[data-live-edit-type=layout]';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    layout.prototype = new AdminLiveEdit.model.component.Base();
    var proto = layout.prototype;
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.model.component');
((function ($) {
    'use strict';
    var parts = AdminLiveEdit.model.component.Part = function () {
        this.cssSelector = '[data-live-edit-type=part]';
        this.renderEmptyPlaceholders();
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    parts.prototype = new AdminLiveEdit.model.component.Base();
    var proto = parts.prototype;
    proto.appendEmptyPlaceholder = function ($part) {
        var $placeholder = $('<div/>', {
            'class': 'live-edit-empty-part-placeholder',
            'html': 'Empty Part'
        });
        $part.append($placeholder);
    };
    proto.isPartEmpty = function ($part) {
        return $($part).children().length === 0;
    };
    proto.renderEmptyPlaceholders = function () {
        var t = this;
        this.getAll().each(function (index) {
            var $part = $(this);
            var partIsEmpty = t.isPartEmpty($part);
            if(partIsEmpty) {
                t.appendEmptyPlaceholder($part);
            }
        });
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.model.component');
((function ($) {
    'use strict';
    var contents = AdminLiveEdit.model.component.Content = function () {
        this.cssSelector = '[data-live-edit-type=content]';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    contents.prototype = new AdminLiveEdit.model.component.Base();
    var proto = contents.prototype;
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.model.component');
((function ($) {
    'use strict';
    var paragraphs = AdminLiveEdit.model.component.Paragraph = function () {
        var me = this;
        this.cssSelector = '[data-live-edit-type=paragraph]';
        this.$selectedParagraph = null;
        this.modes = {
            UNSELECTED: 0,
            SELECTED: 1,
            EDIT: 2
        };
        this.currentMode = me.modes.UNSELECTED;
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
        this.registerGlobalListeners();
    };
    paragraphs.prototype = new AdminLiveEdit.model.component.Base();
    var proto = paragraphs.prototype;
    proto.registerGlobalListeners = function () {
        $(window).on('shader.onClick', $.proxy(this.leaveEditMode, this));
        $(window).on('component.onDeselect', $.proxy(this.leaveEditMode, this));
    };
    proto.attachClickEvent = function () {
        var me = this;
        $(document).on('click contextmenu touchstart', me.cssSelector, function (event) {
            me.handleClick(event);
        });
    };
    proto.handleClick = function (event) {
        var me = this;
        event.stopPropagation();
        event.preventDefault();
        if(me.$selectedParagraph && !(me.currentMode === me.modes.EDIT)) {
            me.$selectedParagraph.css('cursor', '');
        }
        var $paragraph = $(event.currentTarget);
        if(!$paragraph.is(me.$selectedParagraph)) {
            me.currentMode = me.modes.UNSELECTED;
        }
        me.$selectedParagraph = $paragraph;
        if(me.currentMode === me.modes.UNSELECTED) {
            me.setSelectMode(event);
        } else if(me.currentMode === me.modes.SELECTED) {
            me.setEditMode(event);
        } else {
        }
    };
    proto.setSelectMode = function (event) {
        var me = this;
        me.$selectedParagraph.css('cursor', 'url(../../../admin2/live-edit/images/pencil.png) 0 40, text');
        me.currentMode = me.modes.SELECTED;
        if(window.getSelection) {
            window.getSelection().removeAllRanges();
        }
        var pagePosition = {
            x: event.pageX,
            y: event.pageY
        };
        $(window).trigger('component.onSelect', [
            me.$selectedParagraph, 
            pagePosition
        ]);
        $(window).trigger('component.onParagraphSelect', [
            me.$selectedParagraph
        ]);
    };
    proto.setEditMode = function (event) {
        var me = this, $paragraph = me.$selectedParagraph;
        $(window).trigger('component.onParagraphEdit', [
            me.$selectedParagraph
        ]);
        $paragraph.css('cursor', 'text');
        $paragraph.addClass('live-edit-edited-paragraph');
        me.currentMode = me.modes.EDIT;
    };
    proto.leaveEditMode = function (event) {
        var me = this, $paragraph = me.$selectedParagraph;
        if($paragraph === null) {
            return;
        }
        $(window).trigger('component.onParagraphEditLeave', [
            me.$selectedParagraph
        ]);
        $paragraph.css('cursor', '');
        $paragraph.removeClass('live-edit-edited-paragraph');
        me.$selectedParagraph = null;
        me.currentMode = me.modes.UNSELECTED;
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view');
((function ($) {
    'use strict';
    AdminLiveEdit.view.Base = function () {
        this.$element = $([]);
    };
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
            if($parent.length > 0 && this.$element.length > 0) {
                $parent.append(this.$element);
            }
        },
        getEl: function () {
            return this.$element;
        }
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view');
((function ($) {
    'use strict';
    var htmlElementReplacer = AdminLiveEdit.view.HtmlElementReplacer = function () {
        this.elements = [
            'iframe', 
            'object'
        ];
        this.replaceElementsWithPlaceholders();
    };
    var proto = htmlElementReplacer.prototype;
    proto.registerGlobalListeners = function () {
    };
    proto.replaceElementsWithPlaceholders = function () {
        var me = this;
        me.getElements().each(function () {
            me.replace($(this));
        });
    };
    proto.replace = function ($element) {
        this.hideElement($element);
        this.addPlaceholder($element);
    };
    proto.addPlaceholder = function ($element) {
        this.createPlaceholder($element).insertAfter($element);
    };
    proto.createPlaceholder = function ($element) {
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
    };
    proto.getElements = function () {
        return $('[data-live-edit-type=part] > ' + this.elements.toString());
    };
    proto.getElementWidth = function ($element) {
        var attrWidth = $element.attr('width');
        if(!attrWidth) {
            return $element.width() - 2;
        }
        return attrWidth;
    };
    proto.getElementHeight = function ($element) {
        var attrHeight = $element.attr('height');
        if(!attrHeight) {
            return $element.height() - 2;
        }
        return attrHeight;
    };
    proto.showElement = function ($element) {
        $element.show();
    };
    proto.hideElement = function ($element) {
        $element.hide();
    };
    proto.resolveIconCssClass = function ($element) {
        var tagName = $element[0].tagName.toLowerCase();
        var clsName = '';
        if(tagName === 'iframe') {
            clsName = 'live-edit-iframe';
        } else {
            clsName = 'live-edit-object';
        }
        return clsName;
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.htmleditor');
((function ($) {
    'use strict';
    var editor = AdminLiveEdit.view.htmleditor.Editor = function () {
        this.toolbar = new AdminLiveEdit.view.htmleditor.Toolbar();
        this.registerGlobalListeners();
    };
    var proto = editor.prototype;
    proto.registerGlobalListeners = function () {
        var me = this;
        $(window).on('component.onParagraphEdit', function (event, $paragraph) {
            me.activate($paragraph);
        });
        $(window).on('component.onParagraphEditLeave', function (event, $paragraph) {
            me.deActivate($paragraph);
        });
        $(window).on('editorToolbar.onButtonClick', function (event, tag) {
            document.execCommand(tag, false, null);
        });
    };
    proto.activate = function ($paragraph) {
        $paragraph.get(0).contentEditable = true;
        $paragraph.get(0).focus();
    };
    proto.deActivate = function ($paragraph) {
        $paragraph.get(0).contentEditable = false;
        $paragraph.get(0).blur();
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.htmleditor');
((function ($) {
    'use strict';
    var toolbar = AdminLiveEdit.view.htmleditor.Toolbar = function () {
        var me = this;
        me.$selectedComponent = null;
        me.addView();
        me.addEvents();
        me.registerGlobalListeners();
    };
    toolbar.prototype = new AdminLiveEdit.view.Base();
    var proto = toolbar.prototype;
    var util = AdminLiveEdit.Util;
    proto.registerGlobalListeners = function () {
        $(window).on('component.onParagraphEdit', $.proxy(this.show, this));
        $(window).on('component.onParagraphEditLeave', $.proxy(this.hide, this));
        $(window).on('component.onRemove', $.proxy(this.hide, this));
        $(window).on('component.onSortStart', $.proxy(this.hide, this));
    };
    proto.addView = function () {
        var me = this;
        var html = '<div class="live-edit-editor-toolbar live-edit-arrow-bottom" style="display: none">' + '    <button data-tag="paste" class="live-edit-editor-button"></button>' + '    <button data-tag="insertUnorderedList" class="live-edit-editor-button"></button>' + '    <button data-tag="insertOrderedList" class="live-edit-editor-button"></button>' + '    <button data-tag="link" class="live-edit-editor-button"></button>' + '    <button data-tag="cut" class="live-edit-editor-button"></button>' + '    <button data-tag="strikeThrough" class="live-edit-editor-button"></button>' + '    <button data-tag="bold" class="live-edit-editor-button"></button>' + '    <button data-tag="underline" class="live-edit-editor-button"></button>' + '    <button data-tag="italic" class="live-edit-editor-button"></button>' + '    <button data-tag="superscript" class="live-edit-editor-button"></button>' + '    <button data-tag="subscript" class="live-edit-editor-button"></button>' + '    <button data-tag="justifyLeft" class="live-edit-editor-button"></button>' + '    <button data-tag="justifyCenter" class="live-edit-editor-button"></button>' + '    <button data-tag="justifyRight" class="live-edit-editor-button"></button>' + '    <button data-tag="justifyFull" class="live-edit-editor-button"></button>' + '</div>';
        me.createElement(html);
        me.appendTo($('body'));
    };
    proto.addEvents = function () {
        var me = this;
        me.getEl().on('click', function (event) {
            event.stopPropagation();
            var tag = event.target.getAttribute('data-tag');
            if(tag) {
                $(window).trigger('editorToolbar.onButtonClick', [
                    tag
                ]);
            }
        });
        $(window).scroll(function () {
            if(me.$selectedComponent) {
                me.updatePosition();
            }
        });
    };
    proto.show = function (event, $component) {
        var me = this;
        me.$selectedComponent = $component;
        me.getEl().show();
        me.toggleArrowPosition(false);
        me.updatePosition();
    };
    proto.hide = function () {
        var me = this;
        me.$selectedComponent = null;
        me.getEl().hide();
    };
    proto.updatePosition = function () {
        var me = this;
        if(!me.$selectedComponent) {
            return;
        }
        var defaultPosition = me.getDefaultPosition();
        var stick = $(window).scrollTop() >= me.$selectedComponent.offset().top - 60;
        if(stick) {
            me.getEl().css({
                position: 'fixed',
                top: 10,
                left: defaultPosition.left
            });
        } else {
            me.getEl().css({
                position: 'absolute',
                top: defaultPosition.top,
                left: defaultPosition.left
            });
        }
        var placeArrowOnTop = $(window).scrollTop() >= defaultPosition.bottom - 10;
        me.toggleArrowPosition(placeArrowOnTop);
    };
    proto.toggleArrowPosition = function (showArrowAtTop) {
        var me = this;
        if(showArrowAtTop) {
            me.getEl().removeClass('live-edit-arrow-bottom').addClass('live-edit-arrow-top');
        } else {
            me.getEl().removeClass('live-edit-arrow-top').addClass('live-edit-arrow-bottom');
        }
    };
    proto.getDefaultPosition = function () {
        var me = this;
        var componentBox = util.getBoxModel(me.$selectedComponent), leftPos = componentBox.left + (componentBox.width / 2 - me.getEl().outerWidth() / 2), topPos = componentBox.top - me.getEl().height() - 25;
        return {
            left: leftPos,
            top: topPos,
            bottom: componentBox.top + componentBox.height
        };
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view');
((function ($) {
    var shader = AdminLiveEdit.view.Shader = function () {
        this.$selectedComponent = null;
        this.addView();
        this.addEvents();
        this.registerGlobalListeners();
    };
    shader.prototype = new AdminLiveEdit.view.Base();
    var proto = shader.prototype;
    var util = AdminLiveEdit.Util;
    proto.registerGlobalListeners = function () {
        $(window).on('component.onSelect', $.proxy(this.show, this));
        $(window).on('component.onDeselect', $.proxy(this.hide, this));
        $(window).on('component.onRemove', $.proxy(this.hide, this));
        $(window).on('component.onSortStart', $.proxy(this.hide, this));
        $(window).on('component.onParagraphEdit', $.proxy(this.show, this));
        $(window).on('liveEdit.onWindowResize', $.proxy(this.handleWindowResize, this));
    };
    proto.addView = function () {
        var $body = $('body');
        this.$pageShader = $body.append('<div class="live-edit-shader" id="live-edit-page-shader"/>');
        this.$northShader = $('<div id="live-edit-shader-north" class="live-edit-shader"/>');
        $body.append(this.$northShader);
        this.$eastShader = $('<div id="live-edit-shader-east" class="live-edit-shader"/>');
        $body.append(this.$eastShader);
        this.$southShader = $('<div id="live-edit-shader-south" class="live-edit-shader"/>');
        $body.append(this.$southShader);
        this.$westShader = $('<div id="live-edit-shader-west" class="live-edit-shader"/>');
        $body.append(this.$westShader);
    };
    proto.addEvents = function () {
        $('.live-edit-shader').on('click contextmenu', function (event) {
            event.stopPropagation();
            event.preventDefault();
            $(window).trigger('component.onDeselect');
            $(window).trigger('shader.onClick');
        });
    };
    proto.show = function (event, $component) {
        var me = this;
        me.$selectedComponent = $component;
        if(util.getComponentType($component) === 'page') {
            me.showForPage($component);
        } else {
            me.showForComponent($component);
        }
    };
    proto.showForPage = function ($component) {
        this.hide();
        $('#live-edit-page-shader').css({
            top: 0,
            right: 0,
            bottom: 0,
            left: 0
        }).show();
    };
    proto.showForComponent = function ($component) {
        var me = this;
        $('.live-edit-shader').addClass('live-edit-animatable');
        var documentSize = util.getDocumentSize(), docWidth = documentSize.width, docHeight = documentSize.height;
        var boxModel = util.getBoxModel($component), x = boxModel.left, y = boxModel.top, w = boxModel.width, h = boxModel.height;
        me.$northShader.css({
            top: 0,
            left: 0,
            width: docWidth,
            height: y
        }).show();
        me.$eastShader.css({
            top: y,
            left: x + w,
            width: docWidth - (x + w),
            height: h
        }).show();
        me.$southShader.css({
            top: y + h,
            left: 0,
            width: docWidth,
            height: docHeight - (y + h)
        }).show();
        me.$westShader.css({
            top: y,
            left: 0,
            width: x,
            height: h
        }).show();
    };
    proto.hide = function () {
        this.$selectedComponent = null;
        var $shaders = $('.live-edit-shader');
        $shaders.removeClass('live-edit-animatable');
        $shaders.hide();
    };
    proto.handleWindowResize = function (event) {
        if(this.$selectedComponent) {
            this.show(event, this.$selectedComponent);
        }
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view');
((function ($) {
    'use strict';
    var cursor = AdminLiveEdit.view.Cursor = function () {
        this.registerGlobalListeners();
    };
    var proto = cursor.prototype;
    proto.registerGlobalListeners = function () {
        $(window).on('component.mouseOver', $.proxy(this.updateCursor, this));
        $(window).on('component.mouseOut', $.proxy(this.resetCursor, this));
        $(window).on('component.onSelect', $.proxy(this.updateCursor, this));
    };
    proto.updateCursor = function (event, $component) {
        var componentType = AdminLiveEdit.Util.getComponentType($component);
        var $body = $('body');
        var cursor = 'default';
        switch(componentType) {
            case 'region':
                cursor = 'pointer';
                break;
            case 'part':
                cursor = 'move';
                break;
            case 'layout':
                cursor = 'move';
                break;
            case 'paragraph':
                cursor = 'move';
                break;
            default:
                cursor = 'default';
        }
        $body.css('cursor', cursor);
    };
    proto.resetCursor = function () {
        $('body').css('cursor', 'default');
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view');
((function ($) {
    'use strict';
    var highlighter = AdminLiveEdit.view.Highlighter = function () {
        this.$selectedComponent = null;
        this.addView();
        this.registerGlobalListeners();
    };
    highlighter.prototype = new AdminLiveEdit.view.Base();
    var proto = highlighter.prototype;
    var util = AdminLiveEdit.Util;
    proto.registerGlobalListeners = function () {
        $(window).on('component.mouseOver', $.proxy(this.componentMouseOver, this));
        $(window).on('component.mouseOut', $.proxy(this.hide, this));
        $(window).on('component.onSelect', $.proxy(this.selectComponent, this));
        $(window).on('component.onDeselect', $.proxy(this.deselect, this));
        $(window).on('component.onSortStart', $.proxy(this.hide, this));
        $(window).on('component.onRemove', $.proxy(this.hide, this));
        $(window).on('component.onParagraphEdit', $.proxy(this.hide, this));
        $(window).on('liveEdit.onWindowResize', $.proxy(this.handleWindowResize, this));
        $(window).on('component.onSortStop', function (event, uiEvent, ui, wasSelectedOnDragStart) {
            if(wasSelectedOnDragStart) {
                $(window).trigger('component.onSelect', [
                    ui.item
                ]);
            }
        });
    };
    proto.addView = function () {
        var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlight-border" style="top:-5000px;left:-5000px">' + '    <rect width="150" height="150"/>' + '</svg>';
        this.createElement(html);
        this.appendTo($('body'));
    };
    proto.componentMouseOver = function (event, $component) {
        var me = this;
        me.show();
        me.paintBorder($component);
    };
    proto.selectComponent = function (event, $component) {
        var me = this;
        me.$selectedComponent = $component;
        var componentType = util.getComponentType($component);
        $('.live-edit-selected-component').removeClass('live-edit-selected-component');
        $component.addClass('live-edit-selected-component');
        me.getEl().attr('class', me.getEl().attr('class') + ' live-edit-animatable');
        if(componentType === 'page') {
            me.hide();
            return;
        }
        me.paintBorder($component);
        me.show();
    };
    proto.deselect = function () {
        var me = this;
        me.getEl().attr('class', me.getEl().attr('class').replace(/ live-edit-animatable/g, ''));
        $('.live-edit-selected-component').removeClass('live-edit-selected-component');
        me.$selectedComponent = null;
    };
    proto.paintBorder = function ($component) {
        var me = this, $border = me.getEl();
        me.resizeBorderToComponent($component);
        var style = me.getStyleForComponent($component);
        $border.css('stroke', style.strokeColor);
        $border.css('fill', style.fillColor);
        $border.css('stroke-dasharray', style.strokeDashArray);
    };
    proto.resizeBorderToComponent = function ($component) {
        var me = this;
        var componentType = util.getComponentType($component);
        var componentTagName = util.getTagNameForComponent($component);
        var componentBoxModel = util.getBoxModel($component);
        var w = Math.round(componentBoxModel.width);
        var h = Math.round(componentBoxModel.height);
        var top = Math.round(componentBoxModel.top);
        var left = Math.round(componentBoxModel.left);
        var $highlighter = me.getEl();
        var $HighlighterRect = $highlighter.find('rect');
        $highlighter.width(w);
        $highlighter.height(h);
        $HighlighterRect[0].setAttribute('width', w);
        $HighlighterRect[0].setAttribute('height', h);
        $highlighter.css({
            top: top,
            left: left
        });
    };
    proto.show = function () {
        this.getEl().show();
    };
    proto.hide = function () {
        this.getEl().hide();
        var $el = this.getEl();
        $el.attr('class', $el.attr('class').replace(/ live-edit-animatable/g, ''));
    };
    proto.getStyleForComponent = function ($component) {
        var componentType = util.getComponentType($component);
        var strokeColor, strokeDashArray, fillColor;
        switch(componentType) {
            case 'region':
                strokeColor = 'rgba(20,20,20,1)';
                strokeDashArray = '';
                fillColor = 'rgba(255,255,255,0)';
                break;
            case 'layout':
                strokeColor = 'rgba(255,165,0,1)';
                strokeDashArray = '5 5';
                fillColor = 'rgba(100,12,36,0)';
                break;
            case 'part':
                strokeColor = 'rgba(68,68,68,1)';
                strokeDashArray = '5 5';
                fillColor = 'rgba(255,255,255,0)';
                break;
            case 'paragraph':
                strokeColor = 'rgba(85,85,255,1)';
                strokeDashArray = '5 5';
                fillColor = 'rgba(255,255,255,0)';
                break;
            case 'content':
                strokeColor = '';
                strokeDashArray = '';
                fillColor = 'rgba(0,108,255,.25)';
                break;
            default:
                strokeColor = 'rgba(20,20,20,1)';
                strokeDashArray = '';
                fillColor = 'rgba(255,255,255,0)';
        }
        return {
            strokeColor: strokeColor,
            strokeDashArray: strokeDashArray,
            fillColor: fillColor
        };
    };
    proto.handleWindowResize = function (event) {
        if(this.$selectedComponent) {
            this.paintBorder(this.$selectedComponent);
        }
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view');
((function ($) {
    'use strict';
    var toolTip = AdminLiveEdit.view.ToolTip = function () {
        this.OFFSET_X = 0;
        this.OFFSET_Y = 18;
        this.addView();
        this.attachEventListeners();
        this.registerGlobalListeners();
    };
    toolTip.prototype = new AdminLiveEdit.view.Base();
    var proto = toolTip.prototype;
    var util = AdminLiveEdit.Util;
    proto.registerGlobalListeners = function () {
        $(window).on('component.onSelect', $.proxy(this.hide, this));
    };
    proto.addView = function () {
        var me = this;
        var html = '<div class="live-edit-tool-tip" style="top:-5000px; left:-5000px;">' + '    <span class="live-edit-tool-tip-name-text"></span>' + '    <span class="live-edit-tool-tip-type-text"></span> ' + '</div>';
        me.createElement(html);
        me.appendTo($('body'));
    };
    proto.setText = function (componentType, componentName) {
        var $tooltip = this.getEl();
        $tooltip.children('.live-edit-tool-tip-type-text').text(componentType);
        $tooltip.children('.live-edit-tool-tip-name-text').text(componentName);
    };
    proto.attachEventListeners = function () {
        var me = this;
        $(document).on('mousemove', '[data-live-edit-type]', function (event) {
            var targetIsUiComponent = $(event.target).is('[id*=live-edit-ui-cmp]') || $(event.target).parents('[id*=live-edit-ui-cmp]').length > 0;
            var pageHasComponentSelected = $('.live-edit-selected-component').length > 0;
            if(targetIsUiComponent || pageHasComponentSelected || AdminLiveEdit.DragDropSort.isDragging()) {
                me.hide();
                return;
            }
            var $component = $(event.target).closest('[data-live-edit-type]');
            var componentInfo = util.getComponentInfo($component);
            var pos = me.getPosition(event);
            me.getEl().css({
                top: pos.y,
                left: pos.x
            });
            me.setText(componentInfo.type, componentInfo.name);
        });
        $(document).on('hover', '[data-live-edit-type]', function (event) {
            if(event.type === 'mouseenter') {
                me.getEl().hide().fadeIn(300);
            }
        });
        $(document).on('mouseout', function () {
            me.hide.call(me);
        });
    };
    proto.getPosition = function (event) {
        var t = this;
        var pageX = event.pageX;
        var pageY = event.pageY;
        var x = pageX + t.OFFSET_X;
        var y = pageY + t.OFFSET_Y;
        var viewPortSize = util.getViewPortSize();
        var scrollTop = util.getDocumentScrollTop();
        var toolTipWidth = t.getEl().width();
        var toolTipHeight = t.getEl().height();
        if(x + toolTipWidth > (viewPortSize.width - t.OFFSET_X * 2) - 50) {
            x = pageX - toolTipWidth - (t.OFFSET_X * 2);
        }
        if(y + toolTipHeight > (viewPortSize.height + scrollTop - t.OFFSET_Y * 2)) {
            y = pageY - toolTipHeight - (t.OFFSET_Y * 2);
        }
        return {
            x: x,
            y: y
        };
    };
    proto.hide = function () {
        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function ($) {
    'use strict';
    var menu = AdminLiveEdit.view.menu.Menu = function () {
        var me = this;
        me.$selectedComponent = null;
        me.previousPageSizes = null;
        me.previousPagePositions = null;
        me.hidden = true;
        me.buttons = [];
        me.buttonConfig = {
            'page': [
                'settings', 
                'reset'
            ],
            'region': [
                'parent', 
                'settings', 
                'reset', 
                'clear'
            ],
            'layout': [
                'parent', 
                'settings', 
                'remove'
            ],
            'part': [
                'parent', 
                'settings', 
                'details', 
                'remove'
            ],
            'content': [
                'parent', 
                'opencontent', 
                'view'
            ],
            'paragraph': [
                'parent', 
                'edit', 
                'remove'
            ]
        };
        me.addView();
        me.registerEvents();
        me.registerGlobalListeners();
    };
    menu.prototype = new AdminLiveEdit.view.Base();
    var proto = menu.prototype;
    var util = AdminLiveEdit.Util;
    var html = '';
    html += '<div class="live-edit-component-menu live-edit-arrow-top" style="display: none">';
    html += '   <div class="live-edit-component-menu-title-bar">';
    html += '       <div class="live-edit-component-menu-title-icon"><div><!-- --></div></div>';
    html += '       <div class="live-edit-component-menu-title-text"><!-- populated --></div>';
    html += '       <div class="live-edit-component-menu-title-close-button"><!-- --></div>';
    html += '   </div>';
    html += '   <div class="live-edit-component-menu-items">';
    html += '   </div>';
    html += '</div>';
    proto.registerGlobalListeners = function () {
        $(window).on('component.onSelect', $.proxy(this.show, this));
        $(window).on('component.onDeselect', $.proxy(this.hide, this));
        $(window).on('component.onSortStart', $.proxy(this.fadeOutAndHide, this));
        $(window).on('component.onRemove', $.proxy(this.hide, this));
        $(window).on('component.onParagraphEdit', $.proxy(this.hide, this));
    };
    proto.addView = function () {
        var me = this;
        me.createElement(html);
        me.appendTo($('body'));
        me.addButtons();
    };
    proto.registerEvents = function () {
        var me = this;
        me.getEl().draggable({
            handle: '.live-edit-component-menu-title-bar',
            addClasses: false
        });
        me.getCloseButton().click(function () {
            $(window).trigger('component.onDeselect');
        });
    };
    proto.show = function (event, $component, pagePosition) {
        var me = this, componentInfo = util.getComponentInfo($component);
        me.$selectedComponent = $component;
        me.previousPagePositions = pagePosition;
        me.previousPageSizes = util.getViewPortSize();
        me.updateTitleBar($component);
        me.updateMenuItemsForComponent($component);
        var pageXPosition = pagePosition.x - me.getEl().width() / 2, pageYPosition = pagePosition.y + 15;
        me.moveToXY(pageXPosition, pageYPosition);
        me.getEl().show();
        this.hidden = false;
    };
    proto.hide = function () {
        this.$selectedComponent = null;
        this.getEl().hide();
        this.hidden = true;
    };
    proto.fadeOutAndHide = function () {
        var me = this;
        me.getEl().fadeOut(500, function () {
            me.hide();
            $(window).trigger('component.onDeselect', {
                showComponentBar: false
            });
        });
        me.$selectedComponent = null;
    };
    proto.moveToXY = function (x, y) {
        this.getEl().css({
            left: x,
            top: y
        });
    };
    proto.addButtons = function () {
        var me = this;
        var parentButton = new AdminLiveEdit.view.menu.ParentButton(me);
        var settingsButton = new AdminLiveEdit.view.menu.SettingsButton(me);
        var detailsButton = new AdminLiveEdit.view.menu.DetailsButton(me);
        var insertButton = new AdminLiveEdit.view.menu.InsertButton(me);
        var resetButton = new AdminLiveEdit.view.menu.ResetButton(me);
        var clearButton = new AdminLiveEdit.view.menu.ClearButton(me);
        var openContentButton = new AdminLiveEdit.view.menu.OpenContentButton(me);
        var viewButton = new AdminLiveEdit.view.menu.ViewButton(me);
        var editButton = new AdminLiveEdit.view.menu.EditButton(me);
        var removeButton = new AdminLiveEdit.view.menu.RemoveButton(me);
        var i, $menuItemsPlaceholder = me.getMenuItemsPlaceholderElement();
        for(i = 0; i < me.buttons.length; i++) {
            me.buttons[i].appendTo($menuItemsPlaceholder);
        }
    };
    proto.updateMenuItemsForComponent = function ($component) {
        var componentType = util.getComponentType($component);
        if(this.buttonConfig.hasOwnProperty(componentType)) {
            var buttonArray = this.buttonConfig[componentType];
            var buttons = this.getButtons();
            var i;
            for(i = 0; i < buttons.length; i++) {
                var $button = buttons[i].getEl();
                var id = $button.attr('data-live-edit-ui-cmp-id');
                var subStr = id.substring(id.lastIndexOf('-') + 1, id.length);
                if(buttonArray.indexOf(subStr) > -1) {
                    $button.show();
                } else {
                    $button.hide();
                }
            }
        }
    };
    proto.updateTitleBar = function ($component) {
        var componentInfo = util.getComponentInfo($component);
        this.setIcon(componentInfo.type);
        this.setTitle(componentInfo.name);
    };
    proto.setTitle = function (titleText) {
        this.getTitleElement().text(titleText);
    };
    proto.setIcon = function (componentType) {
        var $iconCt = this.getIconElement(), iconCls = this.resolveCssClassForComponentType(componentType);
        $iconCt.children('div').attr('class', iconCls);
        $iconCt.attr('title', componentType);
    };
    proto.resolveCssClassForComponentType = function (componentType) {
        var iconCls;
        switch(componentType) {
            case 'page':
                iconCls = 'live-edit-component-menu-page-icon';
                break;
            case 'region':
                iconCls = 'live-edit-component-menu-region-icon';
                break;
            case 'layout':
                iconCls = 'live-edit-component-menu-layout-icon';
                break;
            case 'part':
                iconCls = 'live-edit-component-menu-part-icon';
                break;
            case 'content':
                iconCls = 'live-edit-component-menu-content-icon';
                break;
            case 'paragraph':
                iconCls = 'live-edit-component-menu-paragraph-icon';
                break;
            default:
                iconCls = '';
        }
        return iconCls;
    };
    proto.getButtons = function () {
        return this.buttons;
    };
    proto.getIconElement = function () {
        return $('.live-edit-component-menu-title-icon', this.getEl());
    };
    proto.getTitleElement = function () {
        return $('.live-edit-component-menu-title-text', this.getEl());
    };
    proto.getCloseButton = function () {
        return $('.live-edit-component-menu-title-close-button', this.getEl());
    };
    proto.getMenuItemsPlaceholderElement = function () {
        return $('.live-edit-component-menu-items', this.getEl());
    };
    proto.handleWindowResize = function (event) {
        if(this.$selectedComponent) {
            var x = this.previousPagePositions.x, y = this.previousPagePositions.y;
            x = x - (this.previousPageSizes.width - util.getViewPortSize().width);
            this.moveToXY(x, y);
        }
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function () {
    'use strict';
    var button = AdminLiveEdit.view.menu.BaseButton = function () {
    };
    button.prototype = new AdminLiveEdit.view.Base();
    var proto = button.prototype;
    proto.createButton = function (config) {
        var id = config.id || '';
        var text = config.text || '';
        var cls = config.cls || '';
        var iconCls = config.iconCls || '';
        var html = '<div data-live-edit-ui-cmp-id="' + id + '" class="live-edit-button ' + cls + '">';
        if(iconCls !== '') {
            html += '<span class="live-edit-button-icon ' + iconCls + '"></span>';
        }
        html += '<span class="live-edit-button-text">' + text + '</span></div>';
        var $button = this.createElement(html);
        if(config.handler) {
            $button.on('click', function (event) {
                config.handler.call(this, event);
            });
        }
        return $button;
    };
})());
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function ($) {
    'use strict';
    var parentButton = AdminLiveEdit.view.menu.ParentButton = function (menu) {
        this.menu = menu;
        this.init();
    };
    parentButton.prototype = new AdminLiveEdit.view.menu.BaseButton();
    var proto = parentButton.prototype;
    var util = AdminLiveEdit.Util;
    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            id: 'live-edit-button-parent',
            text: 'Select Parent',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
                var $parent = me.menu.$selectedComponent.parents('[data-live-edit-type]');
                if($parent && $parent.length > 0) {
                    $parent = $($parent[0]);
                    $(window).trigger('component.onSelect', [
                        $parent, 
                        {
                            x: 0,
                            y: 0
                        }
                    ]);
                    me.scrollComponentIntoView($parent);
                    var menuWidth = me.menu.getEl().outerWidth();
                    var componentBox = util.getBoxModel($parent), newMenuPosition = {
                        x: componentBox.left + (componentBox.width / 2) - (menuWidth / 2),
                        y: componentBox.top + 10
                    };
                    me.menu.moveToXY(newMenuPosition.x, newMenuPosition.y);
                }
            }
        });
        me.appendTo(this.menu.getEl());
        me.menu.buttons.push(me);
    };
    proto.scrollComponentIntoView = function ($component) {
        var componentTopPosition = util.getPagePositionForComponent($component).top;
        if(componentTopPosition <= window.pageYOffset) {
            $('html, body').animate({
                scrollTop: componentTopPosition - 10
            }, 200);
        }
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function ($) {
    'use strict';
    var openContentButton = AdminLiveEdit.view.menu.OpenContentButton = function (menu) {
        this.menu = menu;
        this.init();
    };
    openContentButton.prototype = new AdminLiveEdit.view.menu.BaseButton();
    var proto = openContentButton.prototype;
    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            text: 'Open in new tab',
            id: 'live-edit-button-opencontent',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function ($) {
    'use strict';
    var insertButton = AdminLiveEdit.view.menu.InsertButton = function (menu) {
        this.menu = menu;
        this.init();
    };
    insertButton.prototype = new AdminLiveEdit.view.menu.BaseButton();
    var proto = insertButton.prototype;
    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            text: 'Insert',
            id: 'live-edit-button-insert',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function ($) {
    'use strict';
    var detailsButton = AdminLiveEdit.view.menu.DetailsButton = function (menu) {
        this.menu = menu;
        this.init();
    };
    detailsButton.prototype = new AdminLiveEdit.view.menu.BaseButton();
    var proto = detailsButton.prototype;
    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            text: 'Show Details',
            id: 'live-edit-button-details',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function ($) {
    'use strict';
    var editButton = AdminLiveEdit.view.menu.EditButton = function (menu) {
        this.menu = menu;
        this.init();
    };
    editButton.prototype = new AdminLiveEdit.view.menu.BaseButton();
    var proto = editButton.prototype;
    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            id: 'live-edit-button-edit',
            text: 'Edit',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
                var $paragraph = me.menu.$selectedComponent;
                if($paragraph && $paragraph.length > 0) {
                    $(window).trigger('component.onParagraphEdit', [
                        $paragraph
                    ]);
                }
            }
        });
        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function ($) {
    'use strict';
    var resetButton = AdminLiveEdit.view.menu.ResetButton = function (menu) {
        this.menu = menu;
        this.init();
    };
    resetButton.prototype = new AdminLiveEdit.view.menu.BaseButton();
    var proto = resetButton.prototype;
    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            text: 'Reset to Default',
            id: 'live-edit-button-reset',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function ($) {
    'use strict';
    var clearButton = AdminLiveEdit.view.menu.ClearButton = function (menu) {
        this.menu = menu;
        this.init();
    };
    clearButton.prototype = new AdminLiveEdit.view.menu.BaseButton();
    var proto = clearButton.prototype;
    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            text: 'Empty',
            id: 'live-edit-button-clear',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function ($) {
    'use strict';
    var viewButton = AdminLiveEdit.view.menu.ViewButton = function (menu) {
        this.menu = menu;
        this.init();
    };
    viewButton.prototype = new AdminLiveEdit.view.menu.BaseButton();
    var proto = viewButton.prototype;
    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            text: 'View',
            id: 'live-edit-button-view',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function ($) {
    'use strict';
    var settingsButton = AdminLiveEdit.view.menu.SettingsButton = function (menu) {
        this.menu = menu;
        this.init();
    };
    settingsButton.prototype = new AdminLiveEdit.view.menu.BaseButton();
    var proto = settingsButton.prototype;
    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            text: 'Settings',
            id: 'live-edit-button-settings',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');
((function ($) {
    'use strict';
    var removeButton = AdminLiveEdit.view.menu.RemoveButton = function (menu) {
        this.menu = menu;
        this.init();
    };
    removeButton.prototype = new AdminLiveEdit.view.menu.BaseButton();
    var proto = removeButton.prototype;
    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            text: 'Remove',
            id: 'live-edit-button-remove',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
                me.menu.$selectedComponent.remove();
                $(window).trigger('component.onRemove');
            }
        });
        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };
})($liveedit));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.componentbar');
((function ($) {
    'use strict';
    AdminLiveEdit.view.componentbar = {
    };
    var BAR_WIDTH = 235;
    var TOGGLE_WIDTH = 30;
    var INNER_WIDTH = BAR_WIDTH - TOGGLE_WIDTH;
    var componentBar = AdminLiveEdit.view.componentbar.ComponentBar = function () {
        var me = this;
        me.hidden = true;
        me.addView();
        me.loadComponentsData();
        me.registerGlobalListeners();
        me.registerEvents();
    };
    componentBar.prototype = new AdminLiveEdit.view.Base();
    var proto = componentBar.prototype;
    var html = '';
    html += '<div class="live-edit-components-container live-edit-collapsed" style="width:' + BAR_WIDTH + 'px; right: -' + INNER_WIDTH + 'px">';
    html += '    <div class="live-edit-toggle-components-container" style="width:' + TOGGLE_WIDTH + 'px"><span class="live-edit-toggle-text-container">Toolbar</span></div>';
    html += '        <div class="live-edit-components">';
    html += '            <div class="live-edit-form-container">';
    html += '               <form onsubmit="return false;">';
    html += '                   <input type="text" placeholder="Filter" name="filter"/>';
    html += '               </form>';
    html += '            </div>';
    html += '            <ul>';
    html += '            </ul>';
    html += '        </div>';
    html += '    </div>';
    html += '</div>';
    proto.getComponentsDataUrl = function () {
        return '../../../admin2/live-edit/data/mock-components.json';
    };
    proto.addView = function () {
        var me = this;
        me.createElement(html);
        me.appendTo($('body'));
    };
    proto.registerGlobalListeners = function () {
        var me = this;
        $(window).on('component.onSelect', $.proxy(me.fadeOut, me));
        $(window).on('component.onDeselect', $.proxy(me.fadeIn, me));
        $(window).on('component.onDragStart', $.proxy(me.fadeOut, me));
        $(window).on('component.onDragStop', $.proxy(me.fadeIn, me));
        $(window).on('component.onSortStop', $.proxy(me.fadeIn, me));
        $(window).on('component.onSortStart', $.proxy(me.fadeOut, me));
        $(window).on('component.onSortUpdate', $.proxy(me.fadeIn, me));
        $(window).on('component.onRemove', $.proxy(me.fadeIn, me));
    };
    proto.registerEvents = function () {
        var me = this;
        me.getToggle().click(function () {
            me.toggle();
        });
        me.getFilterInput().on('keyup', function () {
            me.filterList($(this).val());
        });
        me.getBar().on('mouseover', function () {
            $(window).trigger('componentBar:mouseover');
        });
    };
    proto.loadComponentsData = function () {
        var me = this;
        $.getJSON(me.getComponentsDataUrl(), null, function (data, textStatus, jqXHR) {
            me.renderComponents(data);
            $(window).trigger('componentBar.dataLoaded');
        });
    };
    proto.renderComponents = function (jsonData) {
        var me = this, $container = me.getComponentsContainer(), groups = jsonData.componentGroups;
        $.each(groups, function (index, group) {
            me.addHeader(group);
            if(group.components) {
                me.addComponentsToGroup(group.components);
            }
        });
    };
    proto.addHeader = function (componentGroup) {
        var me = this, html = '';
        html += '<li class="live-edit-component-list-header">';
        html += '    <span>' + componentGroup.name + '</span>';
        html += '</li>';
        me.getComponentsContainer().append(html);
    };
    proto.addComponentsToGroup = function (components) {
        var me = this;
        $.each(components, function (index, component) {
            me.addComponent(component);
        });
    };
    proto.addComponent = function (component) {
        var me = this, html = '';
        html += '<li class="live-edit-component" data-live-edit-component-key="' + component.key + '" data-live-edit-component-name="' + component.name + '" data-live-edit-component-type="' + component.type + '">';
        html += '    <img src="' + component.icon + '"/>';
        html += '    <div class="live-edit-component-text">';
        html += '        <div class="live-edit-component-text-name">' + component.name + '</div>';
        html += '        <div class="live-edit-component-text-subtitle">' + component.subtitle + '</div>';
        html += '    </div>';
        html += '</li>';
        me.getComponentsContainer().append(html);
    };
    proto.filterList = function (value) {
        var me = this, $element, name, valueLowerCased = value.toLowerCase();
        me.getComponentList().each(function (index) {
            $element = $(this);
            name = $element.data('live-edit-component-name').toLowerCase();
            $element.css('display', name.indexOf(valueLowerCased) > -1 ? '' : 'none');
        });
    };
    proto.toggle = function () {
        var me = this;
        if(me.hidden) {
            me.show();
            me.hidden = false;
        } else {
            me.hide();
            me.hidden = true;
        }
    };
    proto.show = function () {
        var me = this;
        var $bar = me.getBar();
        $bar.css('right', '0');
        me.getToggleTextContainer().text('');
        $bar.removeClass('live-edit-collapsed');
    };
    proto.hide = function () {
        var me = this;
        var $bar = me.getBar();
        $bar.css('right', '-' + INNER_WIDTH + 'px');
        me.getToggleTextContainer().text('Toolbar');
        $bar.addClass('live-edit-collapsed');
    };
    proto.fadeIn = function (event, triggerConfig) {
        if(triggerConfig && triggerConfig.showComponentBar === false) {
            return;
        }
        this.getBar().fadeIn(120);
    };
    proto.fadeOut = function (event) {
        this.getBar().fadeOut(120);
    };
    proto.getBar = function () {
        return this.getEl();
    };
    proto.getToggle = function () {
        return $('.live-edit-toggle-components-container', this.getEl());
    };
    proto.getFilterInput = function () {
        return $('.live-edit-form-container input[name=filter]', this.getEl());
    };
    proto.getComponentsContainer = function () {
        return $('.live-edit-components ul', this.getEl());
    };
    proto.getComponentList = function () {
        return $('.live-edit-component', this.getEl());
    };
    proto.getToggleTextContainer = function () {
        return $('.live-edit-toggle-text-container', this.getEl());
    };
})($liveedit));
((function ($) {
    'use strict';
    $(window).load(function () {
        $('.live-edit-loader-splash-container').fadeOut('fast', function () {
            $(this).remove();
            new AdminLiveEdit.model.component.Page();
            new AdminLiveEdit.model.component.Region();
            new AdminLiveEdit.model.component.Layout();
            new AdminLiveEdit.model.component.Part();
            new AdminLiveEdit.model.component.Content();
            new AdminLiveEdit.model.component.Paragraph();
            new AdminLiveEdit.view.HtmlElementReplacer();
            new AdminLiveEdit.view.Highlighter();
            new AdminLiveEdit.view.ToolTip();
            new AdminLiveEdit.view.Cursor();
            new AdminLiveEdit.view.menu.Menu();
            new AdminLiveEdit.view.Shader();
            new AdminLiveEdit.view.htmleditor.Editor();
            new AdminLiveEdit.view.componentbar.ComponentBar();
            new AdminLiveEdit.MutationObserver();
            AdminLiveEdit.DragDropSort.initialize();
            $(window).resize(function () {
                $(window).trigger('liveEdit.onWindowResize');
            });
        });
    });
    $(document).ready(function () {
        $(document).on('mousedown', 'btn, button, a, select', function (event) {
            event.preventDefault();
            return false;
        });
    });
})($liveedit));
//@ sourceMappingURL=all.js.map
