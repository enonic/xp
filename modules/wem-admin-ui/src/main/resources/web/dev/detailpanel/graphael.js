/*!
 * g.Raphael 0.51 - Charting library, based on Raphaël
 *
 * Copyright (c) 2009-2012 Dmitry Baranovskiy (http://g.raphaeljs.com)
 * Licensed under the MIT (http://www.opensource.org/licenses/mit-license.php) license.
 */
Raphael.el.popup = function (d, a, b, f) {
    var e = this.paper || this[0].paper, c, g, i, h;
    if (e) {
        switch (this.type) {
        case "text":
        case "circle":
        case "ellipse":
            g = !0;
            break;
        default:
            g = !1
        }
        d = null == d ? "up" : d;
        a = a || 5;
        c = this.getBBox();
        b = "number" == typeof b ? b : g ? c.x + c.width / 2 : c.x;
        f = "number" == typeof f ? f : g ? c.y + c.height / 2 : c.y;
        i = Math.max(c.width / 2 - a, 0);
        h = Math.max(c.height / 2 - a, 0);
        this.translate(b - c.x - (g ? c.width / 2 : 0), f - c.y - (g ? c.height / 2 : 0));
        c = this.getBBox();
        b = {up: ["M", b, f, "l", -a, -a, -i, 0, "a", a, a, 0, 0, 1, -a, -a, "l", 0, -c.height, "a", a, a, 0, 0,
            1, a, -a, "l", 2 * a + 2 * i, 0, "a", a, a, 0, 0, 1, a, a, "l", 0, c.height, "a", a, a, 0, 0, 1, -a, a, "l", -i, 0, "z"
        ].join(), down: ["M", b, f, "l", a, a, i, 0, "a", a, a, 0, 0, 1, a, a, "l", 0, c.height, "a", a, a, 0, 0, 1, -a, a, "l",
            -(2 * a + 2 * i), 0, "a", a, a, 0, 0, 1, -a, -a, "l", 0, -c.height, "a", a, a, 0, 0, 1, a, -a, "l", i, 0, "z"].join(), left: [
            "M", b, f, "l", -a, a, 0, h, "a", a, a, 0, 0, 1, -a, a, "l", -c.width, 0, "a", a, a, 0, 0, 1, -a, -a, "l", 0, -(2 * a + 2 * h),
            "a", a, a, 0, 0, 1, a, -a, "l", c.width, 0, "a", a, a, 0, 0, 1, a, a, "l", 0, h, "z"].join(), right: ["M", b, f, "l", a, -a, 0,
            -h, "a", a, a, 0, 0, 1, a, -a, "l", c.width, 0, "a", a,
            a, 0, 0, 1, a, a, "l", 0, 2 * a + 2 * h, "a", a, a, 0, 0, 1, -a, a, "l", -c.width, 0, "a", a, a, 0, 0, 1, -a, -a, "l", 0, -h,
            "z"].join()};
        a = {up: {x: -!g * (c.width / 2), y: 2 * -a - (g ? c.height / 2 : c.height)}, down: {x: -!g * (c.width / 2), y: 2 * a +
                                                                                                                        (g ? c.height / 2
                                                                                                                            : c.height)}, left: {x: 2 *
                                                                                                                                                    -a -
                                                                                                                                                    (g
                                                                                                                                                        ? c.width /
                                                                                                                                                          2
                                                                                                                                                        : c.width), y: -!g *
                                                                                                                                                                       (c.height /
                                                                                                                                                                        2)}, right: {x: 2 *
                                                                                                                                                                                        a +
                                                                                                                                                                                        (g
                                                                                                                                                                                            ? c.width /
                                                                                                                                                                                              2
                                                                                                                                                                                            : c.width), y: -!g *
                                                                                                                                                                                                           (c.height /
                                                                                                                                                                                                            2)}}[d];
        this.translate(a.x, a.y);
        return e.path(b[d]).attr({fill: "#000", stroke: "none"}).insertBefore(this.node ? this : this[0])
    }
};
Raphael.el.tag = function (d, a, b, f) {
    var e = this.paper || this[0].paper;
    if (e) {
        var e = e.path().attr({fill: "#000", stroke: "#000"}), c = this.getBBox(), g, i, h;
        switch (this.type) {
        case "text":
        case "circle":
        case "ellipse":
            h = !0;
            break;
        default:
            h = !1
        }
        d = d || 0;
        b = "number" == typeof b ? b : h ? c.x + c.width / 2 : c.x;
        f = "number" == typeof f ? f : h ? c.y + c.height / 2 : c.y;
        a = null == a ? 5 : a;
        i = 0.5522 * a;
        c.height >= 2 * a ? e.attr({path: ["M", b, f + a, "a", a, a, 0, 1, 1, 0, 2 * -a, a, a, 0, 1, 1, 0, 2 * a, "m", 0, 2 * -a - 3, "a",
            a + 3, a + 3, 0, 1, 0, 0, 2 * (a + 3), "L", b + a + 3, f + c.height / 2 + 3, "l", c.width + 6, 0,
            0, -c.height - 6, -c.width - 6, 0, "L", b, f - a - 3].join()}) : (g = Math.sqrt(Math.pow(a + 3, 2) -
                                                                                            Math.pow(c.height / 2 + 3, 2)), e.attr({path: [
            "M", b, f + a, "c", -i, 0, -a, i - a, -a, -a, 0, -i, a - i, -a, a, -a, i, 0, a, a - i, a, a, 0, i, i - a, a, -a, a, "M", b + g,
            f - c.height / 2 - 3, "a", a + 3, a + 3, 0, 1, 0, 0, c.height + 6, "l", a + 3 - g + c.width + 6, 0, 0, -c.height - 6, "L",
            b + g, f - c.height / 2 - 3].join()}));
        d = 360 - d;
        e.rotate(d, b, f);
        this.attrs ? (this.attr(this.attrs.x ? "x" : "cx", b + a + 3 + (!h ? "text" == this.type ? c.width : 0 : c.width / 2)).attr("y",
            h ? f : f - c.height / 2), this.rotate(d, b, f), 90 < d && 270 > d && this.attr(this.attrs.x ?
            "x" : "cx", b - a - 3 - (!h ? c.width : c.width / 2)).rotate(180, b, f)) : 90 < d && 270 > d ? (this.translate(b - c.x -
                                                                                                                           c.width - a - 3,
            f - c.y - c.height / 2), this.rotate(d - 180, c.x + c.width + a + 3, c.y + c.height / 2)) : (this.translate(b - c.x + a + 3,
            f - c.y - c.height / 2), this.rotate(d, c.x - a - 3, c.y + c.height / 2));
        return e.insertBefore(this.node ? this : this[0])
    }
};
Raphael.el.drop = function (d, a, b) {
    var f = this.getBBox(), e = this.paper || this[0].paper, c, g;
    if (e) {
        switch (this.type) {
        case "text":
        case "circle":
        case "ellipse":
            c = !0;
            break;
        default:
            c = !1
        }
        d = d || 0;
        a = "number" == typeof a ? a : c ? f.x + f.width / 2 : f.x;
        b = "number" == typeof b ? b : c ? f.y + f.height / 2 : f.y;
        g = Math.max(f.width, f.height) + Math.min(f.width, f.height);
        e = e.path(["M", a, b, "l", g, 0, "A", 0.4 * g, 0.4 * g, 0, 1, 0, a + 0.7 * g, b - 0.7 * g, "z"
        ]).attr({fill: "#000", stroke: "none"}).rotate(22.5 - d, a, b);
        d = (d + 90) * Math.PI / 180;
        a = a + g * Math.sin(d) - (c ? 0 : f.width / 2);
        d = b + g * Math.cos(d) - (c ? 0 : f.height / 2);
        this.attrs ? this.attr(this.attrs.x ? "x" : "cx", a).attr(this.attrs.y ? "y" : "cy", d) : this.translate(a - f.x, d - f.y);
        return e.insertBefore(this.node ? this : this[0])
    }
};
Raphael.el.flag = function (d, a, b) {
    var f = this.paper || this[0].paper;
    if (f) {
        var f = f.path().attr({fill: "#000", stroke: "#000"}), e = this.getBBox(), c = e.height / 2, g;
        switch (this.type) {
        case "text":
        case "circle":
        case "ellipse":
            g = !0;
            break;
        default:
            g = !1
        }
        d = d || 0;
        a = "number" == typeof a ? a : g ? e.x + e.width / 2 : e.x;
        b = "number" == typeof b ? b : g ? e.y + e.height / 2 : e.y;
        f.attr({path: ["M", a, b, "l", c + 3, -c - 3, e.width + 6, 0, 0, e.height + 6, -e.width - 6, 0, "z"].join()});
        d = 360 - d;
        f.rotate(d, a, b);
        this.attrs ? (this.attr(this.attrs.x ? "x" : "cx", a + c + 3 + (!g ? "text" ==
                                                                             this.type ? e.width : 0 : e.width / 2)).attr("y",
                g ? b : b - e.height / 2), this.rotate(d, a, b), 90 < d && 270 > d && this.attr(this.attrs.x ? "x" : "cx",
            a - c - 3 - (!g ? e.width : e.width / 2)).rotate(180, a, b)) : 90 < d && 270 > d ? (this.translate(a - e.x - e.width - c - 3,
            b - e.y - e.height / 2), this.rotate(d - 180, e.x + e.width + c + 3, e.y + e.height / 2)) : (this.translate(a - e.x + c + 3,
            b - e.y - e.height / 2), this.rotate(d, e.x - c - 3, e.y + e.height / 2));
        return f.insertBefore(this.node ? this : this[0])
    }
};
Raphael.el.label = function () {
    var d = this.getBBox(), a = this.paper || this[0].paper, b = Math.min(20, d.width + 10, d.height + 10) / 2;
    if (a) {
        return a.rect(d.x - b / 2, d.y - b / 2, d.width + b, d.height + b, b).attr({stroke: "none", fill: "#000"}).insertBefore(this.node
            ? this : this[0])
    }
};
Raphael.el.blob = function (d, a, b) {
    var f = this.getBBox(), e = Math.PI / 180, c = this.paper || this[0].paper, g, i;
    if (c) {
        switch (this.type) {
        case "text":
        case "circle":
        case "ellipse":
            g = !0;
            break;
        default:
            g = !1
        }
        c = c.path().attr({fill: "#000", stroke: "none"});
        d = (+d + 1 ? d : 45) + 90;
        i = Math.min(f.height, f.width);
        var a = "number" == typeof a ? a : g ? f.x + f.width / 2 : f.x, b = "number" == typeof b ? b : g ? f.y + f.height / 2
            : f.y, h = Math.max(f.width + i, 25 * i / 12), j = Math.max(f.height + i, 25 * i / 12);
        g = a + i * Math.sin((d - 22.5) * e);
        var o = b + i * Math.cos((d - 22.5) * e), l = a + i * Math.sin((d +
                                                                        22.5) * e), d = b + i * Math.cos((d + 22.5) * e), e = (l - g) / 2;
        i = (d - o) / 2;
        var h = h / 2, j = j / 2, n = -Math.sqrt(Math.abs(h * h * j * j - h * h * i * i - j * j * e * e) / (h * h * i * i + j * j * e * e));
        i = n * h * i / j + (l + g) / 2;
        e = n * -j * e / h + (d + o) / 2;
        c.attr({x: i, y: e, path: ["M", a, b, "L", l, d, "A", h, j, 0, 1, 1, g, o, "z"].join()});
        this.translate(i - f.x - f.width / 2, e - f.y - f.height / 2);
        return c.insertBefore(this.node ? this : this[0])
    }
};
Raphael.fn.label = function (d, a, b) {
    var f = this.set(), b = this.text(d, a, b).attr(Raphael.g.txtattr);
    return f.push(b.label(), b)
};
Raphael.fn.popup = function (d, a, b, f, e) {
    var c = this.set(), b = this.text(d, a, b).attr(Raphael.g.txtattr);
    return c.push(b.popup(f, e), b)
};
Raphael.fn.tag = function (d, a, b, f, e) {
    var c = this.set(), b = this.text(d, a, b).attr(Raphael.g.txtattr);
    return c.push(b.tag(f, e), b)
};
Raphael.fn.flag = function (d, a, b, f) {
    var e = this.set(), b = this.text(d, a, b).attr(Raphael.g.txtattr);
    return e.push(b.flag(f), b)
};
Raphael.fn.drop = function (d, a, b, f) {
    var e = this.set(), b = this.text(d, a, b).attr(Raphael.g.txtattr);
    return e.push(b.drop(f), b)
};
Raphael.fn.blob = function (d, a, b, f) {
    var e = this.set(), b = this.text(d, a, b).attr(Raphael.g.txtattr);
    return e.push(b.blob(f), b)
};
Raphael.el.lighter = function (d) {
    var d = d || 2, a = [this.attrs.fill, this.attrs.stroke];
    this.fs = this.fs || [a[0], a[1]];
    a[0] = Raphael.rgb2hsb(Raphael.getRGB(a[0]).hex);
    a[1] = Raphael.rgb2hsb(Raphael.getRGB(a[1]).hex);
    a[0].b = Math.min(a[0].b * d, 1);
    a[0].s /= d;
    a[1].b = Math.min(a[1].b * d, 1);
    a[1].s /= d;
    this.attr({fill: "hsb(" + [a[0].h, a[0].s, a[0].b] + ")", stroke: "hsb(" + [a[1].h, a[1].s, a[1].b] + ")"});
    return this
};
Raphael.el.darker = function (d) {
    var d = d || 2, a = [this.attrs.fill, this.attrs.stroke];
    this.fs = this.fs || [a[0], a[1]];
    a[0] = Raphael.rgb2hsb(Raphael.getRGB(a[0]).hex);
    a[1] = Raphael.rgb2hsb(Raphael.getRGB(a[1]).hex);
    a[0].s = Math.min(a[0].s * d, 1);
    a[0].b /= d;
    a[1].s = Math.min(a[1].s * d, 1);
    a[1].b /= d;
    this.attr({fill: "hsb(" + [a[0].h, a[0].s, a[0].b] + ")", stroke: "hsb(" + [a[1].h, a[1].s, a[1].b] + ")"});
    return this
};
Raphael.el.resetBrightness = function () {
    this.fs && (this.attr({fill: this.fs[0], stroke: this.fs[1]}), delete this.fs);
    return this
};
(function () {
    var d = ["lighter", "darker", "resetBrightness"], a = "popup tag flag label drop blob".split(" "), b;
    for (b in a) {
        (function (a) {
            Raphael.st[a] = function () {
                return Raphael.el[a].apply(this, arguments)
            }
        })(a[b]);
    }
    for (b in d) {
        (function (a) {
            Raphael.st[a] = function () {
                for (var b = 0; b < this.length; b++) {
                    this[b][a].apply(this[b], arguments);
                }
                return this
            }
        })(d[b])
    }
})();
Raphael.g =
{shim: {stroke: "none", fill: "#000", "fill-opacity": 0}, txtattr: {font: "12px Arial, sans-serif", fill: "#fff"}, colors: function () {
    for (var d = [0.6, 0.2, 0.05, 0.1333, 0.75, 0], a = [], b = 0; 10 > b; b++) {
        b < d.length ? a.push("hsb(" + d[b] + ",.75, .75)")
            : a.push("hsb(" + d[b - d.length] + ", 1, .5)");
    }
    return a
}(), snapEnds: function (d, a, b) {
    function f(a) {
        return 0.25 > Math.abs(a - 0.5) ? ~~a + 0.5 : Math.round(a)
    }

    var e = d, c = a;
    if (e == c) {
        return{from: e, to: c, power: 0};
    }
    var e = (c - e) / b, g = c = ~~e, b = 0;
    if (c) {
        for (; g;) {
            b--, g = ~~(e * Math.pow(10, b)) / Math.pow(10, b);
        }
        b++
    } else {
        if (0 == e || !isFinite(e)) {
            b = 1;
        } else {
            for (; !c;) {
                b = b || 1, c = ~~(e * Math.pow(10, b)) / Math.pow(10, b), b++;
            }
        }
        b && b--
    }
    c = f(a * Math.pow(10, b)) / Math.pow(10, b);
    c < a && (c = f((a + 0.5) * Math.pow(10, b)) / Math.pow(10, b));
    e = f((d - (0 < b ? 0 : 0.5)) * Math.pow(10, b)) / Math.pow(10, b);
    return{from: e, to: c, power: b}
}, axis: function (d, a, b, f, e, c, g, i, h, j, o) {
    var j = null == j ? 2 : j, h = h || "t", c = c || 10, o = arguments[arguments.length - 1], l = "|" == h || " " == h ? ["M", d + 0.5, a,
            "l", 0, 0.001] : 1 == g || 3 == g ? ["M", d + 0.5, a, "l", 0, -b] : ["M", d, a + 0.5, "l", b, 0], n = this.snapEnds(f, e,
            c), p = n.from,
        t = n.to, m = n.power, u = 0, v = {font: "11px 'Fontin Sans', Fontin-Sans, sans-serif"}, n = o.set(), t = (t - p) /
                                                                                                                  c, k = p, r = 0 < m ? m
            : 0, s = b / c;
    if (1 == +g || 3 == +g) {
        m = a;
        for (p = (g - 1 ? 1 : -1) * (j + 3 + !!(g - 1)); m >= a - b;) {
            "-" != h && " " != h && (l = l.concat(
                ["M", d - ("+" == h || "|" == h ? j : 2 * !(g - 1) * j), m + 0.5, "l", 2 * j + 1, 0])), n.push(o.text(d + p, m,
                i && i[u++] || (Math.round(k) == k ? k : +k.toFixed(r))).attr(v).attr({"text-anchor": g - 1 ? "start" : "end"})), k +=
                                                                                                                                  t, m -=
                                                                                                                                     s;
        }
        Math.round(m + s - (a - b)) && ("-" != h && " " != h && (l = l.concat(
            ["M", d - ("+" == h || "|" == h ? j : 2 * !(g - 1) * j), a - b + 0.5, "l", 2 * j + 1, 0])), n.push(o.text(d +
                                                                                                                      p, a - b,
            i && i[u] || (Math.round(k) == k ? k : +k.toFixed(r))).attr(v).attr({"text-anchor": g - 1 ? "start" : "end"})))
    } else {
        for (var k = p, r = (0 < m) * m, p = (g ? -1 : 1) * (j + 9 + !g), m = d, s = b / c, q = 0, w = 0; m <= d + b;) {
            "-" != h &&
            " " != h && (l =
                         l.concat(
                             ["M",
                                 m +
                                 0.5,
                                 a -
                                 ("+" ==
                                  h
                                     ? j
                                     : 2 * !!g *
                                       j),
                                 "l",
                                 0,
                                 2 *
                                 j +
                                 1
                             ])), n.push(q =
                                         o.text(m,
                                             a +
                                             p,
                                             i &&
                                             i[u++] ||
                                             (Math.round(k) ==
                                              k
                                                 ? k
                                                 : +k.toFixed(r))).attr(v)), q =
                                                                             q.getBBox(), w >=
                                                                                          q.x -
                                                                                          5
                ? n.pop(n.length - 1).remove() : w = q.x + q.width, k += t, m += s;
        }
        Math.round(m - s - d - b) &&
        ("-" != h && " " != h && (l = l.concat(["M", d + b + 0.5, a - ("+" == h ? j : 2 * !!g * j), "l", 0, 2 * j + 1])), n.push(o.text(d +
                                                                                                                                        b,
            a + p, i && i[u] || (Math.round(k) == k ? k : +k.toFixed(r))).attr(v)))
    }
    l = o.path(l);
    l.text = n;
    l.all = o.set([l, n]);
    l.remove = function () {
        this.text.remove();
        this.constructor.prototype.remove.call(this)
    };
    return l
}, labelise: function (d, a, b) {
    return d ? (d + "").replace(/(##+(?:\.#+)?)|(%%+(?:\.%+)?)/g, function (d, e, c) {
        if (e) {
            return(+a).toFixed(e.replace(/^#+\.?/g, "").length);
        }
        if (c) {
            return(100 * a / b).toFixed(c.replace(/^%+\.?/g, "").length) + "%"
        }
    }) : (+a).toFixed(0)
}};
/*!
 * g.Raphael 0.51 - Charting library, based on Raphaël
 *
 * Copyright (c) 2009-2012 Dmitry Baranovskiy (http://g.raphaeljs.com)
 * Licensed under the MIT (http://www.opensource.org/licenses/mit-license.php) license.
 */
(function () {
    function S(h, o) {
        for (var p = h.length / o, m = 0, k = p, b = 0, i = []; m < h.length;) {
            k--, 0 > k ? (b += h[m] * (1 + k), i.push(b / p), b = h[m++] * -k, k +=
                                                                               p)
                : b += 1 * h[m++];
        }
        return i
    }

    function E(h, o, p, m, k, b, i, c) {
        var F, f, u, w;

        function J(a) {
            for (var s = [], e = 0, G = b.length; e < G; e++) {
                s = s.concat(b[e]);
            }
            s.sort(function (a, e) {
                return a - e
            });
            for (var c = [], g = [], e = 0, G = s.length; e < G; e++) {
                s[e] != s[e - 1] && c.push(s[e]) && g.push(o + d + (s[e] - v) * A);
            }
            for (var s = c, G = s.length, l = a || h.set(), e = 0; e < G; e++) {
                var c = g[e] - (g[e] - (g[e - 1] || o)) / 2, f = ((g[e + 1] || o + m) - g[e]) / 2 + (g[e] - (g[e -
                                                                                                               1] || o)) / 2, j;
                a ? j = {} : l.push(j = h.rect(c - 1, p, Math.max(f + 1, 1), k).attr({stroke: "none", fill: "#000", opacity: 0}));
                j.values = [];
                j.symbols = h.set();
                j.y = [];
                j.x = g[e];
                j.axis = s[e];
                for (var f = 0, r = i.length; f < r; f++) {
                    for (var c = b[f] || b[0], n = 0, u = c.length; n < u; n++) {
                        c[n] == s[e] &&
                        (j.values.push(i[f][n]), j.y.push(p +
                                                          k -
                                                          d -
                                                          (i[f][n] -
                                                           y) *
                                                          H), j.symbols.push(q.symbols[f][n]));
                    }
                }
                a && a.call(j)
            }
            !a && (t = l)
        }

        function N(a) {
            for (var g = a || h.set(), e, c = 0, j = i.length; c < j; c++) {
                for (var f = 0, m = i[c].length; f < m; f++) {
                    var l = o + d + ((b[c] || b[0])[f] - v) * A, n = o + d + ((b[c] ||
                                                                               b[0])[f ? f - 1 : 1] - v) * A, r = p + k - d -
                                                                                                                  (i[c][f] - y) * H;
                    a ? e = {} : g.push(e = h.circle(l, r, Math.abs(n - l) / 2).attr({stroke: "#000", fill: "#000", opacity: 1}));
                    e.x = l;
                    e.y = r;
                    e.value = i[c][f];
                    e.line = q.lines[c];
                    e.shade = q.shades[c];
                    e.symbol = q.symbols[c][f];
                    e.symbols = q.symbols[c];
                    e.axis = (b[c] || b[0])[f];
                    a && a.call(e)
                }
            }
            !a && (C = g)
        }

        c = c || {};
        h.raphael.is(b[0], "array") || (b = [b]);
        h.raphael.is(i[0], "array") || (i = [i]);
        for (var d = c.gutter || 10, l = Math.max(b[0].length, i[0].length), O = c.symbol || "", P = c.colors ||
                                                                                                     this.colors, t = null, C = null, q = h.set(), g =
            [], a =
            0, n = i.length; a < n; a++) {
            l = Math.max(l, i[a].length);
        }
        for (var K = h.set(), a = 0, n = i.length; a < n; a++) {
            c.shade &&
            K.push(h.path().attr({stroke: "none", fill: P[a], opacity: c.nostroke ? 1
                : 0.3})), i[a].length > m - 2 * d &&
                          (i[a] = S(i[a], m - 2 * d), l = m - 2 * d), b[a] &&
                                                                      b[a].length >
                                                                      m - 2 * d && (b[a] =
                                                                                    S(b[a],
                                                                                        m -
                                                                                        2 *
                                                                                        d));
        }
        var g = Array.prototype.concat.apply([], b), l = Array.prototype.concat.apply([], i), g = this.snapEnds(Math.min.apply(Math, g),
            Math.max.apply(Math, g), b[0].length - 1), v = g.from, g = g.to, l = this.snapEnds(Math.min.apply(Math, l),
            Math.max.apply(Math, l), i[0].length -
                                     1), y = l.from, a = l.to, A = (m - 2 * d) / (g - v || 1), H = (k - 2 * d) / (a - y || 1), l = h.set();
        c.axis && (n = (c.axis + "").split(/[,\s]+/), +n[0] && l.push(this.axis(o + d, p + d, m - 2 * d, v, g,
            c.axisxstep || Math.floor((m - 2 * d) / 20), 2, h)), +n[1] && l.push(this.axis(o + m - d, p + k - d, k - 2 * d, y, a,
            c.axisystep || Math.floor((k - 2 * d) / 20), 3, h)), +n[2] && l.push(this.axis(o + d, p + k - d, m - 2 * d, v, g,
            c.axisxstep || Math.floor((m - 2 * d) / 20), 0, h)), +n[3] && l.push(this.axis(o + d, p + k - d, k - 2 * d, y, a,
            c.axisystep || Math.floor((k - 2 * d) / 20), 1, h)));
        for (var Q = h.set(), R = h.set(), E, a = 0, n = i.length; a < n; a++) {
            c.nostroke ||
            Q.push(E = h.path().attr({stroke: P[a], "stroke-width": c.width ||
                                                                    2, "stroke-linejoin": "round", "stroke-linecap": "round", "stroke-dasharray": c.dash ||
                                                                                                                                                  ""}));
            for (var D = Raphael.is(O, "array") ? O[a] : O, I = h.set(), g = [], j = 0, T = i[a].length; j < T; j++) {
                var x = o + d + ((b[a] || b[0])[j] - v) * A, z = p + k - d - (i[a][j] - y) * H;
                (Raphael.is(D, "array") ? D[j] : D) &&
                I.push(h[Raphael.is(D, "array") ? D[j] : D](x, z, 3 * (c.width || 2)).attr({fill: P[a], stroke: "none"}));
                if (c.smooth) {
                    if (j && j != T - 1) {
                        f = o + d + ((b[a] || b[0])[j - 1] - v) * A;
                        var L = p + k - d - (i[a][j - 1] - y) * H;
                        u = x;
                        w = z;
                        var r =
                            o + d + ((b[a] || b[0])[j + 1] - v) * A, B = p + k - d - (i[a][j + 1] - y) * H, M = (u - f) / 2;
                        F = (r - u) / 2;
                        f = Math.atan((u - f) / Math.abs(w - L));
                        r = Math.atan((r - u) / Math.abs(w - B));
                        f = L < w ? Math.PI - f : f;
                        r = B < w ? Math.PI - r : r;
                        B = Math.PI / 2 - (f + r) % (2 * Math.PI) / 2;
                        L = M * Math.sin(B + f);
                        f = M * Math.cos(B + f);
                        M = F * Math.sin(B + r);
                        r = F * Math.cos(B + r);
                        F = u - L;
                        f = w + f;
                        u += M;
                        w += r;
                        g = g.concat([F, f, x, z, u, w])
                    }
                    j || (g = ["M", x, z, "C", x, z])
                } else {
                    g = g.concat([j ? "L" : "M", x, z])
                }
            }
            c.smooth && (g = g.concat([x, z, x, z]));
            R.push(I);
            c.shade && K[a].attr({path: g.concat(["L", x, p + k - d, "L", o + d + ((b[a] || b[0])[0] - v) *
                                                                                  A, p + k - d, "z"]).join(",")});
            !c.nostroke && E.attr({path: g.join(",")})
        }
        q.push(Q, K, R, l, t, C);
        q.lines = Q;
        q.shades = K;
        q.symbols = R;
        q.axis = l;
        q.hoverColumn = function (a, c) {
            !t && J();
            t.mouseover(a).mouseout(c);
            return this
        };
        q.clickColumn = function (a) {
            !t && J();
            t.click(a);
            return this
        };
        q.hrefColumn = function (a) {
            var c = h.raphael.is(arguments[0], "array") ? arguments[0] : arguments;
            if (!(arguments.length - 1) && typeof a == "object") {
                for (var e in a) {
                    for (var b = 0, d = t.length; b < d; b++) {
                        t[b].axis == e &&
                        t[b].attr("href",
                            a[e]);
                    }
                }
            }
            !t && J();
            b = 0;
            for (d = c.length; b <
                               d; b++) {
                t[b] && t[b].attr("href", c[b]);
            }
            return this
        };
        q.hover = function (a, b) {
            !C && N();
            C.mouseover(a).mouseout(b);
            return this
        };
        q.click = function (a) {
            !C && N();
            C.click(a);
            return this
        };
        q.each = function (a) {
            N(a);
            return this
        };
        q.eachColumn = function (a) {
            J(a);
            return this
        };
        return q
    }

    var I = function () {
    };
    I.prototype = Raphael.g;
    E.prototype = new I;
    Raphael.fn.linechart = function (h, o, p, m, k, b, i) {
        return new E(this, h, o, p, m, k, b, i)
    }
})();
