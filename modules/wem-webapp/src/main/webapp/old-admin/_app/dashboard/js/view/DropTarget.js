Ext.define("App.view.DropTarget", {
    extend: "Ext.dd.DropTarget",

    ddScrollConfig: {
        vthresh: 50,
        hthresh: -1,
        animate: true,
        increment: 200
    },

    constructor: function (a, b) {
        this.portal = a;
        Ext.dd.ScrollManager.register(a.body);
        App.view.DropTarget.superclass.constructor.call(this, a.body, b);
        a.body.ddScrollConfig = this.ddScrollConfig;
    },
    createEvent: function (a, f, d, b, h, g) {
        return {
            portal: this.portal,
            panel: d.panel,
            columnIndex: b,
            column: h,
            position: g,
            data: d,
            source: a,
            rawEvent: f,
            status: this.dropAllowed
        };
    },
    notifyOver: function (u, t, v) {
        var d = t.getXY(),
            a = this.portal,
            p = u.panelProxy;
        if (!this.grid) {
            this.grid = this.getGrid();
        }
        var b = a.body.dom.clientWidth;
        if (!this.lastCW) {
            this.lastCW = b;
        } else {
            if (this.lastCW !== b) {
                this.lastCW = b;
                this.grid = this.getGrid();
            }
        }
        var o = 0,
            c = 0,
            n = this.grid.columnX,
            q = n.length,
            m = false;
        for (q; o < q; o++) {
            c = n[o].x + n[o].w;
            if (d[0] < c) {
                m = true;
                break;
            }
        }
        if (!m) {
            o--;
        }
        var i, g = 0,
            r = 0,
            l = false,
            k = a.items.getAt(o),
            s = k.items.items,
            j = false;
        q = s.length;
        for (q; g < q; g++) {
            i = s[g];
            r = i.el.getHeight();
            if (r === 0) {
                j = true;
            } else {
                if ((i.el.getY() + (r / 2)) > d[1]) {
                    l = true;
                    break;
                }
            }
        }
        g = (l && i ? g : k.items.getCount()) + (j ? -1 : 0);
        var f = this.createEvent(u, t, v, o, k, g);
        if (a.fireEvent("validatedrop", f) !== false && a.fireEvent("beforedragover", f) !== false) {
            if (!v.draggedRecord) {
                p.getProxy().setWidth("auto");
                if (i) {
                    p.moveProxy(i.el.dom.parentNode, l ? i.el.dom : null);
                } else {
                    p.moveProxy(k.el.dom, null);
                }
            }
            this.lastPos = {
                c: k,
                col: o,
                p: j || (l && i) ? g : false
            };
            this.scrollPos = a.body.getScroll();
            a.fireEvent("dragover", f);
            return f.status;
        } else {
            return f.status;
        }
    },
    notifyOut: function () {
        delete this.grid;
    },
    notifyDrop: function (l, h, g) {
        delete this.grid;
        if (!this.lastPos) {
            return;
        }
        var j = this.lastPos.c,
            f = this.lastPos.col,
            k = this.lastPos.p,
            a = l.panel,
            b = this.createEvent(l, h, g, f, j, k !== false ? k : j.items.getCount());
        if (this.portal.fireEvent("validatedrop", b) !== false &&
            this.portal.fireEvent("beforedrop", b) !== false) {
            Ext.suspendLayouts();

            if (!g.draggedRecord) {
                a.el.dom.style.display = "";
                if (k !== false) {
                    j.insert(k, a);
                } else {
                    j.add(a);
                }
                l.proxy.hide();
                l.panelProxy.hide();
            } else {
                var dashlet = Ext.create(g.draggedRecord.data.xtype, {
                    title: g.draggedRecord.data.title,
                    html: g.draggedRecord.data.body
                });
                Ext.isNumber(k) ? j.insert(k, dashlet) : j.add(dashlet);
            }

            Ext.resumeLayouts(true);
            this.portal.fireEvent("drop", b);
            var m = this.scrollPos.top;
            if (m) {
                var i = this.portal.body.dom;
                setTimeout(function () {
                    i.scrollTop = m;
                }, 10);
            }
        }
        delete this.lastPos;
        return true;
    },
    getGrid: function () {
        var a = this.portal.body.getBox();
        a.columnX = [];
        this.portal.items.each(function (b) {
            a.columnX.push({
                x: b.el.getX(),
                w: b.el.getWidth()
            });
        });
        return a;
    },
    unreg: function () {
        Ext.dd.ScrollManager.unregister(this.portal.body);
        App.view.DropTarget.superclass.unreg.call(this);
    }
});