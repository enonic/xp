/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
Ext.define('Admin.lib.Sortable', {
    constructor: function (parentComponent, config) {
        var me = this;
        me.config = config || {};
        me.parentComponent = parentComponent;
        me.id = Ext.id();
        me.group = me.config.group || '' + me.id;
        me.indicatorEl = me._createDDIndicator();

        me._initDragZone();
        me._initDropZone();
    },


    _initDragZone: function () {
        var sortable = this;

        var dragZone = new Ext.dd.DragZone(sortable.parentComponent.getEl(), {
            ddGroup: sortable.group,
            containerScroll: true,
            getDragData: function (e) {
                var sourceElement = e.getTarget('.admin-sortable');
                if (!sourceElement) {
                    return;
                }

                // If a handle is configured and anything else than the handle is pressed, return.
                if (sortable.config.handle && Ext.fly(sourceElement).down(sortable.config.handle).dom !== e.getTarget()) {
                    return;
                }

                var dragProxyElement = sortable._createDragProxy(sourceElement);

                Ext.fly(sourceElement).setStyle('opacity', '.2');

                return {
                    ddel: dragProxyElement,
                    sourceElement: sourceElement,
                    repairXY: Ext.fly(sourceElement).getXY()
                };
            },

            onMouseUp: function (e) {
                Ext.fly(this.dragData.sourceElement).setStyle('opacity', '1');
            },

            afterInvalidDrop: function (e, id) {
                sortable._hideIndicator();
            },

            getRepairXY: function () {
                return this.dragData.repairXY;
            }
        });
    },


    _initDropZone: function () {
        var sortable = this;

        var dropZone = new Ext.dd.DropZone(sortable.parentComponent.getEl(), {
            ddGroup: sortable.group,

            getTargetFromEvent: function (e) {
                return e.getTarget('.admin-sortable');
            },

            onNodeOver: function (target, dd, e, data) {
                var cmpNode = Ext.getCmp(target.id);
                if (!cmpNode) {
                    return;
                }

                if (target === data.sourceElement) {
                    // return;
                }

                var mouseYPos = e.getY();

                var cmpArea = cmpNode.getEl().getPageBox();

                var nodeMiddle = cmpArea.top + cmpArea.height / 2;

                if (mouseYPos < nodeMiddle) {
                    sortable.currentPos = 'above';
                } else {
                    sortable.currentPos = 'below';
                }

                sortable._showIndicator(cmpArea, sortable.currentPos);

                return Ext.dd.DropZone.prototype.dropAllowed;
            },

            onNodeOut: function (nodeData, source, e, data) {
                if (e.getTarget().className.indexOf('admin-drop-indicator') > -1) {
                    return;
                }
                sortable._hideIndicator();
            },

            onNodeDrop: function (target, dd, e, data) {
                var draggedCmp = Ext.getCmp(data.sourceElement.id);
                var targetCmp = Ext.getCmp(target.id);

                if (target === data.sourceElement) {
                    return;
                }

                draggedCmp.getEl().setStyle('opacity', 1);

                if (targetCmp) {
                    var targetCmpIndex = sortable._getIndexOfComponent(targetCmp);
                    var draggedCmpOrgIndex = sortable._getIndexOfComponent(draggedCmp);

                    if (sortable.currentPos === 'below') {
                        targetCmpIndex = targetCmpIndex + 1;
                        if (draggedCmpOrgIndex < targetCmpIndex) {
                            targetCmpIndex = targetCmpIndex - 1;
                        }
                    }

                    sortable.parentComponent.insert(targetCmpIndex, draggedCmp);
                    sortable.parentComponent.doLayout();

                    draggedCmp.getEl().highlight();
                }

                sortable._hideIndicator();

                return true;
            }
        });

    },


    _createDDIndicator: function () {
        var me = this,
            indicatorEl,
            arrowLeft;

        indicatorEl = Ext.get(document.createElement('div'));
        indicatorEl.addCls('admin-drop-indicator');
        indicatorEl.setStyle('display', 'none');
        indicatorEl.setStyle('width', '500px');
        indicatorEl.setStyle('top', '10px');
        indicatorEl.setStyle('left', '10px');
        indicatorEl.appendTo(Ext.getBody());

        return indicatorEl;
    },


    _showIndicator: function (area, position) {
        var me = this,
            top;

        me.indicatorEl.show();

        top = position === 'above' ? area.top: area.bottom;

        me.indicatorEl.setStyle('width', area.width + 'px');
        me.indicatorEl.setStyle('top', top + 'px');
        me.indicatorEl.setStyle('left', area.left + 'px');
    },


    _hideIndicator: function () {
        this.indicatorEl.hide();
    },


    _createDragProxy: function (sourceElement) {
        var me = this,
            proxyEl;

        if (me.config.proxyHtml) {
            proxyEl = Ext.get(document.createElement('div'));
            proxyEl.setHTML(me.config.proxyHtml);
            proxyEl = proxyEl.dom;
        } else { // If no proxy is configured we'll clone the source element
            proxyEl = sourceElement.cloneNode(true);
        }

        return proxyEl;
    },

    _getIndexOfComponent: function (cmp) {
        return this.parentComponent.items.indexOf(cmp);
    }

});