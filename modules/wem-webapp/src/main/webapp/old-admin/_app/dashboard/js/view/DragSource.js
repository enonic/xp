Ext.define('App.view.DragSource', {
    extend: 'Ext.dd.DragSource',

    constructor: function (panel, cfg) {
        this.panel = panel;
        this.dragData = {panel: panel};

        this.callParent([panel.el, cfg]);

        if (this.containerScroll) {
            Ext.dd.ScrollManager.register(this.el);
        }
    },

    onInitDrag: function (x, y) {
        this.proxy.update(this.dragData.ddel.cloneNode(true));
        this.onStartDrag(x, y);
        return true;
    },

    getDragData: function (e) {
        var sourceEl = e.getTarget(this.panel.itemSelector, 10);
        if (sourceEl) {
            var d = sourceEl.cloneNode(true);
            d.id = Ext.id();
            return {
                ddel: d,
                sourceEl: sourceEl,
                repairXY: Ext.fly(sourceEl).getXY(),
                draggedRecord: this.panel.getRecord(sourceEl)
            };
        }
    },

    getRepairXY: function () {
        return this.dragData.repairXY;
    },

    afterRepair: function () {
        var me = this;
        if (Ext.enableFx) {
            Ext.fly(me.dragData.ddel).highlight(me.repairHighlightColor);
        }
        me.dragging = false;
    },

    destroy: function () {
        this.callParent();
        if (this.containerScroll) {
            Ext.dd.ScrollManager.unregister(this.el);
        }
    }

});