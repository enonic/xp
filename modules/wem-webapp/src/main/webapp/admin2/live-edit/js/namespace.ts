/* Test */

/**
 * Copyright (c) 2011 Tapasvi Moturu, http://code.google.com/p/namespace-js/
 *
 *      This software consists of voluntary contributions made by many
 *      individuals (AUTHORS.txt, http://code.google.com/p/namespace-js/) For exact
 *      contribution history, see the revision history.
 *
 *      Permission is hereby granted, free of charge, to any person obtaining
 *      a copy of this software and associated documentation files (the
 *      "Software"), to deal in the Software without restriction, including
 *      without limitation the rights to use, copy, modify, merge, publish,
 *      distribute, sublicense, and/or sell copies of the Software, and to
 *      permit persons to whom the Software is furnished to do so, subject to
 *      the following conditions:
 *
 *      The above copyright notice and this permission notice shall be
 *      included in all copies or substantial portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *      EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *      MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *      NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *      LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *      OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *      WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


//Creating a system namespace under global.
//This will ensure you will always have this function available to you

var AdminLiveEdit = AdminLiveEdit || function () {
};
AdminLiveEdit.namespace = AdminLiveEdit.namespace || function () {
};
AdminLiveEdit.namespace.prototype.constructor = new AdminLiveEdit();
AdminLiveEdit.namespace.useNamespace = function (namespace, container) {
    if (namespace === undefined || namespace === '') {
        return;
    }
    var separator = '.';
    var ns = namespace.split(separator);
    var o = container || window;
    var i;
    var len;

    if (ns.length > 0) {
        o[ns[0]] = o[ns[0]] || function () {
        };
        if (o[ns[0]].prototype.constructor == undefined) {
            o[ns[0]].prototype.constructor = new o();
        }
    }

    var remainingNs = '';
    for (i = 1, len = ns.length; i < len; i++) {
        if (i === 1) {
            remainingNs = ns[1];
        } else {
            remainingNs = remainingNs + separator + ns[i];
        }
    }
    return AdminLiveEdit.namespace.useNamespace(remainingNs, o[ns[0]]);
};