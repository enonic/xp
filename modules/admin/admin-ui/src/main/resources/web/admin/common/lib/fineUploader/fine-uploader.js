/*!
 * Fine Uploader
 *
 * Copyright 2013-present, Widen Enterprises, Inc.
 *
 * Version: 5.10.0
 *
 * Homepage: http://fineuploader.com
 *
 * Repository: git://github.com/FineUploader/fine-uploader.git
 *
 * Licensed only under the MIT license (http://fineuploader.com/licensing).
 */
/*!
 * Enonic changes
 * 07.07.2016 @sts Adjusted reset method to make it able to clean the created input that triggers file select dialog
 * 07.07.2016 @sts Adjusted isErrorUploadResponse method so that it does not treat absence of success field on our responses as error
 * 07.07.2016 @sts Added getInputButton() method that returns first input button of uploader form _buttons array
 * 07.07.2016 @sts Added onDrop, onDragEnter and onDragLeave callbacks for dropzone
 * 07.07.2016 @sts Adjusted uploader's input overflow property
 * 13.07.2016 @sts Added debug option for a draganddrop
 * 13.07.2016 @sts Removed qq.Scaler, qq.FineUploader, qq.Templating and their dependant classes
 */

(function (global) {
    /*globals window, navigator, document, FormData, File, HTMLInputElement, XMLHttpRequest, Blob, Storage, ActiveXObject */
    /* jshint -W079 */
    var qq = function (element) {
        "use strict";

        return {
            hide: function () {
                element.style.display = "none";
                return this;
            },

            /** Returns the function which detaches attached event */
            attach: function (type, fn) {
                if (element.addEventListener) {
                    element.addEventListener(type, fn, false);
                } else if (element.attachEvent) {
                    element.attachEvent("on" + type, fn);
                }
                return function () {
                    qq(element).detach(type, fn);
                };
            },

            detach: function (type, fn) {
                if (element.removeEventListener) {
                    element.removeEventListener(type, fn, false);
                } else if (element.attachEvent) {
                    element.detachEvent("on" + type, fn);
                }
                return this;
            },

            contains: function (descendant) {
                // The [W3C spec](http://www.w3.org/TR/domcore/#dom-node-contains)
                // says a `null` (or ostensibly `undefined`) parameter
                // passed into `Node.contains` should result in a false return value.
                // IE7 throws an exception if the parameter is `undefined` though.
                if (!descendant) {
                    return false;
                }

                // compareposition returns false in this case
                if (element === descendant) {
                    return true;
                }

                if (element.contains) {
                    return element.contains(descendant);
                } else {
                    /*jslint bitwise: true*/
                    return !!(descendant.compareDocumentPosition(element) & 8);
                }
            },

            /**
             * Insert this element before elementB.
             */
            insertBefore: function (elementB) {
                elementB.parentNode.insertBefore(element, elementB);
                return this;
            },

            remove: function () {
                element.parentNode.removeChild(element);
                return this;
            },

            /**
             * Sets styles for an element.
             * Fixes opacity in IE6-8.
             */
            css: function (styles) {
                /*jshint eqnull: true*/
                if (element.style == null) {
                    throw new qq.Error("Can't apply style to node as it is not on the HTMLElement prototype chain!");
                }

                /*jshint -W116*/
                if (styles.opacity != null) {
                    if (typeof element.style.opacity !== "string" && typeof (element.filters) !== "undefined") {
                        styles.filter = "alpha(opacity=" + Math.round(100 * styles.opacity) + ")";
                    }
                }
                qq.extend(element.style, styles);

                return this;
            },

            hasClass: function (name, considerParent) {
                var re = new RegExp("(^| )" + name + "( |$)");
                return re.test(element.className) || !!(considerParent && re.test(element.parentNode.className));
            },

            addClass: function (name) {
                if (!qq(element).hasClass(name)) {
                    element.className += " " + name;
                }
                return this;
            },

            removeClass: function (name) {
                var re = new RegExp("(^| )" + name + "( |$)");
                element.className = element.className.replace(re, " ").replace(/^\s+|\s+$/g, "");
                return this;
            },

            getByClass: function (className, first) {
                var candidates,
                    result = [];

                if (first && element.querySelector) {
                    return element.querySelector("." + className);
                }
                else if (element.querySelectorAll) {
                    return element.querySelectorAll("." + className);
                }

                candidates = element.getElementsByTagName("*");

                qq.each(candidates, function (idx, val) {
                    if (qq(val).hasClass(className)) {
                        result.push(val);
                    }
                });
                return first ? result[0] : result;
            },

            getFirstByClass: function (className) {
                return qq(element).getByClass(className, true);
            },

            children: function () {
                var children = [],
                    child = element.firstChild;

                while (child) {
                    if (child.nodeType === 1) {
                        children.push(child);
                    }
                    child = child.nextSibling;
                }

                return children;
            },

            setText: function (text) {
                element.innerText = text;
                element.textContent = text;
                return this;
            },

            clearText: function () {
                return qq(element).setText("");
            },

            // Returns true if the attribute exists on the element
            // AND the value of the attribute is NOT "false" (case-insensitive)
            hasAttribute: function (attrName) {
                var attrVal;

                if (element.hasAttribute) {

                    if (!element.hasAttribute(attrName)) {
                        return false;
                    }

                    /*jshint -W116*/
                    return (/^false$/i).exec(element.getAttribute(attrName)) == null;
                }
                else {
                    attrVal = element[attrName];

                    if (attrVal === undefined) {
                        return false;
                    }

                    /*jshint -W116*/
                    return (/^false$/i).exec(attrVal) == null;
                }
            }
        };
    };

    (function () {
        "use strict";

        qq.canvasToBlob = function (canvas, mime, quality) {
            return qq.dataUriToBlob(canvas.toDataURL(mime, quality));
        };

        qq.dataUriToBlob = function (dataUri) {
            var arrayBuffer, byteString,
                createBlob = function (data, mime) {
                    var BlobBuilder = window.BlobBuilder ||
                                      window.WebKitBlobBuilder ||
                                      window.MozBlobBuilder ||
                                      window.MSBlobBuilder,
                        blobBuilder = BlobBuilder && new BlobBuilder();

                    if (blobBuilder) {
                        blobBuilder.append(data);
                        return blobBuilder.getBlob(mime);
                    }
                    else {
                        return new Blob([data], {type: mime});
                    }
                },
                intArray, mimeString;

            // convert base64 to raw binary data held in a string
            if (dataUri.split(",")[0].indexOf("base64") >= 0) {
                byteString = atob(dataUri.split(",")[1]);
            }
            else {
                byteString = decodeURI(dataUri.split(",")[1]);
            }

            // extract the MIME
            mimeString = dataUri.split(",")[0]
                .split(":")[1]
                .split(";")[0];

            // write the bytes of the binary string to an ArrayBuffer
            arrayBuffer = new ArrayBuffer(byteString.length);
            intArray = new Uint8Array(arrayBuffer);
            qq.each(byteString, function (idx, character) {
                intArray[idx] = character.charCodeAt(0);
            });

            return createBlob(arrayBuffer, mimeString);
        };

        qq.log = function (message, level) {
            if (window.console) {
                if (!level || level === "info") {
                    window.console.log(message);
                }
                else {
                    if (window.console[level]) {
                        window.console[level](message);
                    }
                    else {
                        window.console.log("<" + level + "> " + message);
                    }
                }
            }
        };

        qq.isObject = function (variable) {
            return variable && !variable.nodeType && Object.prototype.toString.call(variable) === "[object Object]";
        };

        qq.isFunction = function (variable) {
            return typeof (variable) === "function";
        };

        /**
         * Check the type of a value.  Is it an "array"?
         *
         * @param value value to test.
         * @returns true if the value is an array or associated with an `ArrayBuffer`
         */
        qq.isArray = function (value) {
            return Object.prototype.toString.call(value) === "[object Array]" ||
                   (value && window.ArrayBuffer && value.buffer && value.buffer.constructor === ArrayBuffer);
        };

        // Looks for an object on a `DataTransfer` object that is associated with drop events when utilizing the Filesystem API.
        qq.isItemList = function (maybeItemList) {
            return Object.prototype.toString.call(maybeItemList) === "[object DataTransferItemList]";
        };

        // Looks for an object on a `NodeList` or an `HTMLCollection`|`HTMLFormElement`|`HTMLSelectElement`
        // object that is associated with collections of Nodes.
        qq.isNodeList = function (maybeNodeList) {
            return Object.prototype.toString.call(maybeNodeList) === "[object NodeList]" ||
                   // If `HTMLCollection` is the actual type of the object, we must determine this
                   // by checking for expected properties/methods on the object
                   (maybeNodeList.item && maybeNodeList.namedItem);
        };

        qq.isString = function (maybeString) {
            return Object.prototype.toString.call(maybeString) === "[object String]";
        };

        qq.trimStr = function (string) {
            if (String.prototype.trim) {
                return string.trim();
            }

            return string.replace(/^\s+|\s+$/g, "");
        };

        /**
         * @param str String to format.
         * @returns {string} A string, swapping argument values with the associated occurrence of {} in the passed string.
         */
        qq.format = function (str) {

            var args = Array.prototype.slice.call(arguments, 1),
                newStr = str,
                nextIdxToReplace = newStr.indexOf("{}");

            qq.each(args, function (idx, val) {
                var strBefore = newStr.substring(0, nextIdxToReplace),
                    strAfter = newStr.substring(nextIdxToReplace + 2);

                newStr = strBefore + val + strAfter;
                nextIdxToReplace = newStr.indexOf("{}", nextIdxToReplace + val.length);

                // End the loop if we have run out of tokens (when the arguments exceed the # of tokens)
                if (nextIdxToReplace < 0) {
                    return false;
                }
            });

            return newStr;
        };

        qq.isFile = function (maybeFile) {
            return window.File && Object.prototype.toString.call(maybeFile) === "[object File]";
        };

        qq.isFileList = function (maybeFileList) {
            return window.FileList && Object.prototype.toString.call(maybeFileList) === "[object FileList]";
        };

        qq.isFileOrInput = function (maybeFileOrInput) {
            return qq.isFile(maybeFileOrInput) || qq.isInput(maybeFileOrInput);
        };

        qq.isInput = function (maybeInput, notFile) {
            var evaluateType = function (type) {
                var normalizedType = type.toLowerCase();

                if (notFile) {
                    return normalizedType !== "file";
                }

                return normalizedType === "file";
            };

            if (window.HTMLInputElement) {
                if (Object.prototype.toString.call(maybeInput) === "[object HTMLInputElement]") {
                    if (maybeInput.type && evaluateType(maybeInput.type)) {
                        return true;
                    }
                }
            }
            if (maybeInput.tagName) {
                if (maybeInput.tagName.toLowerCase() === "input") {
                    if (maybeInput.type && evaluateType(maybeInput.type)) {
                        return true;
                    }
                }
            }

            return false;
        };

        qq.isBlob = function (maybeBlob) {
            if (window.Blob && Object.prototype.toString.call(maybeBlob) === "[object Blob]") {
                return true;
            }
        };

        qq.isXhrUploadSupported = function () {
            var input = document.createElement("input");
            input.type = "file";

            return (
            input.multiple !== undefined &&
            typeof File !== "undefined" &&
            typeof FormData !== "undefined" &&
            typeof (qq.createXhrInstance()).upload !== "undefined");
        };

        // Fall back to ActiveX is native XHR is disabled (possible in any version of IE).
        qq.createXhrInstance = function () {
            if (window.XMLHttpRequest) {
                return new XMLHttpRequest();
            }

            try {
                return new ActiveXObject("MSXML2.XMLHTTP.3.0");
            }
            catch (error) {
                qq.log("Neither XHR or ActiveX are supported!", "error");
                return null;
            }
        };

        qq.isFolderDropSupported = function (dataTransfer) {
            return dataTransfer.items &&
                   dataTransfer.items.length > 0 &&
                   dataTransfer.items[0].webkitGetAsEntry;
        };

        qq.isFileChunkingSupported = function () {
            return !qq.androidStock() && //Android's stock browser cannot upload Blobs correctly
                   qq.isXhrUploadSupported() &&
                   (File.prototype.slice !== undefined || File.prototype.webkitSlice !== undefined ||
                    File.prototype.mozSlice !== undefined);
        };

        qq.sliceBlob = function (fileOrBlob, start, end) {
            var slicer = fileOrBlob.slice || fileOrBlob.mozSlice || fileOrBlob.webkitSlice;

            return slicer.call(fileOrBlob, start, end);
        };

        qq.arrayBufferToHex = function (buffer) {
            var bytesAsHex = "",
                bytes = new Uint8Array(buffer);

            qq.each(bytes, function (idx, byt) {
                var byteAsHexStr = byt.toString(16);

                if (byteAsHexStr.length < 2) {
                    byteAsHexStr = "0" + byteAsHexStr;
                }

                bytesAsHex += byteAsHexStr;
            });

            return bytesAsHex;
        };

        qq.readBlobToHex = function (blob, startOffset, length) {
            var initialBlob = qq.sliceBlob(blob, startOffset, startOffset + length),
                fileReader = new FileReader(),
                promise = new qq.Promise();

            fileReader.onload = function () {
                promise.success(qq.arrayBufferToHex(fileReader.result));
            };

            fileReader.onerror = promise.failure;

            fileReader.readAsArrayBuffer(initialBlob);

            return promise;
        };

        qq.extend = function (first, second, extendNested) {
            qq.each(second, function (prop, val) {
                if (extendNested && qq.isObject(val)) {
                    if (first[prop] === undefined) {
                        first[prop] = {};
                    }
                    qq.extend(first[prop], val, true);
                }
                else {
                    first[prop] = val;
                }
            });

            return first;
        };

        /**
         * Allow properties in one object to override properties in another,
         * keeping track of the original values from the target object.
         *
         * Note that the pre-overriden properties to be overriden by the source will be passed into the `sourceFn` when it is invoked.
         *
         * @param target Update properties in this object from some source
         * @param sourceFn A function that, when invoked, will return properties that will replace properties with the same name in the target.
         * @returns {object} The target object
         */
        qq.override = function (target, sourceFn) {
            var super_ = {},
                source = sourceFn(super_);

            qq.each(source, function (srcPropName, srcPropVal) {
                if (target[srcPropName] !== undefined) {
                    super_[srcPropName] = target[srcPropName];
                }

                target[srcPropName] = srcPropVal;
            });

            return target;
        };

        /**
         * Searches for a given element (elt) in the array, returns -1 if it is not present.
         */
        qq.indexOf = function (arr, elt, from) {
            if (arr.indexOf) {
                return arr.indexOf(elt, from);
            }

            from = from || 0;
            var len = arr.length;

            if (from < 0) {
                from += len;
            }

            for (; from < len; from += 1) {
                if (arr.hasOwnProperty(from) && arr[from] === elt) {
                    return from;
                }
            }
            return -1;
        };

        //this is a version 4 UUID
        qq.getUniqueId = function () {
            return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
                /*jslint eqeq: true, bitwise: true*/
                var r = Math.random() * 16 | 0, v = c == "x" ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        };

        //
        // Browsers and platforms detection
        qq.ie = function () {
            return navigator.userAgent.indexOf("MSIE") !== -1 ||
                   navigator.userAgent.indexOf("Trident") !== -1;
        };

        qq.ie7 = function () {
            return navigator.userAgent.indexOf("MSIE 7") !== -1;
        };

        qq.ie8 = function () {
            return navigator.userAgent.indexOf("MSIE 8") !== -1;
        };

        qq.ie10 = function () {
            return navigator.userAgent.indexOf("MSIE 10") !== -1;
        };

        qq.ie11 = function () {
            return qq.ie() && navigator.userAgent.indexOf("rv:11") !== -1;
        };

        qq.edge = function () {
            return navigator.userAgent.indexOf("Edge") >= 0;
        };

        qq.safari = function () {
            return navigator.vendor !== undefined && navigator.vendor.indexOf("Apple") !== -1;
        };

        qq.chrome = function () {
            return navigator.vendor !== undefined && navigator.vendor.indexOf("Google") !== -1;
        };

        qq.opera = function () {
            return navigator.vendor !== undefined && navigator.vendor.indexOf("Opera") !== -1;
        };

        qq.firefox = function () {
            return (!qq.edge() && !qq.ie11() && navigator.userAgent.indexOf("Mozilla") !== -1 && navigator.vendor !== undefined &&
                    navigator.vendor === "");
        };

        qq.windows = function () {
            return navigator.platform === "Win32";
        };

        qq.android = function () {
            return navigator.userAgent.toLowerCase().indexOf("android") !== -1;
        };

        // We need to identify the Android stock browser via the UA string to work around various bugs in this browser,
        // such as the one that prevents a `Blob` from being uploaded.
        qq.androidStock = function () {
            return qq.android() && navigator.userAgent.toLowerCase().indexOf("chrome") < 0;
        };

        qq.ios6 = function () {
            return qq.ios() && navigator.userAgent.indexOf(" OS 6_") !== -1;
        };

        qq.ios7 = function () {
            return qq.ios() && navigator.userAgent.indexOf(" OS 7_") !== -1;
        };

        qq.ios8 = function () {
            return qq.ios() && navigator.userAgent.indexOf(" OS 8_") !== -1;
        };

        // iOS 8.0.0
        qq.ios800 = function () {
            return qq.ios() && navigator.userAgent.indexOf(" OS 8_0 ") !== -1;
        };

        qq.ios = function () {
            /*jshint -W014 */
            return navigator.userAgent.indexOf("iPad") !== -1
                   || navigator.userAgent.indexOf("iPod") !== -1
                   || navigator.userAgent.indexOf("iPhone") !== -1;
        };

        qq.iosChrome = function () {
            return qq.ios() && navigator.userAgent.indexOf("CriOS") !== -1;
        };

        qq.iosSafari = function () {
            return qq.ios() && !qq.iosChrome() && navigator.userAgent.indexOf("Safari") !== -1;
        };

        qq.iosSafariWebView = function () {
            return qq.ios() && !qq.iosChrome() && !qq.iosSafari();
        };

        //
        // Events

        qq.preventDefault = function (e) {
            if (e.preventDefault) {
                e.preventDefault();
            } else {
                e.returnValue = false;
            }
        };

        /**
         * Creates and returns element from html string
         * Uses innerHTML to create an element
         */
        qq.toElement = (function () {
            var div = document.createElement("div");
            return function (html) {
                div.innerHTML = html;
                var element = div.firstChild;
                div.removeChild(element);
                return element;
            };
        }());

        //key and value are passed to callback for each entry in the iterable item
        qq.each = function (iterableItem, callback) {
            var keyOrIndex, retVal;

            if (iterableItem) {
                // Iterate through [`Storage`](http://www.w3.org/TR/webstorage/#the-storage-interface) items
                if (window.Storage && iterableItem.constructor === window.Storage) {
                    for (keyOrIndex = 0; keyOrIndex < iterableItem.length; keyOrIndex++) {
                        retVal = callback(iterableItem.key(keyOrIndex), iterableItem.getItem(iterableItem.key(keyOrIndex)));
                        if (retVal === false) {
                            break;
                        }
                    }
                }
                // `DataTransferItemList` & `NodeList` objects are array-like and should be treated as arrays
                // when iterating over items inside the object.
                else if (qq.isArray(iterableItem) || qq.isItemList(iterableItem) || qq.isNodeList(iterableItem)) {
                    for (keyOrIndex = 0; keyOrIndex < iterableItem.length; keyOrIndex++) {
                        retVal = callback(keyOrIndex, iterableItem[keyOrIndex]);
                        if (retVal === false) {
                            break;
                        }
                    }
                }
                else if (qq.isString(iterableItem)) {
                    for (keyOrIndex = 0; keyOrIndex < iterableItem.length; keyOrIndex++) {
                        retVal = callback(keyOrIndex, iterableItem.charAt(keyOrIndex));
                        if (retVal === false) {
                            break;
                        }
                    }
                }
                else {
                    for (keyOrIndex in iterableItem) {
                        if (Object.prototype.hasOwnProperty.call(iterableItem, keyOrIndex)) {
                            retVal = callback(keyOrIndex, iterableItem[keyOrIndex]);
                            if (retVal === false) {
                                break;
                            }
                        }
                    }
                }
            }
        };

        //include any args that should be passed to the new function after the context arg
        qq.bind = function (oldFunc, context) {
            if (qq.isFunction(oldFunc)) {
                var args = Array.prototype.slice.call(arguments, 2);

                return function () {
                    var newArgs = qq.extend([], args);
                    if (arguments.length) {
                        newArgs = newArgs.concat(Array.prototype.slice.call(arguments));
                    }
                    return oldFunc.apply(context, newArgs);
                };
            }

            throw new Error("first parameter must be a function!");
        };

        /**
         * obj2url() takes a json-object as argument and generates
         * a querystring. pretty much like jQuery.param()
         *
         * how to use:
         *
         *    `qq.obj2url({a:'b',c:'d'},'http://any.url/upload?otherParam=value');`
         *
         * will result in:
         *
         *    `http://any.url/upload?otherParam=value&a=b&c=d`
         *
         * @param  Object JSON-Object
         * @param  String current querystring-part
         * @return String encoded querystring
         */
        qq.obj2url = function (obj, temp, prefixDone) {
            /*jshint laxbreak: true*/
            var uristrings = [],
                prefix = "&",
                add = function (nextObj, i) {
                    var nextTemp = temp
                        ? (/\[\]$/.test(temp)) // prevent double-encoding
                                       ? temp
                                       : temp + "[" + i + "]"
                        : i;
                    if ((nextTemp !== "undefined") && (i !== "undefined")) {
                        uristrings.push(
                            (typeof nextObj === "object")
                                ? qq.obj2url(nextObj, nextTemp, true)
                                : (Object.prototype.toString.call(nextObj) === "[object Function]")
                                ? encodeURIComponent(nextTemp) + "=" + encodeURIComponent(nextObj())
                                : encodeURIComponent(nextTemp) + "=" + encodeURIComponent(nextObj)
                        );
                    }
                };

            if (!prefixDone && temp) {
                prefix = (/\?/.test(temp)) ? (/\?$/.test(temp)) ? "" : "&" : "?";
                uristrings.push(temp);
                uristrings.push(qq.obj2url(obj));
            } else if ((Object.prototype.toString.call(obj) === "[object Array]") && (typeof obj !== "undefined")) {
                qq.each(obj, function (idx, val) {
                    add(val, idx);
                });
            } else if ((typeof obj !== "undefined") && (obj !== null) && (typeof obj === "object")) {
                qq.each(obj, function (prop, val) {
                    add(val, prop);
                });
            } else {
                uristrings.push(encodeURIComponent(temp) + "=" + encodeURIComponent(obj));
            }

            if (temp) {
                return uristrings.join(prefix);
            } else {
                return uristrings.join(prefix)
                    .replace(/^&/, "")
                    .replace(/%20/g, "+");
            }
        };

        qq.obj2FormData = function (obj, formData, arrayKeyName) {
            if (!formData) {
                formData = new FormData();
            }

            qq.each(obj, function (key, val) {
                key = arrayKeyName ? arrayKeyName + "[" + key + "]" : key;

                if (qq.isObject(val)) {
                    qq.obj2FormData(val, formData, key);
                }
                else if (qq.isFunction(val)) {
                    formData.append(key, val());
                }
                else {
                    formData.append(key, val);
                }
            });

            return formData;
        };

        qq.obj2Inputs = function (obj, form) {
            var input;

            if (!form) {
                form = document.createElement("form");
            }

            qq.obj2FormData(obj, {
                append: function (key, val) {
                    input = document.createElement("input");
                    input.setAttribute("name", key);
                    input.setAttribute("value", val);
                    form.appendChild(input);
                }
            });

            return form;
        };

        /**
         * Not recommended for use outside of Fine Uploader since this falls back to an unchecked eval if JSON.parse is not
         * implemented.  For a more secure JSON.parse polyfill, use Douglas Crockford's json2.js.
         */
        qq.parseJson = function (json) {
            /*jshint evil: true*/
            if (window.JSON && qq.isFunction(JSON.parse)) {
                return JSON.parse(json);
            } else {
                return eval("(" + json + ")");
            }
        };

        /**
         * Retrieve the extension of a file, if it exists.
         *
         * @param filename
         * @returns {string || undefined}
         */
        qq.getExtension = function (filename) {
            var extIdx = filename.lastIndexOf(".") + 1;

            if (extIdx > 0) {
                return filename.substr(extIdx, filename.length - extIdx);
            }
        };

        qq.getFilename = function (blobOrFileInput) {
            /*jslint regexp: true*/

            if (qq.isInput(blobOrFileInput)) {
                // get input value and remove path to normalize
                return blobOrFileInput.value.replace(/.*(\/|\\)/, "");
            }
            else if (qq.isFile(blobOrFileInput)) {
                if (blobOrFileInput.fileName !== null && blobOrFileInput.fileName !== undefined) {
                    return blobOrFileInput.fileName;
                }
            }

            return blobOrFileInput.name;
        };

        /**
         * A generic module which supports object disposing in dispose() method.
         * */
        qq.DisposeSupport = function () {
            var disposers = [];

            return {
                /** Run all registered disposers */
                dispose: function () {
                    var disposer;
                    do {
                        disposer = disposers.shift();
                        if (disposer) {
                            disposer();
                        }
                    }
                    while (disposer);
                },

                /** Attach event handler and register de-attacher as a disposer */
                attach: function () {
                    var args = arguments;
                    /*jslint undef:true*/
                    this.addDisposer(qq(args[0]).attach.apply(this, Array.prototype.slice.call(arguments, 1)));
                },

                /** Add disposer to the collection */
                addDisposer: function (disposeFunction) {
                    disposers.push(disposeFunction);
                }
            };
        };
    }());

    /* globals qq */
    /**
     * Fine Uploader top-level Error container.  Inherits from `Error`.
     */
    (function () {
        "use strict";

        qq.Error = function (message) {
            this.message = "[Fine Uploader " + qq.version + "] " + message;
        };

        qq.Error.prototype = new Error();
    }());

    /*global qq */
    qq.version = "5.10.0";

    /* globals qq */
    qq.supportedFeatures = (function () {
        "use strict";

        var supportsUploading,
            supportsUploadingBlobs,
            supportsFileDrop,
            supportsAjaxFileUploading,
            supportsFolderDrop,
            supportsChunking,
            supportsResume,
            supportsUploadViaPaste,
            supportsUploadCors,
            supportsDeleteFileXdr,
            supportsDeleteFileCorsXhr,
            supportsDeleteFileCors,
            supportsFolderSelection,
            supportsImagePreviews,
            supportsUploadProgress;

        function testSupportsFileInputElement() {
            var supported = true,
                tempInput;

            try {
                tempInput = document.createElement("input");
                tempInput.type = "file";
                qq(tempInput).hide();

                if (tempInput.disabled) {
                    supported = false;
                }
            }
            catch (ex) {
                supported = false;
            }

            return supported;
        }

        //only way to test for Filesystem API support since webkit does not expose the DataTransfer interface
        function isChrome21OrHigher() {
            return (qq.chrome() || qq.opera()) &&
                   navigator.userAgent.match(/Chrome\/[2][1-9]|Chrome\/[3-9][0-9]/) !== undefined;
        }

        //only way to test for complete Clipboard API support at this time
        function isChrome14OrHigher() {
            return (qq.chrome() || qq.opera()) &&
                   navigator.userAgent.match(/Chrome\/[1][4-9]|Chrome\/[2-9][0-9]/) !== undefined;
        }

        //Ensure we can send cross-origin `XMLHttpRequest`s
        function isCrossOriginXhrSupported() {
            if (window.XMLHttpRequest) {
                var xhr = qq.createXhrInstance();

                //Commonly accepted test for XHR CORS support.
                return xhr.withCredentials !== undefined;
            }

            return false;
        }

        //Test for (terrible) cross-origin ajax transport fallback for IE9 and IE8
        function isXdrSupported() {
            return window.XDomainRequest !== undefined;
        }

        // CORS Ajax requests are supported if it is either possible to send credentialed `XMLHttpRequest`s,
        // or if `XDomainRequest` is an available alternative.
        function isCrossOriginAjaxSupported() {
            if (isCrossOriginXhrSupported()) {
                return true;
            }

            return isXdrSupported();
        }

        function isFolderSelectionSupported() {
            // We know that folder selection is only supported in Chrome via this proprietary attribute for now
            return document.createElement("input").webkitdirectory !== undefined;
        }

        function isLocalStorageSupported() {
            try {
                return !!window.localStorage &&
                       // unpatched versions of IE10/11 have buggy impls of localStorage where setItem is a string
                       qq.isFunction(window.localStorage.setItem);
            }
            catch (error) {
                // probably caught a security exception, so no localStorage for you
                return false;
            }
        }

        function isDragAndDropSupported() {
            var span = document.createElement("span");

            return ("draggable" in span || ("ondragstart" in span && "ondrop" in span)) && !qq.android() && !qq.ios();
        }

        supportsUploading = testSupportsFileInputElement();

        supportsAjaxFileUploading = supportsUploading && qq.isXhrUploadSupported();

        supportsUploadingBlobs = supportsAjaxFileUploading && !qq.androidStock();

        supportsFileDrop = supportsAjaxFileUploading && isDragAndDropSupported();

        supportsFolderDrop = supportsFileDrop && isChrome21OrHigher();

        supportsChunking = supportsAjaxFileUploading && qq.isFileChunkingSupported();

        supportsResume = supportsAjaxFileUploading && supportsChunking && isLocalStorageSupported();

        supportsUploadViaPaste = supportsAjaxFileUploading && isChrome14OrHigher();

        supportsUploadCors = supportsUploading && (window.postMessage !== undefined || supportsAjaxFileUploading);

        supportsDeleteFileCorsXhr = isCrossOriginXhrSupported();

        supportsDeleteFileXdr = isXdrSupported();

        supportsDeleteFileCors = isCrossOriginAjaxSupported();

        supportsFolderSelection = isFolderSelectionSupported();

        supportsImagePreviews = supportsAjaxFileUploading && window.FileReader !== undefined;

        supportsUploadProgress = (function () {
            if (supportsAjaxFileUploading) {
                return !qq.androidStock() && !qq.iosChrome();
            }
            return false;
        }());

        return {
            ajaxUploading: supportsAjaxFileUploading,
            blobUploading: supportsUploadingBlobs,
            canDetermineSize: supportsAjaxFileUploading,
            chunking: supportsChunking,
            deleteFileCors: supportsDeleteFileCors,
            deleteFileCorsXdr: supportsDeleteFileXdr, //NOTE: will also return true in IE10, where XDR is also supported
            deleteFileCorsXhr: supportsDeleteFileCorsXhr,
            dialogElement: !!window.HTMLDialogElement,
            fileDrop: supportsFileDrop,
            folderDrop: supportsFolderDrop,
            folderSelection: supportsFolderSelection,
            imagePreviews: supportsImagePreviews,
            imageValidation: supportsImagePreviews,
            itemSizeValidation: supportsAjaxFileUploading,
            pause: supportsChunking,
            progressBar: supportsUploadProgress,
            resume: supportsResume,
            tiffPreviews: qq.safari(), // Not the best solution, but simple and probably accurate enough (for now)
            unlimitedScaledImageSize: !qq.ios(), // false simply indicates that there is some known limit
            uploading: supportsUploading,
            uploadCors: supportsUploadCors,
            uploadCustomHeaders: supportsAjaxFileUploading,
            uploadNonMultipart: supportsAjaxFileUploading,
            uploadViaPaste: supportsUploadViaPaste
        };

    }());

    /*globals qq*/

// Is the passed object a promise instance?
    qq.isGenericPromise = function (maybePromise) {
        "use strict";
        return !!(maybePromise && maybePromise.then && qq.isFunction(maybePromise.then));
    };

    qq.Promise = function () {
        "use strict";

        var successArgs, failureArgs,
            successCallbacks = [],
            failureCallbacks = [],
            doneCallbacks = [],
            state = 0;

        qq.extend(this, {
            then: function (onSuccess, onFailure) {
                if (state === 0) {
                    if (onSuccess) {
                        successCallbacks.push(onSuccess);
                    }
                    if (onFailure) {
                        failureCallbacks.push(onFailure);
                    }
                }
                else if (state === -1) {
                    onFailure && onFailure.apply(null, failureArgs);
                }
                else if (onSuccess) {
                    onSuccess.apply(null, successArgs);
                }

                return this;
            },

            done: function (callback) {
                if (state === 0) {
                    doneCallbacks.push(callback);
                }
                else {
                    callback.apply(null, failureArgs === undefined ? successArgs : failureArgs);
                }

                return this;
            },

            success: function () {
                state = 1;
                successArgs = arguments;

                if (successCallbacks.length) {
                    qq.each(successCallbacks, function (idx, callback) {
                        callback.apply(null, successArgs);
                    });
                }

                if (doneCallbacks.length) {
                    qq.each(doneCallbacks, function (idx, callback) {
                        callback.apply(null, successArgs);
                    });
                }

                return this;
            },

            failure: function () {
                state = -1;
                failureArgs = arguments;

                if (failureCallbacks.length) {
                    qq.each(failureCallbacks, function (idx, callback) {
                        callback.apply(null, failureArgs);
                    });
                }

                if (doneCallbacks.length) {
                    qq.each(doneCallbacks, function (idx, callback) {
                        callback.apply(null, failureArgs);
                    });
                }

                return this;
            }
        });
    };

    /* globals qq */
    /**
     * Placeholder for a Blob that will be generated on-demand.
     *
     * @param referenceBlob Parent of the generated blob
     * @param onCreate Function to invoke when the blob must be created.  Must be promissory.
     * @constructor
     */
    qq.BlobProxy = function (referenceBlob, onCreate) {
        "use strict";

        qq.extend(this, {
            referenceBlob: referenceBlob,

            create: function () {
                return onCreate(referenceBlob);
            }
        });
    };

    /*globals qq*/

    /**
     * This module represents an upload or "Select File(s)" button.  It's job is to embed an opaque `<input type="file">`
     * element as a child of a provided "container" element.  This "container" element (`options.element`) is used to provide
     * a custom style for the `<input type="file">` element.  The ability to change the style of the container element is also
     * provided here by adding CSS classes to the container on hover/focus.
     *
     * TODO Eliminate the mouseover and mouseout event handlers since the :hover CSS pseudo-class should now be
     * available on all supported browsers.
     *
     * @param o Options to override the default values
     */
    qq.UploadButton = function (o) {
        "use strict";

        var self = this,

            disposeSupport = new qq.DisposeSupport(),

            options = {
                // Corresponds to the `accept` attribute on the associated `<input type="file">`
                acceptFiles: null,

                // "Container" element
                element: null,

                focusClass: "qq-upload-button-focus",

                // A true value allows folders to be selected, if supported by the UA
                folders: false,

                // **This option will be removed** in the future as the :hover CSS pseudo-class is available on all supported browsers
                hoverClass: "qq-upload-button-hover",

                ios8BrowserCrashWorkaround: false,

                // If true adds `multiple` attribute to `<input type="file">`
                multiple: false,

                // `name` attribute of `<input type="file">`
                name: "qqfile",

                // Called when the browser invokes the onchange handler on the `<input type="file">`
                onChange: function (input) {
                },

                title: null
            },
            input, buttonId;

        // Overrides any of the default option values with any option values passed in during construction.
        qq.extend(options, o);

        buttonId = qq.getUniqueId();

        // Embed an opaque `<input type="file">` element as a child of `options.element`.
        function createInput() {
            var input = document.createElement("input");

            input.setAttribute(qq.UploadButton.BUTTON_ID_ATTR_NAME, buttonId);
            input.setAttribute("title", options.title);

            self.setMultiple(options.multiple, input);

            if (options.folders && qq.supportedFeatures.folderSelection) {
                // selecting directories is only possible in Chrome now, via a vendor-specific prefixed attribute
                input.setAttribute("webkitdirectory", "");
            }

            if (options.acceptFiles) {
                input.setAttribute("accept", options.acceptFiles);
            }

            input.setAttribute("type", "file");
            input.setAttribute("name", options.name);

            qq(input).css({
                position: "absolute",
                // in Opera only 'browse' button
                // is clickable and it is located at
                // the right side of the input
                right: 0,
                top: 0,
                fontFamily: "Arial",
                // It's especially important to make this an arbitrarily large value
                // to ensure the rendered input button in IE takes up the entire
                // space of the container element.  Otherwise, the left side of the
                // button will require a double-click to invoke the file chooser.
                // In other browsers, this might cause other issues, so a large font-size
                // is only used in IE.  There is a bug in IE8 where the opacity style is  ignored
                // in some cases when the font-size is large.  So, this workaround is not applied
                // to IE8.
                fontSize: qq.ie() && !qq.ie8() ? "3500px" : "118px",
                margin: 0,
                padding: 0,
                cursor: "pointer",
                opacity: 0
            });

            // Setting the file input's height to 100% in IE7 causes
            // most of the visible button to be unclickable.
            !qq.ie7() && qq(input).css({height: "100%"});

            options.element.appendChild(input);

            disposeSupport.attach(input, "change", function () {
                options.onChange(input);
            });

            // **These event handlers will be removed** in the future as the :hover CSS pseudo-class is available on all supported browsers
            disposeSupport.attach(input, "mouseover", function () {
                qq(options.element).addClass(options.hoverClass);
            });
            disposeSupport.attach(input, "mouseout", function () {
                qq(options.element).removeClass(options.hoverClass);
            });

            disposeSupport.attach(input, "focus", function () {
                qq(options.element).addClass(options.focusClass);
            });
            disposeSupport.attach(input, "blur", function () {
                qq(options.element).removeClass(options.focusClass);
            });

            return input;
        }

        // Make button suitable container for input
        qq(options.element).css({
            position: "relative",
            // overflow: "hidden",
            // Make sure browse button is in the right side in Internet Explorer
            direction: "ltr"
        });

        // Exposed API
        qq.extend(this, {
            getInput: function () {
                return input;
            },

            getButtonId: function () {
                return buttonId;
            },

            setMultiple: function (isMultiple, optInput) {
                var input = optInput || this.getInput();

                // Temporary workaround for bug in in iOS8 UIWebView that causes the browser to crash
                // before the file chooser appears if the file input doesn't contain a multiple attribute.
                // See #1283.
                if (options.ios8BrowserCrashWorkaround && qq.ios8() && (qq.iosChrome() || qq.iosSafariWebView())) {
                    input.setAttribute("multiple", "");
                }

                else {
                    if (isMultiple) {
                        input.setAttribute("multiple", "");
                    }
                    else {
                        input.removeAttribute("multiple");
                    }
                }
            },

            setAcceptFiles: function (acceptFiles) {
                if (acceptFiles !== options.acceptFiles) {
                    input.setAttribute("accept", acceptFiles);
                }
            },

            reset: function () {
                if (input.parentNode) {
                    qq(input).remove();
                }

                qq(options.element).removeClass(options.focusClass);
                input = null;
                input = createInput();
            },

            remove: function () {
                if (input.parentNode) {
                    qq(input).remove();
                }

                qq(options.element).removeClass(options.focusClass);
                input = null;
            }
        });

        input = createInput();
    };

    qq.UploadButton.BUTTON_ID_ATTR_NAME = "qq-button-id";

    /*globals qq */
    qq.UploadData = function (uploaderProxy) {
        "use strict";

        var data = [],
            byUuid = {},
            byStatus = {},
            byProxyGroupId = {},
            byBatchId = {};

        function getDataByIds(idOrIds) {
            if (qq.isArray(idOrIds)) {
                var entries = [];

                qq.each(idOrIds, function (idx, id) {
                    entries.push(data[id]);
                });

                return entries;
            }

            return data[idOrIds];
        }

        function getDataByUuids(uuids) {
            if (qq.isArray(uuids)) {
                var entries = [];

                qq.each(uuids, function (idx, uuid) {
                    entries.push(data[byUuid[uuid]]);
                });

                return entries;
            }

            return data[byUuid[uuids]];
        }

        function getDataByStatus(status) {
            var statusResults = [],
                statuses = [].concat(status);

            qq.each(statuses, function (index, statusEnum) {
                var statusResultIndexes = byStatus[statusEnum];

                if (statusResultIndexes !== undefined) {
                    qq.each(statusResultIndexes, function (i, dataIndex) {
                        statusResults.push(data[dataIndex]);
                    });
                }
            });

            return statusResults;
        }

        qq.extend(this, {
            /**
             * Adds a new file to the data cache for tracking purposes.
             *
             * @param spec Data that describes this file.  Possible properties are:
             *
             * - uuid: Initial UUID for this file.
             * - name: Initial name of this file.
             * - size: Size of this file, omit if this cannot be determined
             * - status: Initial `qq.status` for this file.  Omit for `qq.status.SUBMITTING`.
             * - batchId: ID of the batch this file belongs to
             * - proxyGroupId: ID of the proxy group associated with this file
             *
             * @returns {number} Internal ID for this file.
             */
            addFile: function (spec) {
                var status = spec.status || qq.status.SUBMITTING,
                    id = data.push({
                            name: spec.name,
                            originalName: spec.name,
                            uuid: spec.uuid,
                            size: spec.size == null ? -1 : spec.size,
                            status: status
                        }) - 1;

                if (spec.batchId) {
                    data[id].batchId = spec.batchId;

                    if (byBatchId[spec.batchId] === undefined) {
                        byBatchId[spec.batchId] = [];
                    }
                    byBatchId[spec.batchId].push(id);
                }

                if (spec.proxyGroupId) {
                    data[id].proxyGroupId = spec.proxyGroupId;

                    if (byProxyGroupId[spec.proxyGroupId] === undefined) {
                        byProxyGroupId[spec.proxyGroupId] = [];
                    }
                    byProxyGroupId[spec.proxyGroupId].push(id);
                }

                data[id].id = id;
                byUuid[spec.uuid] = id;

                if (byStatus[status] === undefined) {
                    byStatus[status] = [];
                }
                byStatus[status].push(id);

                uploaderProxy.onStatusChange(id, null, status);

                return id;
            },

            retrieve: function (optionalFilter) {
                if (qq.isObject(optionalFilter) && data.length) {
                    if (optionalFilter.id !== undefined) {
                        return getDataByIds(optionalFilter.id);
                    }

                    else if (optionalFilter.uuid !== undefined) {
                        return getDataByUuids(optionalFilter.uuid);
                    }

                    else if (optionalFilter.status) {
                        return getDataByStatus(optionalFilter.status);
                    }
                }
                else {
                    return qq.extend([], data, true);
                }
            },

            reset: function () {
                data = [];
                byUuid = {};
                byStatus = {};
                byBatchId = {};
            },

            setStatus: function (id, newStatus) {
                var oldStatus = data[id].status,
                    byStatusOldStatusIndex = qq.indexOf(byStatus[oldStatus], id);

                byStatus[oldStatus].splice(byStatusOldStatusIndex, 1);

                data[id].status = newStatus;

                if (byStatus[newStatus] === undefined) {
                    byStatus[newStatus] = [];
                }
                byStatus[newStatus].push(id);

                uploaderProxy.onStatusChange(id, oldStatus, newStatus);
            },

            uuidChanged: function (id, newUuid) {
                var oldUuid = data[id].uuid;

                data[id].uuid = newUuid;
                byUuid[newUuid] = id;
                delete byUuid[oldUuid];
            },

            updateName: function (id, newName) {
                data[id].name = newName;
            },

            updateSize: function (id, newSize) {
                data[id].size = newSize;
            },

            // Only applicable if this file has a parent that we may want to reference later.
            setParentId: function (targetId, parentId) {
                data[targetId].parentId = parentId;
            },

            getIdsInProxyGroup: function (id) {
                var proxyGroupId = data[id].proxyGroupId;

                if (proxyGroupId) {
                    return byProxyGroupId[proxyGroupId];
                }
                return [];
            },

            getIdsInBatch: function (id) {
                var batchId = data[id].batchId;

                return byBatchId[batchId];
            }
        });
    };

    qq.status = {
        SUBMITTING: "submitting",
        SUBMITTED: "submitted",
        REJECTED: "rejected",
        QUEUED: "queued",
        CANCELED: "canceled",
        PAUSED: "paused",
        UPLOADING: "uploading",
        UPLOAD_RETRYING: "retrying upload",
        UPLOAD_SUCCESSFUL: "upload successful",
        UPLOAD_FAILED: "upload failed",
        DELETE_FAILED: "delete failed",
        DELETING: "deleting",
        DELETED: "deleted"
    };

    /*globals qq*/
    /**
     * Defines the public API for FineUploaderBasic mode.
     */
    (function () {
        "use strict";

        qq.basePublicApi = {
            // DEPRECATED - TODO REMOVE IN NEXT MAJOR RELEASE (replaced by addFiles)
            addBlobs: function (blobDataOrArray, params, endpoint) {
                this.addFiles(blobDataOrArray, params, endpoint);
            },

            addInitialFiles: function (cannedFileList) {
                var self = this;

                qq.each(cannedFileList, function (index, cannedFile) {
                    self._addCannedFile(cannedFile);
                });
            },

            addFiles: function (data, params, endpoint) {
                this._maybeHandleIos8SafariWorkaround();

                var batchId = this._storedIds.length === 0 ? qq.getUniqueId() : this._currentBatchId,

                    processBlob = qq.bind(function (blob) {
                        this._handleNewFile({
                            blob: blob,
                            name: this._options.blobs.defaultName
                        }, batchId, verifiedFiles);
                    }, this),

                    processBlobData = qq.bind(function (blobData) {
                        this._handleNewFile(blobData, batchId, verifiedFiles);
                    }, this),

                    processCanvas = qq.bind(function (canvas) {
                        var blob = qq.canvasToBlob(canvas);

                        this._handleNewFile({
                            blob: blob,
                            name: this._options.blobs.defaultName + ".png"
                        }, batchId, verifiedFiles);
                    }, this),

                    processCanvasData = qq.bind(function (canvasData) {
                        var normalizedQuality = canvasData.quality && canvasData.quality / 100,
                            blob = qq.canvasToBlob(canvasData.canvas, canvasData.type, normalizedQuality);

                        this._handleNewFile({
                            blob: blob,
                            name: canvasData.name
                        }, batchId, verifiedFiles);
                    }, this),

                    processFileOrInput = qq.bind(function (fileOrInput) {
                        if (qq.isInput(fileOrInput) && qq.supportedFeatures.ajaxUploading) {
                            var files = Array.prototype.slice.call(fileOrInput.files),
                                self = this;

                            qq.each(files, function (idx, file) {
                                self._handleNewFile(file, batchId, verifiedFiles);
                            });
                        }
                        else {
                            this._handleNewFile(fileOrInput, batchId, verifiedFiles);
                        }
                    }, this),

                    normalizeData = function () {
                        if (qq.isFileList(data)) {
                            data = Array.prototype.slice.call(data);
                        }
                        data = [].concat(data);
                    },

                    self = this,
                    verifiedFiles = [];

                this._currentBatchId = batchId;

                if (data) {
                    normalizeData();

                    qq.each(data, function (idx, fileContainer) {
                        if (qq.isFileOrInput(fileContainer)) {
                            processFileOrInput(fileContainer);
                        }
                        else if (qq.isBlob(fileContainer)) {
                            processBlob(fileContainer);
                        }
                        else if (qq.isObject(fileContainer)) {
                            if (fileContainer.blob && fileContainer.name) {
                                processBlobData(fileContainer);
                            }
                            else if (fileContainer.canvas && fileContainer.name) {
                                processCanvasData(fileContainer);
                            }
                        }
                        else if (fileContainer.tagName && fileContainer.tagName.toLowerCase() === "canvas") {
                            processCanvas(fileContainer);
                        }
                        else {
                            self.log(fileContainer + " is not a valid file container!  Ignoring!", "warn");
                        }
                    });

                    this.log("Received " + verifiedFiles.length + " files.");
                    this._prepareItemsForUpload(verifiedFiles, params, endpoint);
                }
            },

            cancel: function (id) {
                this._handler.cancel(id);
            },

            cancelAll: function () {
                var storedIdsCopy = [],
                    self = this;

                qq.extend(storedIdsCopy, this._storedIds);
                qq.each(storedIdsCopy, function (idx, storedFileId) {
                    self.cancel(storedFileId);
                });

                this._handler.cancelAll();
            },

            clearStoredFiles: function () {
                this._storedIds = [];
            },

            continueUpload: function (id) {
                var uploadData = this._uploadData.retrieve({id: id});

                if (!qq.supportedFeatures.pause || !this._options.chunking.enabled) {
                    return false;
                }

                if (uploadData.status === qq.status.PAUSED) {
                    this.log(qq.format("Paused file ID {} ({}) will be continued.  Not paused.", id, this.getName(id)));
                    this._uploadFile(id);
                    return true;
                }
                else {
                    this.log(qq.format("Ignoring continue for file ID {} ({}).  Not paused.", id, this.getName(id)), "error");
                }

                return false;
            },

            deleteFile: function (id) {
                return this._onSubmitDelete(id);
            },

            // TODO document?
            doesExist: function (fileOrBlobId) {
                return this._handler.isValid(fileOrBlobId);
            },

            getButton: function (fileId) {
                return this._getButton(this._buttonIdsForFileIds[fileId]);
            },

            getEndpoint: function (fileId) {
                return this._endpointStore.get(fileId);
            },

            getFile: function (fileOrBlobId) {
                return this._handler.getFile(fileOrBlobId) || null;
            },

            getInProgress: function () {
                return this._uploadData.retrieve({
                    status: [
                        qq.status.UPLOADING,
                        qq.status.UPLOAD_RETRYING,
                        qq.status.QUEUED
                    ]
                }).length;
            },

            getName: function (id) {
                return this._uploadData.retrieve({id: id}).name;
            },

            // Parent ID for a specific file, or null if this is the parent, or if it has no parent.
            getParentId: function (id) {
                var uploadDataEntry = this.getUploads({id: id}),
                    parentId = null;

                if (uploadDataEntry) {
                    if (uploadDataEntry.parentId !== undefined) {
                        parentId = uploadDataEntry.parentId;
                    }
                }

                return parentId;
            },

            getResumableFilesData: function () {
                return this._handler.getResumableFilesData();
            },

            getSize: function (id) {
                return this._uploadData.retrieve({id: id}).size;
            },

            getNetUploads: function () {
                return this._netUploaded;
            },

            getRemainingAllowedItems: function () {
                var allowedItems = this._currentItemLimit;

                if (allowedItems > 0) {
                    return allowedItems - this._netUploadedOrQueued;
                }

                return null;
            },

            getUploads: function (optionalFilter) {
                return this._uploadData.retrieve(optionalFilter);
            },

            getUuid: function (id) {
                return this._uploadData.retrieve({id: id}).uuid;
            },

            log: function (str, level) {
                if (this._options.debug && (!level || level === "info")) {
                    qq.log("[Fine Uploader " + qq.version + "] " + str);
                }
                else if (level && level !== "info") {
                    qq.log("[Fine Uploader " + qq.version + "] " + str, level);

                }
            },

            pauseUpload: function (id) {
                var uploadData = this._uploadData.retrieve({id: id});

                if (!qq.supportedFeatures.pause || !this._options.chunking.enabled) {
                    return false;
                }

                // Pause only really makes sense if the file is uploading or retrying
                if (qq.indexOf([qq.status.UPLOADING, qq.status.UPLOAD_RETRYING], uploadData.status) >= 0) {
                    if (this._handler.pause(id)) {
                        this._uploadData.setStatus(id, qq.status.PAUSED);
                        return true;
                    }
                    else {
                        this.log(qq.format("Unable to pause file ID {} ({}).", id, this.getName(id)), "error");
                    }
                }
                else {
                    this.log(qq.format("Ignoring pause for file ID {} ({}).  Not in progress.", id, this.getName(id)), "error");
                }

                return false;
            },

            reset: function (removeButtons) {
                this.log("Resetting uploader...");

                this._handler.reset();
                this._storedIds = [];
                this._autoRetries = [];
                this._retryTimeouts = [];
                this._preventRetries = [];
                this._thumbnailUrls = [];

                qq.each(this._buttons, function (idx, button) {
                    !!removeButtons ? button.remove() : button.reset();
                });

                this._paramsStore.reset();
                this._endpointStore.reset();
                this._netUploadedOrQueued = 0;
                this._netUploaded = 0;
                this._uploadData.reset();
                this._buttonIdsForFileIds = [];

                this._pasteHandler && this._pasteHandler.reset();
                this._options.session.refreshOnReset && this._refreshSessionData();

                this._succeededSinceLastAllComplete = [];
                this._failedSinceLastAllComplete = [];

                this._totalProgress && this._totalProgress.reset();
            },

            getInputButton: function () {
                return this._buttons[0];
            },

            retry: function (id) {
                return this._manualRetry(id);
            },

            setCustomHeaders: function (headers, id) {
                this._customHeadersStore.set(headers, id);
            },

            setDeleteFileCustomHeaders: function (headers, id) {
                this._deleteFileCustomHeadersStore.set(headers, id);
            },

            setDeleteFileEndpoint: function (endpoint, id) {
                this._deleteFileEndpointStore.set(endpoint, id);
            },

            setDeleteFileParams: function (params, id) {
                this._deleteFileParamsStore.set(params, id);
            },

            // Re-sets the default endpoint, an endpoint for a specific file, or an endpoint for a specific button
            setEndpoint: function (endpoint, id) {
                this._endpointStore.set(endpoint, id);
            },

            setForm: function (elementOrId) {
                this._updateFormSupportAndParams(elementOrId);
            },

            setItemLimit: function (newItemLimit) {
                this._currentItemLimit = newItemLimit;
            },

            setName: function (id, newName) {
                this._uploadData.updateName(id, newName);
            },

            setParams: function (params, id) {
                this._paramsStore.set(params, id);
            },

            setUuid: function (id, newUuid) {
                return this._uploadData.uuidChanged(id, newUuid);
            },

            uploadStoredFiles: function () {
                if (this._storedIds.length === 0) {
                    this._itemError("noFilesError");
                }
                else {
                    this._uploadStoredFiles();
                }
            }
        };

        /**
         * Defines the private (internal) API for FineUploaderBasic mode.
         */
        qq.basePrivateApi = {
            // Updates internal state with a file record (not backed by a live file).  Returns the assigned ID.
            _addCannedFile: function (sessionData) {
                var id = this._uploadData.addFile({
                    uuid: sessionData.uuid,
                    name: sessionData.name,
                    size: sessionData.size,
                    status: qq.status.UPLOAD_SUCCESSFUL
                });

                sessionData.deleteFileEndpoint && this.setDeleteFileEndpoint(sessionData.deleteFileEndpoint, id);
                sessionData.deleteFileParams && this.setDeleteFileParams(sessionData.deleteFileParams, id);

                if (sessionData.thumbnailUrl) {
                    this._thumbnailUrls[id] = sessionData.thumbnailUrl;
                }

                this._netUploaded++;
                this._netUploadedOrQueued++;

                return id;
            },

            _annotateWithButtonId: function (file, associatedInput) {
                if (qq.isFile(file)) {
                    file.qqButtonId = this._getButtonId(associatedInput);
                }
            },

            _batchError: function (message) {
                this._options.callbacks.onError(null, null, message, undefined);
            },

            _createDeleteHandler: function () {
                var self = this;

                return new qq.DeleteFileAjaxRequester({
                    method: this._options.deleteFile.method.toUpperCase(),
                    maxConnections: this._options.maxConnections,
                    uuidParamName: this._options.request.uuidName,
                    customHeaders: this._deleteFileCustomHeadersStore,
                    paramsStore: this._deleteFileParamsStore,
                    endpointStore: this._deleteFileEndpointStore,
                    cors: this._options.cors,
                    log: qq.bind(self.log, self),
                    onDelete: function (id) {
                        self._onDelete(id);
                        self._options.callbacks.onDelete(id);
                    },
                    onDeleteComplete: function (id, xhrOrXdr, isError) {
                        self._onDeleteComplete(id, xhrOrXdr, isError);
                        self._options.callbacks.onDeleteComplete(id, xhrOrXdr, isError);
                    }

                });
            },

            _createPasteHandler: function () {
                var self = this;

                return new qq.PasteSupport({
                    targetElement: this._options.paste.targetElement,
                    callbacks: {
                        log: qq.bind(self.log, self),
                        pasteReceived: function (blob) {
                            self._handleCheckedCallback({
                                name: "onPasteReceived",
                                callback: qq.bind(self._options.callbacks.onPasteReceived, self, blob),
                                onSuccess: qq.bind(self._handlePasteSuccess, self, blob),
                                identifier: "pasted image"
                            });
                        }
                    }
                });
            },

            _createStore: function (initialValue, _readOnlyValues_) {
                var store = {},
                    catchall = initialValue,
                    perIdReadOnlyValues = {},
                    readOnlyValues = _readOnlyValues_,
                    copy = function (orig) {
                        if (qq.isObject(orig)) {
                            return qq.extend({}, orig);
                        }
                        return orig;
                    },
                    getReadOnlyValues = function () {
                        if (qq.isFunction(readOnlyValues)) {
                            return readOnlyValues();
                        }
                        return readOnlyValues;
                    },
                    includeReadOnlyValues = function (id, existing) {
                        if (readOnlyValues && qq.isObject(existing)) {
                            qq.extend(existing, getReadOnlyValues());
                        }

                        if (perIdReadOnlyValues[id]) {
                            qq.extend(existing, perIdReadOnlyValues[id]);
                        }
                    };

                return {
                    set: function (val, id) {
                        /*jshint eqeqeq: true, eqnull: true*/
                        if (id == null) {
                            store = {};
                            catchall = copy(val);
                        }
                        else {
                            store[id] = copy(val);
                        }
                    },

                    get: function (id) {
                        var values;

                        /*jshint eqeqeq: true, eqnull: true*/
                        if (id != null && store[id]) {
                            values = store[id];
                        }
                        else {
                            values = copy(catchall);
                        }

                        includeReadOnlyValues(id, values);

                        return copy(values);
                    },

                    addReadOnly: function (id, values) {
                        // Only applicable to Object stores
                        if (qq.isObject(store)) {
                            // If null ID, apply readonly values to all files
                            if (id === null) {
                                if (qq.isFunction(values)) {
                                    readOnlyValues = values;
                                }
                                else {
                                    readOnlyValues = readOnlyValues || {};
                                    qq.extend(readOnlyValues, values);
                                }
                            }
                            else {
                                perIdReadOnlyValues[id] = perIdReadOnlyValues[id] || {};
                                qq.extend(perIdReadOnlyValues[id], values);
                            }
                        }
                    },

                    remove: function (fileId) {
                        return delete store[fileId];
                    },

                    reset: function () {
                        store = {};
                        perIdReadOnlyValues = {};
                        catchall = initialValue;
                    }
                };
            },

            _createUploadDataTracker: function () {
                var self = this;

                return new qq.UploadData({
                    getName: function (id) {
                        return self.getName(id);
                    },
                    getUuid: function (id) {
                        return self.getUuid(id);
                    },
                    getSize: function (id) {
                        return self.getSize(id);
                    },
                    onStatusChange: function (id, oldStatus, newStatus) {
                        self._onUploadStatusChange(id, oldStatus, newStatus);
                        self._options.callbacks.onStatusChange(id, oldStatus, newStatus);
                        self._maybeAllComplete(id, newStatus);

                        if (self._totalProgress) {
                            setTimeout(function () {
                                self._totalProgress.onStatusChange(id, oldStatus, newStatus);
                            }, 0);
                        }
                    }
                });
            },

            /**
             * Generate a tracked upload button.
             *
             * @param spec Object containing a required `element` property
             * along with optional `multiple`, `accept`, and `folders`.
             * @returns {qq.UploadButton}
             * @private
             */
            _createUploadButton: function (spec) {
                var self = this,
                    acceptFiles = spec.accept || this._options.validation.acceptFiles,
                    allowedExtensions = spec.allowedExtensions || this._options.validation.allowedExtensions,
                    button;

                function allowMultiple() {
                    if (qq.supportedFeatures.ajaxUploading) {
                        // Workaround for bug in iOS7+ (see #1039)
                        if (self._options.workarounds.iosEmptyVideos &&
                            qq.ios() && !qq.ios6() &&
                            self._isAllowedExtension(allowedExtensions, ".mov")) {

                            return false;
                        }

                        if (spec.multiple === undefined) {
                            return self._options.multiple;
                        }

                        return spec.multiple;
                    }

                    return false;
                }

                button = new qq.UploadButton({
                    acceptFiles: acceptFiles,
                    element: spec.element,
                    focusClass: this._options.classes.buttonFocus,
                    folders: spec.folders,
                    hoverClass: this._options.classes.buttonHover,
                    ios8BrowserCrashWorkaround: this._options.workarounds.ios8BrowserCrash,
                    multiple: allowMultiple(),
                    name: this._options.request.inputName,
                    onChange: function (input) {
                        self._onInputChange(input);
                    },
                    title: spec.title == null ? this._options.text.fileInputTitle : spec.title
                });

                this._disposeSupport.addDisposer(function () {
                    button.dispose();
                });

                self._buttons.push(button);

                return button;
            },

            _createUploadHandler: function (additionalOptions, namespace) {
                var self = this,
                    lastOnProgress = {},
                    options = {
                        debug: this._options.debug,
                        maxConnections: this._options.maxConnections,
                        cors: this._options.cors,
                        paramsStore: this._paramsStore,
                        endpointStore: this._endpointStore,
                        chunking: this._options.chunking,
                        resume: this._options.resume,
                        blobs: this._options.blobs,
                        log: qq.bind(self.log, self),
                        preventRetryParam: this._options.retry.preventRetryResponseProperty,
                        onProgress: function (id, name, loaded, total) {
                            if (loaded < 0 || total < 0) {
                                return;
                            }

                            if (lastOnProgress[id]) {
                                if (lastOnProgress[id].loaded !== loaded || lastOnProgress[id].total !== total) {
                                    self._onProgress(id, name, loaded, total);
                                    self._options.callbacks.onProgress(id, name, loaded, total);
                                }
                            }
                            else {
                                self._onProgress(id, name, loaded, total);
                                self._options.callbacks.onProgress(id, name, loaded, total);
                            }

                            lastOnProgress[id] = {loaded: loaded, total: total};

                        },
                        onComplete: function (id, name, result, xhr) {
                            delete lastOnProgress[id];

                            var status = self.getUploads({id: id}).status,
                                retVal;

                            // This is to deal with some observed cases where the XHR readyStateChange handler is
                            // invoked by the browser multiple times for the same XHR instance with the same state
                            // readyState value.  Higher level: don't invoke complete-related code if we've already
                            // done this.
                            if (status === qq.status.UPLOAD_SUCCESSFUL || status === qq.status.UPLOAD_FAILED) {
                                return;
                            }

                            retVal = self._onComplete(id, name, result, xhr);

                            // If the internal `_onComplete` handler returns a promise, don't invoke the `onComplete` callback
                            // until the promise has been fulfilled.
                            if (retVal instanceof  qq.Promise) {
                                retVal.done(function () {
                                    self._options.callbacks.onComplete(id, name, result, xhr);
                                });
                            }
                            else {
                                self._options.callbacks.onComplete(id, name, result, xhr);
                            }
                        },
                        onCancel: function (id, name, cancelFinalizationEffort) {
                            var promise = new qq.Promise();

                            self._handleCheckedCallback({
                                name: "onCancel",
                                callback: qq.bind(self._options.callbacks.onCancel, self, id, name),
                                onFailure: promise.failure,
                                onSuccess: function () {
                                    cancelFinalizationEffort.then(function () {
                                        self._onCancel(id, name);
                                    });

                                    promise.success();
                                },
                                identifier: id
                            });

                            return promise;
                        },
                        onUploadPrep: qq.bind(this._onUploadPrep, this),
                        onUpload: function (id, name) {
                            self._onUpload(id, name);
                            self._options.callbacks.onUpload(id, name);
                        },
                        onUploadChunk: function (id, name, chunkData) {
                            self._onUploadChunk(id, chunkData);
                            self._options.callbacks.onUploadChunk(id, name, chunkData);
                        },
                        onUploadChunkSuccess: function (id, chunkData, result, xhr) {
                            self._options.callbacks.onUploadChunkSuccess.apply(self, arguments);
                        },
                        onResume: function (id, name, chunkData) {
                            return self._options.callbacks.onResume(id, name, chunkData);
                        },
                        onAutoRetry: function (id, name, responseJSON, xhr) {
                            return self._onAutoRetry.apply(self, arguments);
                        },
                        onUuidChanged: function (id, newUuid) {
                            self.log("Server requested UUID change from '" + self.getUuid(id) + "' to '" + newUuid + "'");
                            self.setUuid(id, newUuid);
                        },
                        getName: qq.bind(self.getName, self),
                        getUuid: qq.bind(self.getUuid, self),
                        getSize: qq.bind(self.getSize, self),
                        setSize: qq.bind(self._setSize, self),
                        getDataByUuid: function (uuid) {
                            return self.getUploads({uuid: uuid});
                        },
                        isQueued: function (id) {
                            var status = self.getUploads({id: id}).status;
                            return status === qq.status.QUEUED ||
                                   status === qq.status.SUBMITTED ||
                                   status === qq.status.UPLOAD_RETRYING ||
                                   status === qq.status.PAUSED;
                        },
                        getIdsInProxyGroup: self._uploadData.getIdsInProxyGroup,
                        getIdsInBatch: self._uploadData.getIdsInBatch
                    };

                qq.each(this._options.request, function (prop, val) {
                    options[prop] = val;
                });

                options.customHeaders = this._customHeadersStore;

                if (additionalOptions) {
                    qq.each(additionalOptions, function (key, val) {
                        options[key] = val;
                    });
                }

                return new qq.UploadHandlerController(options, namespace);
            },

            _fileOrBlobRejected: function (id) {
                this._netUploadedOrQueued--;
                this._uploadData.setStatus(id, qq.status.REJECTED);
            },

            _formatSize: function (bytes) {
                var i = -1;
                do {
                    bytes = bytes / 1000;
                    i++;
                } while (bytes > 999);

                return Math.max(bytes, 0.1).toFixed(1) + this._options.text.sizeSymbols[i];
            },

            // Creates an internal object that tracks various properties of each extra button,
            // and then actually creates the extra button.
            _generateExtraButtonSpecs: function () {
                var self = this;

                this._extraButtonSpecs = {};

                qq.each(this._options.extraButtons, function (idx, extraButtonOptionEntry) {
                    var multiple = extraButtonOptionEntry.multiple,
                        validation = qq.extend({}, self._options.validation, true),
                        extraButtonSpec = qq.extend({}, extraButtonOptionEntry);

                    if (multiple === undefined) {
                        multiple = self._options.multiple;
                    }

                    if (extraButtonSpec.validation) {
                        qq.extend(validation, extraButtonOptionEntry.validation, true);
                    }

                    qq.extend(extraButtonSpec, {
                        multiple: multiple,
                        validation: validation
                    }, true);

                    self._initExtraButton(extraButtonSpec);
                });
            },

            _getButton: function (buttonId) {
                var extraButtonsSpec = this._extraButtonSpecs[buttonId];

                if (extraButtonsSpec) {
                    return extraButtonsSpec.element;
                }
                else if (buttonId === this._defaultButtonId) {
                    return this._options.button;
                }
            },

            /**
             * Gets the internally used tracking ID for a button.
             *
             * @param buttonOrFileInputOrFile `File`, `<input type="file">`, or a button container element
             * @returns {*} The button's ID, or undefined if no ID is recoverable
             * @private
             */
            _getButtonId: function (buttonOrFileInputOrFile) {
                var inputs, fileInput,
                    fileBlobOrInput = buttonOrFileInputOrFile;

                // We want the reference file/blob here if this is a proxy (a file that will be generated on-demand later)
                if (fileBlobOrInput instanceof qq.BlobProxy) {
                    fileBlobOrInput = fileBlobOrInput.referenceBlob;
                }

                // If the item is a `Blob` it will never be associated with a button or drop zone.
                if (fileBlobOrInput && !qq.isBlob(fileBlobOrInput)) {
                    if (qq.isFile(fileBlobOrInput)) {
                        return fileBlobOrInput.qqButtonId;
                    }
                    else if (fileBlobOrInput.tagName.toLowerCase() === "input" &&
                             fileBlobOrInput.type.toLowerCase() === "file") {

                        return fileBlobOrInput.getAttribute(qq.UploadButton.BUTTON_ID_ATTR_NAME);
                    }

                    inputs = fileBlobOrInput.getElementsByTagName("input");

                    qq.each(inputs, function (idx, input) {
                        if (input.getAttribute("type") === "file") {
                            fileInput = input;
                            return false;
                        }
                    });

                    if (fileInput) {
                        return fileInput.getAttribute(qq.UploadButton.BUTTON_ID_ATTR_NAME);
                    }
                }
            },

            _getNotFinished: function () {
                return this._uploadData.retrieve({
                    status: [
                        qq.status.UPLOADING,
                        qq.status.UPLOAD_RETRYING,
                        qq.status.QUEUED,
                        qq.status.SUBMITTING,
                        qq.status.SUBMITTED,
                        qq.status.PAUSED
                    ]
                }).length;
            },

            // Get the validation options for this button.  Could be the default validation option
            // or a specific one assigned to this particular button.
            _getValidationBase: function (buttonId) {
                var extraButtonSpec = this._extraButtonSpecs[buttonId];

                return extraButtonSpec ? extraButtonSpec.validation : this._options.validation;
            },

            _getValidationDescriptor: function (fileWrapper) {
                if (fileWrapper.file instanceof qq.BlobProxy) {
                    return {
                        name: qq.getFilename(fileWrapper.file.referenceBlob),
                        size: fileWrapper.file.referenceBlob.size
                    };
                }

                return {
                    name: this.getUploads({id: fileWrapper.id}).name,
                    size: this.getUploads({id: fileWrapper.id}).size
                };
            },

            _getValidationDescriptors: function (fileWrappers) {
                var self = this,
                    fileDescriptors = [];

                qq.each(fileWrappers, function (idx, fileWrapper) {
                    fileDescriptors.push(self._getValidationDescriptor(fileWrapper));
                });

                return fileDescriptors;
            },

            // Allows camera access on either the default or an extra button for iOS devices.
            _handleCameraAccess: function () {
                if (this._options.camera.ios && qq.ios()) {
                    var acceptIosCamera = "image/*;capture=camera",
                        button = this._options.camera.button,
                        buttonId = button ? this._getButtonId(button) : this._defaultButtonId,
                        optionRoot = this._options;

                    // If we are not targeting the default button, it is an "extra" button
                    if (buttonId && buttonId !== this._defaultButtonId) {
                        optionRoot = this._extraButtonSpecs[buttonId];
                    }

                    // Camera access won't work in iOS if the `multiple` attribute is present on the file input
                    optionRoot.multiple = false;

                    // update the options
                    if (optionRoot.validation.acceptFiles === null) {
                        optionRoot.validation.acceptFiles = acceptIosCamera;
                    }
                    else {
                        optionRoot.validation.acceptFiles += "," + acceptIosCamera;
                    }

                    // update the already-created button
                    qq.each(this._buttons, function (idx, button) {
                        if (button.getButtonId() === buttonId) {
                            button.setMultiple(optionRoot.multiple);
                            button.setAcceptFiles(optionRoot.acceptFiles);

                            return false;
                        }
                    });
                }
            },

            _handleCheckedCallback: function (details) {
                var self = this,
                    callbackRetVal = details.callback();

                if (qq.isGenericPromise(callbackRetVal)) {
                    this.log(details.name + " - waiting for " + details.name + " promise to be fulfilled for " + details.identifier);
                    return callbackRetVal.then(
                        function (successParam) {
                            self.log(details.name + " promise success for " + details.identifier);
                            details.onSuccess(successParam);
                        },
                        function () {
                            if (details.onFailure) {
                                self.log(details.name + " promise failure for " + details.identifier);
                                details.onFailure();
                            }
                            else {
                                self.log(details.name + " promise failure for " + details.identifier);
                            }
                        });
                }

                if (callbackRetVal !== false) {
                    details.onSuccess(callbackRetVal);
                }
                else {
                    if (details.onFailure) {
                        this.log(details.name + " - return value was 'false' for " + details.identifier + ".  Invoking failure callback.");
                        details.onFailure();
                    }
                    else {
                        this.log(details.name + " - return value was 'false' for " + details.identifier + ".  Will not proceed.");
                    }
                }

                return callbackRetVal;
            },

            // Updates internal state when a new file has been received, and adds it along with its ID to a passed array.
            _handleNewFile: function (file, batchId, newFileWrapperList) {
                var self = this,
                    uuid = qq.getUniqueId(),
                    size = -1,
                    name = qq.getFilename(file),
                    actualFile = file.blob || file,
                    handler = this._customNewFileHandler ?
                              this._customNewFileHandler :
                              qq.bind(self._handleNewFileGeneric, self);

                if (!qq.isInput(actualFile) && actualFile.size >= 0) {
                    size = actualFile.size;
                }

                handler(actualFile, name, uuid, size, newFileWrapperList, batchId, this._options.request.uuidName, {
                    uploadData: self._uploadData,
                    paramsStore: self._paramsStore,
                    addFileToHandler: function (id, file) {
                        self._handler.add(id, file);
                        self._netUploadedOrQueued++;
                        self._trackButton(id);
                    }
                });
            },

            _handleNewFileGeneric: function (file, name, uuid, size, fileList, batchId) {
                var id = this._uploadData.addFile({uuid: uuid, name: name, size: size, batchId: batchId});

                this._handler.add(id, file);
                this._trackButton(id);

                this._netUploadedOrQueued++;

                fileList.push({id: id, file: file});
            },

            _handlePasteSuccess: function (blob, extSuppliedName) {
                var extension = blob.type.split("/")[1],
                    name = extSuppliedName;

                /*jshint eqeqeq: true, eqnull: true*/
                if (name == null) {
                    name = this._options.paste.defaultName;
                }

                name += "." + extension;

                this.addFiles({
                    name: name,
                    blob: blob
                });
            },

            // Creates an extra button element
            _initExtraButton: function (spec) {
                var button = this._createUploadButton({
                    accept: spec.validation.acceptFiles,
                    allowedExtensions: spec.validation.allowedExtensions,
                    element: spec.element,
                    folders: spec.folders,
                    multiple: spec.multiple,
                    title: spec.fileInputTitle
                });

                this._extraButtonSpecs[button.getButtonId()] = spec;
            },

            _initFormSupportAndParams: function () {
                this._formSupport = qq.FormSupport && new qq.FormSupport(
                        this._options.form, qq.bind(this.uploadStoredFiles, this), qq.bind(this.log, this)
                    );

                if (this._formSupport && this._formSupport.attachedToForm) {
                    this._paramsStore = this._createStore(
                        this._options.request.params, this._formSupport.getFormInputsAsObject
                    );

                    this._options.autoUpload = this._formSupport.newAutoUpload;
                    if (this._formSupport.newEndpoint) {
                        this._options.request.endpoint = this._formSupport.newEndpoint;
                    }
                }
                else {
                    this._paramsStore = this._createStore(this._options.request.params);
                }
            },

            _isDeletePossible: function () {
                if (!qq.DeleteFileAjaxRequester || !this._options.deleteFile.enabled) {
                    return false;
                }

                if (this._options.cors.expected) {
                    if (qq.supportedFeatures.deleteFileCorsXhr) {
                        return true;
                    }

                    if (qq.supportedFeatures.deleteFileCorsXdr && this._options.cors.allowXdr) {
                        return true;
                    }

                    return false;
                }

                return true;
            },

            _isAllowedExtension: function (allowed, fileName) {
                var valid = false;

                if (!allowed.length) {
                    return true;
                }

                qq.each(allowed, function (idx, allowedExt) {
                    /**
                     * If an argument is not a string, ignore it.  Added when a possible issue with MooTools hijacking the
                     * `allowedExtensions` array was discovered.  See case #735 in the issue tracker for more details.
                     */
                    if (qq.isString(allowedExt)) {
                        /*jshint eqeqeq: true, eqnull: true*/
                        var extRegex = new RegExp("\\." + allowedExt + "$", "i");

                        if (fileName.match(extRegex) != null) {
                            valid = true;
                            return false;
                        }
                    }
                });

                return valid;
            },

            /**
             * Constructs and returns a message that describes an item/file error.  Also calls `onError` callback.
             *
             * @param code REQUIRED - a code that corresponds to a stock message describing this type of error
             * @param maybeNameOrNames names of the items that have failed, if applicable
             * @param item `File`, `Blob`, or `<input type="file">`
             * @private
             */
            _itemError: function (code, maybeNameOrNames, item) {
                var message = this._options.messages[code],
                    allowedExtensions = [],
                    names = [].concat(maybeNameOrNames),
                    name = names[0],
                    buttonId = this._getButtonId(item),
                    validationBase = this._getValidationBase(buttonId),
                    extensionsForMessage, placeholderMatch;

                function r(name, replacement) {
                    message = message.replace(name, replacement);
                }

                qq.each(validationBase.allowedExtensions, function (idx, allowedExtension) {
                    /**
                     * If an argument is not a string, ignore it.  Added when a possible issue with MooTools hijacking the
                     * `allowedExtensions` array was discovered.  See case #735 in the issue tracker for more details.
                     */
                    if (qq.isString(allowedExtension)) {
                        allowedExtensions.push(allowedExtension);
                    }
                });

                extensionsForMessage = allowedExtensions.join(", ").toLowerCase();

                r("{file}", this._options.formatFileName(name));
                r("{extensions}", extensionsForMessage);
                r("{sizeLimit}", this._formatSize(validationBase.sizeLimit));
                r("{minSizeLimit}", this._formatSize(validationBase.minSizeLimit));

                placeholderMatch = message.match(/(\{\w+\})/g);
                if (placeholderMatch !== null) {
                    qq.each(placeholderMatch, function (idx, placeholder) {
                        r(placeholder, names[idx]);
                    });
                }

                this._options.callbacks.onError(null, name, message, undefined);

                return message;
            },

            /**
             * Conditionally orders a manual retry of a failed upload.
             *
             * @param id File ID of the failed upload
             * @param callback Optional callback to invoke if a retry is prudent.
             * In lieu of asking the upload handler to retry.
             * @returns {boolean} true if a manual retry will occur
             * @private
             */
            _manualRetry: function (id, callback) {
                if (this._onBeforeManualRetry(id)) {
                    this._netUploadedOrQueued++;
                    this._uploadData.setStatus(id, qq.status.UPLOAD_RETRYING);

                    if (callback) {
                        callback(id);
                    }
                    else {
                        this._handler.retry(id);
                    }

                    return true;
                }
            },

            _maybeAllComplete: function (id, status) {
                var self = this,
                    notFinished = this._getNotFinished();

                if (status === qq.status.UPLOAD_SUCCESSFUL) {
                    this._succeededSinceLastAllComplete.push(id);
                }
                else if (status === qq.status.UPLOAD_FAILED) {
                    this._failedSinceLastAllComplete.push(id);
                }

                if (notFinished === 0 &&
                    (this._succeededSinceLastAllComplete.length || this._failedSinceLastAllComplete.length)) {
                    // Attempt to ensure onAllComplete is not invoked before other callbacks, such as onCancel & onComplete
                    setTimeout(function () {
                        self._onAllComplete(self._succeededSinceLastAllComplete, self._failedSinceLastAllComplete);
                    }, 0);
                }
            },

            _maybeHandleIos8SafariWorkaround: function () {
                var self = this;

                if (this._options.workarounds.ios8SafariUploads && qq.ios800() && qq.iosSafari()) {
                    setTimeout(function () {
                        window.alert(self._options.messages.unsupportedBrowserIos8Safari);
                    }, 0);
                    throw new qq.Error(this._options.messages.unsupportedBrowserIos8Safari);
                }
            },

            _maybeParseAndSendUploadError: function (id, name, response, xhr) {
                // Assuming no one will actually set the response code to something other than 200
                // and still set 'success' to true...
                if (!response.success) {
                    if (xhr && xhr.status !== 200 && !response.error) {
                        this._options.callbacks.onError(id, name, "XHR returned response code " + xhr.status, xhr);
                    }
                    else {
                        var errorReason = response.error ? response.error : this._options.text.defaultResponseError;
                        this._options.callbacks.onError(id, name, errorReason, xhr);
                    }
                }
            },

            _maybeProcessNextItemAfterOnValidateCallback: function (validItem, items, index, params, endpoint) {
                var self = this;

                if (items.length > index) {
                    if (validItem || !this._options.validation.stopOnFirstInvalidFile) {
                        //use setTimeout to prevent a stack overflow with a large number of files in the batch & non-promissory callbacks
                        setTimeout(function () {
                            var validationDescriptor = self._getValidationDescriptor(items[index]),
                                buttonId = self._getButtonId(items[index].file),
                                button = self._getButton(buttonId);

                            self._handleCheckedCallback({
                                name: "onValidate",
                                callback: qq.bind(self._options.callbacks.onValidate, self, validationDescriptor, button),
                                onSuccess: qq.bind(self._onValidateCallbackSuccess, self, items, index, params, endpoint),
                                onFailure: qq.bind(self._onValidateCallbackFailure, self, items, index, params, endpoint),
                                identifier: "Item '" + validationDescriptor.name + "', size: " + validationDescriptor.size
                            });
                        }, 0);
                    }
                    else if (!validItem) {
                        for (; index < items.length; index++) {
                            self._fileOrBlobRejected(items[index].id);
                        }
                    }
                }
            },

            _onAllComplete: function (successful, failed) {
                this._totalProgress && this._totalProgress.onAllComplete(successful, failed, this._preventRetries);

                this._options.callbacks.onAllComplete(qq.extend([], successful), qq.extend([], failed));

                this._succeededSinceLastAllComplete = [];
                this._failedSinceLastAllComplete = [];
            },

            /**
             * Attempt to automatically retry a failed upload.
             *
             * @param id The file ID of the failed upload
             * @param name The name of the file associated with the failed upload
             * @param responseJSON Response from the server, parsed into a javascript object
             * @param xhr Ajax transport used to send the failed request
             * @param callback Optional callback to be invoked if a retry is prudent.
             * Invoked in lieu of asking the upload handler to retry.
             * @returns {boolean} true if an auto-retry will occur
             * @private
             */
            _onAutoRetry: function (id, name, responseJSON, xhr, callback) {
                var self = this;

                self._preventRetries[id] = responseJSON[self._options.retry.preventRetryResponseProperty];

                if (self._shouldAutoRetry(id, name, responseJSON)) {
                    self._maybeParseAndSendUploadError.apply(self, arguments);
                    self._options.callbacks.onAutoRetry(id, name, self._autoRetries[id]);
                    self._onBeforeAutoRetry(id, name);

                    self._retryTimeouts[id] = setTimeout(function () {
                        self.log("Retrying " + name + "...");
                        self._uploadData.setStatus(id, qq.status.UPLOAD_RETRYING);

                        if (callback) {
                            callback(id);
                        }
                        else {
                            self._handler.retry(id);
                        }
                    }, self._options.retry.autoAttemptDelay * 1000);

                    return true;
                }
            },

            _onBeforeAutoRetry: function (id, name) {
                this.log("Waiting " + this._options.retry.autoAttemptDelay + " seconds before retrying " + name + "...");
            },

            //return false if we should not attempt the requested retry
            _onBeforeManualRetry: function (id) {
                var itemLimit = this._currentItemLimit,
                    fileName;

                if (this._preventRetries[id]) {
                    this.log("Retries are forbidden for id " + id, "warn");
                    return false;
                }
                else if (this._handler.isValid(id)) {
                    fileName = this.getName(id);

                    if (this._options.callbacks.onManualRetry(id, fileName) === false) {
                        return false;
                    }

                    if (itemLimit > 0 && this._netUploadedOrQueued + 1 > itemLimit) {
                        this._itemError("retryFailTooManyItems");
                        return false;
                    }

                    this.log("Retrying upload for '" + fileName + "' (id: " + id + ")...");
                    return true;
                }
                else {
                    this.log("'" + id + "' is not a valid file ID", "error");
                    return false;
                }
            },

            _onCancel: function (id, name) {
                this._netUploadedOrQueued--;

                clearTimeout(this._retryTimeouts[id]);

                var storedItemIndex = qq.indexOf(this._storedIds, id);
                if (!this._options.autoUpload && storedItemIndex >= 0) {
                    this._storedIds.splice(storedItemIndex, 1);
                }

                this._uploadData.setStatus(id, qq.status.CANCELED);
            },

            _onComplete: function (id, name, result, xhr) {
                if (!result.success) {
                    this._netUploadedOrQueued--;
                    this._uploadData.setStatus(id, qq.status.UPLOAD_FAILED);

                    if (result[this._options.retry.preventRetryResponseProperty] === true) {
                        this._preventRetries[id] = true;
                    }
                }
                else {
                    if (result.thumbnailUrl) {
                        this._thumbnailUrls[id] = result.thumbnailUrl;
                    }

                    this._netUploaded++;
                    this._uploadData.setStatus(id, qq.status.UPLOAD_SUCCESSFUL);
                }

                this._maybeParseAndSendUploadError(id, name, result, xhr);

                return result.success ? true : false;
            },

            _onDelete: function (id) {
                this._uploadData.setStatus(id, qq.status.DELETING);
            },

            _onDeleteComplete: function (id, xhrOrXdr, isError) {
                var name = this.getName(id);

                if (isError) {
                    this._uploadData.setStatus(id, qq.status.DELETE_FAILED);
                    this.log("Delete request for '" + name + "' has failed.", "error");

                    // For error reporting, we only have access to the response status if this is not
                    // an `XDomainRequest`.
                    if (xhrOrXdr.withCredentials === undefined) {
                        this._options.callbacks.onError(id, name, "Delete request failed", xhrOrXdr);
                    }
                    else {
                        this._options.callbacks.onError(id, name, "Delete request failed with response code " + xhrOrXdr.status, xhrOrXdr);
                    }
                }
                else {
                    this._netUploadedOrQueued--;
                    this._netUploaded--;
                    this._handler.expunge(id);
                    this._uploadData.setStatus(id, qq.status.DELETED);
                    this.log("Delete request for '" + name + "' has succeeded.");
                }
            },

            _onInputChange: function (input) {
                var fileIndex;

                if (qq.supportedFeatures.ajaxUploading) {
                    for (fileIndex = 0; fileIndex < input.files.length; fileIndex++) {
                        this._annotateWithButtonId(input.files[fileIndex], input);
                    }

                    this.addFiles(input.files);
                }
                // Android 2.3.x will fire `onchange` even if no file has been selected
                else if (input.value.length > 0) {
                    this.addFiles(input);
                }

                qq.each(this._buttons, function (idx, button) {
                    button.reset();
                });
            },

            _onProgress: function (id, name, loaded, total) {
                this._totalProgress && this._totalProgress.onIndividualProgress(id, loaded, total);
            },

            _onSubmit: function (id, name) {
                //nothing to do yet in core uploader
            },

            _onSubmitCallbackSuccess: function (id, name) {
                this._onSubmit.apply(this, arguments);
                this._uploadData.setStatus(id, qq.status.SUBMITTED);
                this._onSubmitted.apply(this, arguments);

                if (this._options.autoUpload) {
                    this._options.callbacks.onSubmitted.apply(this, arguments);
                    this._uploadFile(id);
                }
                else {
                    this._storeForLater(id);
                    this._options.callbacks.onSubmitted.apply(this, arguments);
                }
            },

            _onSubmitDelete: function (id, onSuccessCallback, additionalMandatedParams) {
                var uuid = this.getUuid(id),
                    adjustedOnSuccessCallback;

                if (onSuccessCallback) {
                    adjustedOnSuccessCallback = qq.bind(onSuccessCallback, this, id, uuid, additionalMandatedParams);
                }

                if (this._isDeletePossible()) {
                    this._handleCheckedCallback({
                        name: "onSubmitDelete",
                        callback: qq.bind(this._options.callbacks.onSubmitDelete, this, id),
                        onSuccess: adjustedOnSuccessCallback ||
                                   qq.bind(this._deleteHandler.sendDelete, this, id, uuid, additionalMandatedParams),
                        identifier: id
                    });
                    return true;
                }
                else {
                    this.log("Delete request ignored for ID " + id + ", delete feature is disabled or request not possible " +
                             "due to CORS on a user agent that does not support pre-flighting.", "warn");
                    return false;
                }
            },

            _onSubmitted: function (id) {
                //nothing to do in the base uploader
            },

            _onTotalProgress: function (loaded, total) {
                this._options.callbacks.onTotalProgress(loaded, total);
            },

            _onUploadPrep: function (id) {
                // nothing to do in the core uploader for now
            },

            _onUpload: function (id, name) {
                this._uploadData.setStatus(id, qq.status.UPLOADING);
            },

            _onUploadChunk: function (id, chunkData) {
                //nothing to do in the base uploader
            },

            _onUploadStatusChange: function (id, oldStatus, newStatus) {
                // Make sure a "queued" retry attempt is canceled if the upload has been paused
                if (newStatus === qq.status.PAUSED) {
                    clearTimeout(this._retryTimeouts[id]);
                }
            },

            _onValidateBatchCallbackFailure: function (fileWrappers) {
                var self = this;

                qq.each(fileWrappers, function (idx, fileWrapper) {
                    self._fileOrBlobRejected(fileWrapper.id);
                });
            },

            _onValidateBatchCallbackSuccess: function (validationDescriptors, items, params, endpoint, button) {
                var errorMessage,
                    itemLimit = this._currentItemLimit,
                    proposedNetFilesUploadedOrQueued = this._netUploadedOrQueued;

                if (itemLimit === 0 || proposedNetFilesUploadedOrQueued <= itemLimit) {
                    if (items.length > 0) {
                        this._handleCheckedCallback({
                            name: "onValidate",
                            callback: qq.bind(this._options.callbacks.onValidate, this, validationDescriptors[0], button),
                            onSuccess: qq.bind(this._onValidateCallbackSuccess, this, items, 0, params, endpoint),
                            onFailure: qq.bind(this._onValidateCallbackFailure, this, items, 0, params, endpoint),
                            identifier: "Item '" + items[0].file.name + "', size: " + items[0].file.size
                        });
                    }
                    else {
                        this._itemError("noFilesError");
                    }
                }
                else {
                    this._onValidateBatchCallbackFailure(items);
                    errorMessage = this._options.messages.tooManyItemsError
                        .replace(/\{netItems\}/g, proposedNetFilesUploadedOrQueued)
                        .replace(/\{itemLimit\}/g, itemLimit);
                    this._batchError(errorMessage);
                }
            },

            _onValidateCallbackFailure: function (items, index, params, endpoint) {
                var nextIndex = index + 1;

                this._fileOrBlobRejected(items[index].id, items[index].file.name);

                this._maybeProcessNextItemAfterOnValidateCallback(false, items, nextIndex, params, endpoint);
            },

            _onValidateCallbackSuccess: function (items, index, params, endpoint) {
                var self = this,
                    nextIndex = index + 1,
                    validationDescriptor = this._getValidationDescriptor(items[index]);

                this._validateFileOrBlobData(items[index], validationDescriptor)
                    .then(
                    function () {
                        self._upload(items[index].id, params, endpoint);
                        self._maybeProcessNextItemAfterOnValidateCallback(true, items, nextIndex, params, endpoint);
                    },
                    function () {
                        self._maybeProcessNextItemAfterOnValidateCallback(false, items, nextIndex, params, endpoint);
                    }
                );
            },

            _prepareItemsForUpload: function (items, params, endpoint) {
                if (items.length === 0) {
                    this._itemError("noFilesError");
                    return;
                }

                var validationDescriptors = this._getValidationDescriptors(items),
                    buttonId = this._getButtonId(items[0].file),
                    button = this._getButton(buttonId);

                this._handleCheckedCallback({
                    name: "onValidateBatch",
                    callback: qq.bind(this._options.callbacks.onValidateBatch, this, validationDescriptors, button),
                    onSuccess: qq.bind(this._onValidateBatchCallbackSuccess, this, validationDescriptors, items, params, endpoint, button),
                    onFailure: qq.bind(this._onValidateBatchCallbackFailure, this, items),
                    identifier: "batch validation"
                });
            },

            _preventLeaveInProgress: function () {
                var self = this;

                this._disposeSupport.attach(window, "beforeunload", function (e) {
                    if (self.getInProgress()) {
                        e = e || window.event;
                        // for ie, ff
                        e.returnValue = self._options.messages.onLeave;
                        // for webkit
                        return self._options.messages.onLeave;
                    }
                });
            },

            // Attempts to refresh session data only if the `qq.Session` module exists
            // and a session endpoint has been specified.  The `onSessionRequestComplete`
            // callback will be invoked once the refresh is complete.
            _refreshSessionData: function () {
                var self = this,
                    options = this._options.session;

                /* jshint eqnull:true */
                if (qq.Session && this._options.session.endpoint != null) {
                    if (!this._session) {
                        qq.extend(options, this._options.cors);

                        options.log = qq.bind(this.log, this);
                        options.addFileRecord = qq.bind(this._addCannedFile, this);

                        this._session = new qq.Session(options);
                    }

                    setTimeout(function () {
                        self._session.refresh().then(function (response, xhrOrXdr) {
                            self._sessionRequestComplete();
                            self._options.callbacks.onSessionRequestComplete(response, true, xhrOrXdr);

                        }, function (response, xhrOrXdr) {

                            self._options.callbacks.onSessionRequestComplete(response, false, xhrOrXdr);
                        });
                    }, 0);
                }
            },

            _sessionRequestComplete: function () {
            },

            _setSize: function (id, newSize) {
                this._uploadData.updateSize(id, newSize);
                this._totalProgress && this._totalProgress.onNewSize(id);
            },

            _shouldAutoRetry: function (id, name, responseJSON) {
                var uploadData = this._uploadData.retrieve({id: id});

                /*jshint laxbreak: true */
                if (!this._preventRetries[id]
                    && this._options.retry.enableAuto
                    && uploadData.status !== qq.status.PAUSED) {

                    if (this._autoRetries[id] === undefined) {
                        this._autoRetries[id] = 0;
                    }

                    if (this._autoRetries[id] < this._options.retry.maxAutoAttempts) {
                        this._autoRetries[id] += 1;
                        return true;
                    }
                }

                return false;
            },

            _storeForLater: function (id) {
                this._storedIds.push(id);
            },

            // Maps a file with the button that was used to select it.
            _trackButton: function (id) {
                var buttonId;

                if (qq.supportedFeatures.ajaxUploading) {
                    buttonId = this._handler.getFile(id).qqButtonId;
                }
                else {
                    buttonId = this._getButtonId(this._handler.getInput(id));
                }

                if (buttonId) {
                    this._buttonIdsForFileIds[id] = buttonId;
                }
            },

            _updateFormSupportAndParams: function (formElementOrId) {
                this._options.form.element = formElementOrId;

                this._formSupport = qq.FormSupport && new qq.FormSupport(
                        this._options.form, qq.bind(this.uploadStoredFiles, this), qq.bind(this.log, this)
                    );

                if (this._formSupport && this._formSupport.attachedToForm) {
                    this._paramsStore.addReadOnly(null, this._formSupport.getFormInputsAsObject);

                    this._options.autoUpload = this._formSupport.newAutoUpload;
                    if (this._formSupport.newEndpoint) {
                        this.setEndpoint(this._formSupport.newEndpoint);
                    }
                }
            },

            _upload: function (id, params, endpoint) {
                var name = this.getName(id);

                if (params) {
                    this.setParams(params, id);
                }

                if (endpoint) {
                    this.setEndpoint(endpoint, id);
                }

                this._handleCheckedCallback({
                    name: "onSubmit",
                    callback: qq.bind(this._options.callbacks.onSubmit, this, id, name),
                    onSuccess: qq.bind(this._onSubmitCallbackSuccess, this, id, name),
                    onFailure: qq.bind(this._fileOrBlobRejected, this, id, name),
                    identifier: id
                });
            },

            _uploadFile: function (id) {
                if (!this._handler.upload(id)) {
                    this._uploadData.setStatus(id, qq.status.QUEUED);
                }
            },

            _uploadStoredFiles: function () {
                var idToUpload, stillSubmitting,
                    self = this;

                while (this._storedIds.length) {
                    idToUpload = this._storedIds.shift();
                    this._uploadFile(idToUpload);
                }

                // If we are still waiting for some files to clear validation, attempt to upload these again in a bit
                stillSubmitting = this.getUploads({status: qq.status.SUBMITTING}).length;
                if (stillSubmitting) {
                    qq.log("Still waiting for " + stillSubmitting +
                           " files to clear submit queue. Will re-parse stored IDs array shortly.");
                    setTimeout(function () {
                        self._uploadStoredFiles();
                    }, 1000);
                }
            },

            /**
             * Performs some internal validation checks on an item, defined in the `validation` option.
             *
             * @param fileWrapper Wrapper containing a `file` along with an `id`
             * @param validationDescriptor Normalized information about the item (`size`, `name`).
             * @returns qq.Promise with appropriate callbacks invoked depending on the validity of the file
             * @private
             */
            _validateFileOrBlobData: function (fileWrapper, validationDescriptor) {
                var self = this,
                    file = (function () {
                        if (fileWrapper.file instanceof qq.BlobProxy) {
                            return fileWrapper.file.referenceBlob;
                        }
                        return fileWrapper.file;
                    }()),
                    name = validationDescriptor.name,
                    size = validationDescriptor.size,
                    buttonId = this._getButtonId(fileWrapper.file),
                    validationBase = this._getValidationBase(buttonId),
                    validityChecker = new qq.Promise();

                validityChecker.then(
                    function () {
                    },
                    function () {
                        self._fileOrBlobRejected(fileWrapper.id, name);
                    });

                if (qq.isFileOrInput(file) && !this._isAllowedExtension(validationBase.allowedExtensions, name)) {
                    this._itemError("typeError", name, file);
                    return validityChecker.failure();
                }

                if (size === 0) {
                    this._itemError("emptyError", name, file);
                    return validityChecker.failure();
                }

                if (size > 0 && validationBase.sizeLimit && size > validationBase.sizeLimit) {
                    this._itemError("sizeError", name, file);
                    return validityChecker.failure();
                }

                if (size > 0 && size < validationBase.minSizeLimit) {
                    this._itemError("minSizeError", name, file);
                    return validityChecker.failure();
                }

                if (qq.ImageValidation && qq.supportedFeatures.imagePreviews && qq.isFile(file)) {
                    new qq.ImageValidation(file, qq.bind(self.log, self)).validate(validationBase.image).then(
                        validityChecker.success,
                        function (errorCode) {
                            self._itemError(errorCode + "ImageError", name, file);
                            validityChecker.failure();
                        }
                    );
                }
                else {
                    validityChecker.success();
                }

                return validityChecker;
            },

            _wrapCallbacks: function () {
                var self, safeCallback, prop;

                self = this;

                safeCallback = function (name, callback, args) {
                    var errorMsg;

                    try {
                        return callback.apply(self, args);
                    }
                    catch (exception) {
                        errorMsg = exception.message || exception.toString();
                        self.log("Caught exception in '" + name + "' callback - " + errorMsg, "error");
                    }
                };

                /* jshint forin: false, loopfunc: true */
                for (prop in this._options.callbacks) {
                    (function () {
                        var callbackName, callbackFunc;
                        callbackName = prop;
                        callbackFunc = self._options.callbacks[callbackName];
                        self._options.callbacks[callbackName] = function () {
                            return safeCallback(callbackName, callbackFunc, arguments);
                        };
                    }());
                }
            }
        };
    }());

    /*globals qq*/
    (function () {
        "use strict";

        qq.FineUploaderBasic = function (o) {
            var self = this;

            // These options define FineUploaderBasic mode.
            this._options = {
                debug: false,
                button: null,
                multiple: true,
                maxConnections: 3,
                disableCancelForFormUploads: false,
                autoUpload: true,

                request: {
                    customHeaders: {},
                    endpoint: "/server/upload",
                    filenameParam: "qqfilename",
                    forceMultipart: true,
                    inputName: "qqfile",
                    method: "POST",
                    params: {},
                    paramsInBody: true,
                    totalFileSizeName: "qqtotalfilesize",
                    uuidName: "qquuid"
                },

                validation: {
                    allowedExtensions: [],
                    sizeLimit: 0,
                    minSizeLimit: 0,
                    itemLimit: 0,
                    stopOnFirstInvalidFile: true,
                    acceptFiles: null,
                    image: {
                        maxHeight: 0,
                        maxWidth: 0,
                        minHeight: 0,
                        minWidth: 0
                    }
                },

                callbacks: {
                    onSubmit: function (id, name) {
                    },
                    onSubmitted: function (id, name) {
                    },
                    onComplete: function (id, name, responseJSON, maybeXhr) {
                    },
                    onAllComplete: function (successful, failed) {
                    },
                    onCancel: function (id, name) {
                    },
                    onUpload: function (id, name) {
                    },
                    onUploadChunk: function (id, name, chunkData) {
                    },
                    onUploadChunkSuccess: function (id, chunkData, responseJSON, xhr) {
                    },
                    onResume: function (id, fileName, chunkData) {
                    },
                    onProgress: function (id, name, loaded, total) {
                    },
                    onTotalProgress: function (loaded, total) {
                    },
                    onError: function (id, name, reason, maybeXhrOrXdr) {
                    },
                    onAutoRetry: function (id, name, attemptNumber) {
                    },
                    onManualRetry: function (id, name) {
                    },
                    onValidateBatch: function (fileOrBlobData) {
                    },
                    onValidate: function (fileOrBlobData) {
                    },
                    onSubmitDelete: function (id) {
                    },
                    onDelete: function (id) {
                    },
                    onDeleteComplete: function (id, xhrOrXdr, isError) {
                    },
                    onPasteReceived: function (blob) {
                    },
                    onStatusChange: function (id, oldStatus, newStatus) {
                    },
                    onSessionRequestComplete: function (response, success, xhrOrXdr) {
                    }
                },

                messages: {
                    typeError: "{file} has an invalid extension. Valid extension(s): {extensions}.",
                    sizeError: "{file} is too large, maximum file size is {sizeLimit}.",
                    minSizeError: "{file} is too small, minimum file size is {minSizeLimit}.",
                    emptyError: "{file} is empty, please select files again without it.",
                    noFilesError: "No files to upload.",
                    tooManyItemsError: "Too many items ({netItems}) would be uploaded.  Item limit is {itemLimit}.",
                    maxHeightImageError: "Image is too tall.",
                    maxWidthImageError: "Image is too wide.",
                    minHeightImageError: "Image is not tall enough.",
                    minWidthImageError: "Image is not wide enough.",
                    retryFailTooManyItems: "Retry failed - you have reached your file limit.",
                    onLeave: "The files are being uploaded, if you leave now the upload will be canceled.",
                    unsupportedBrowserIos8Safari: "Unrecoverable error - this browser does not permit file uploading of any kind due to serious bugs in iOS8 Safari.  Please use iOS8 Chrome until Apple fixes these issues."
                },

                retry: {
                    enableAuto: false,
                    maxAutoAttempts: 3,
                    autoAttemptDelay: 5,
                    preventRetryResponseProperty: "preventRetry"
                },

                classes: {
                    buttonHover: "qq-upload-button-hover",
                    buttonFocus: "qq-upload-button-focus"
                },

                chunking: {
                    enabled: false,
                    concurrent: {
                        enabled: false
                    },
                    mandatory: false,
                    paramNames: {
                        partIndex: "qqpartindex",
                        partByteOffset: "qqpartbyteoffset",
                        chunkSize: "qqchunksize",
                        totalFileSize: "qqtotalfilesize",
                        totalParts: "qqtotalparts"
                    },
                    partSize: 2000000,
                    // only relevant for traditional endpoints, only required when concurrent.enabled === true
                    success: {
                        endpoint: null
                    }
                },

                resume: {
                    enabled: false,
                    recordsExpireIn: 7, //days
                    paramNames: {
                        resuming: "qqresume"
                    }
                },

                formatFileName: function (fileOrBlobName) {
                    return fileOrBlobName;
                },

                text: {
                    defaultResponseError: "Upload failure reason unknown",
                    fileInputTitle: "file input",
                    sizeSymbols: ["kB", "MB", "GB", "TB", "PB", "EB"]
                },

                deleteFile: {
                    enabled: false,
                    method: "DELETE",
                    endpoint: "/server/upload",
                    customHeaders: {},
                    params: {}
                },

                cors: {
                    expected: false,
                    sendCredentials: false,
                    allowXdr: false
                },

                blobs: {
                    defaultName: "misc_data"
                },

                paste: {
                    targetElement: null,
                    defaultName: "pasted_image"
                },

                camera: {
                    ios: false,

                    // if ios is true: button is null means target the default button, otherwise target the button specified
                    button: null
                },

                // This refers to additional upload buttons to be handled by Fine Uploader.
                // Each element is an object, containing `element` as the only required
                // property.  The `element` must be a container that will ultimately
                // contain an invisible `<input type="file">` created by Fine Uploader.
                // Optional properties of each object include `multiple`, `validation`,
                // and `folders`.
                extraButtons: [],

                // Depends on the session module.  Used to query the server for an initial file list
                // during initialization and optionally after a `reset`.
                session: {
                    endpoint: null,
                    params: {},
                    customHeaders: {},
                    refreshOnReset: true
                },

                // Send parameters associated with an existing form along with the files
                form: {
                    // Element ID, HTMLElement, or null
                    element: "qq-form",

                    // Overrides the base `autoUpload`, unless `element` is null.
                    autoUpload: false,

                    // true = upload files on form submission (and squelch submit event)
                    interceptSubmit: true
                },

                workarounds: {
                    iosEmptyVideos: true,
                    ios8SafariUploads: true,
                    ios8BrowserCrash: false
                }
            };

            // Replace any default options with user defined ones
            qq.extend(this._options, o, true);

            this._buttons = [];
            this._extraButtonSpecs = {};
            this._buttonIdsForFileIds = [];

            this._wrapCallbacks();
            this._disposeSupport = new qq.DisposeSupport();

            this._storedIds = [];
            this._autoRetries = [];
            this._retryTimeouts = [];
            this._preventRetries = [];
            this._thumbnailUrls = [];

            this._netUploadedOrQueued = 0;
            this._netUploaded = 0;
            this._uploadData = this._createUploadDataTracker();

            this._initFormSupportAndParams();

            this._customHeadersStore = this._createStore(this._options.request.customHeaders);
            this._deleteFileCustomHeadersStore = this._createStore(this._options.deleteFile.customHeaders);

            this._deleteFileParamsStore = this._createStore(this._options.deleteFile.params);

            this._endpointStore = this._createStore(this._options.request.endpoint);
            this._deleteFileEndpointStore = this._createStore(this._options.deleteFile.endpoint);

            this._handler = this._createUploadHandler();

            this._deleteHandler = qq.DeleteFileAjaxRequester && this._createDeleteHandler();

            if (this._options.button) {
                this._defaultButtonId = this._createUploadButton({
                    element: this._options.button,
                    title: this._options.text.fileInputTitle
                }).getButtonId();
            }

            this._generateExtraButtonSpecs();

            this._handleCameraAccess();

            if (this._options.paste.targetElement) {
                if (qq.PasteSupport) {
                    this._pasteHandler = this._createPasteHandler();
                }
                else {
                    this.log("Paste support module not found", "error");
                }
            }

            this._preventLeaveInProgress();

            this._imageGenerator = qq.ImageGenerator && new qq.ImageGenerator(qq.bind(this.log, this));
            this._refreshSessionData();

            this._succeededSinceLastAllComplete = [];
            this._failedSinceLastAllComplete = [];

            if (qq.TotalProgress && qq.supportedFeatures.progressBar) {
                this._totalProgress = new qq.TotalProgress(
                    qq.bind(this._onTotalProgress, this),

                    function (id) {
                        var entry = self._uploadData.retrieve({id: id});
                        return (entry && entry.size) || 0;
                    }
                );
            }

            this._currentItemLimit = this._options.validation.itemLimit;
        };

        // Define the private & public API methods.
        qq.FineUploaderBasic.prototype = qq.basePublicApi;
        qq.extend(qq.FineUploaderBasic.prototype, qq.basePrivateApi);
    }());

    /*globals qq, XDomainRequest*/
    /** Generic class for sending non-upload ajax requests and handling the associated responses **/
    qq.AjaxRequester = function (o) {
        "use strict";

        var log, shouldParamsBeInQueryString,
            queue = [],
            requestData = {},
            options = {
                acceptHeader: null,
                validMethods: ["PATCH", "POST", "PUT"],
                method: "POST",
                contentType: "application/x-www-form-urlencoded",
                maxConnections: 3,
                customHeaders: {},
                endpointStore: {},
                paramsStore: {},
                mandatedParams: {},
                allowXRequestedWithAndCacheControl: true,
                successfulResponseCodes: {
                    DELETE: [200, 202, 204],
                    PATCH: [200, 201, 202, 203, 204],
                    POST: [200, 201, 202, 203, 204],
                    PUT: [200, 201, 202, 203, 204],
                    GET: [200]
                },
                cors: {
                    expected: false,
                    sendCredentials: false
                },
                log: function (str, level) {
                },
                onSend: function (id) {
                },
                onComplete: function (id, xhrOrXdr, isError) {
                },
                onProgress: null
            };

        qq.extend(options, o);
        log = options.log;

        if (qq.indexOf(options.validMethods, options.method) < 0) {
            throw new Error("'" + options.method + "' is not a supported method for this type of request!");
        }

        // [Simple methods](http://www.w3.org/TR/cors/#simple-method)
        // are defined by the W3C in the CORS spec as a list of methods that, in part,
        // make a CORS request eligible to be exempt from preflighting.
        function isSimpleMethod() {
            return qq.indexOf(["GET", "POST", "HEAD"], options.method) >= 0;
        }

        // [Simple headers](http://www.w3.org/TR/cors/#simple-header)
        // are defined by the W3C in the CORS spec as a list of headers that, in part,
        // make a CORS request eligible to be exempt from preflighting.
        function containsNonSimpleHeaders(headers) {
            var containsNonSimple = false;

            qq.each(containsNonSimple, function (idx, header) {
                if (qq.indexOf(["Accept", "Accept-Language", "Content-Language", "Content-Type"], header) < 0) {
                    containsNonSimple = true;
                    return false;
                }
            });

            return containsNonSimple;
        }

        function isXdr(xhr) {
            //The `withCredentials` test is a commonly accepted way to determine if XHR supports CORS.
            return options.cors.expected && xhr.withCredentials === undefined;
        }

        // Returns either a new `XMLHttpRequest` or `XDomainRequest` instance.
        function getCorsAjaxTransport() {
            var xhrOrXdr;

            if (window.XMLHttpRequest || window.ActiveXObject) {
                xhrOrXdr = qq.createXhrInstance();

                if (xhrOrXdr.withCredentials === undefined) {
                    xhrOrXdr = new XDomainRequest();
                    // Workaround for XDR bug in IE9 - https://social.msdn.microsoft.com/Forums/ie/en-US/30ef3add-767c-4436-b8a9-f1ca19b4812e/ie9-rtm-xdomainrequest-issued-requests-may-abort-if-all-event-handlers-not-specified?forum=iewebdevelopment
                    xhrOrXdr.onload = function () {
                    };
                    xhrOrXdr.onerror = function () {
                    };
                    xhrOrXdr.ontimeout = function () {
                    };
                    xhrOrXdr.onprogress = function () {
                    };
                }
            }

            return xhrOrXdr;
        }

        // Returns either a new XHR/XDR instance, or an existing one for the associated `File` or `Blob`.
        function getXhrOrXdr(id, suppliedXhr) {
            var xhrOrXdr = requestData[id].xhr;

            if (!xhrOrXdr) {
                if (suppliedXhr) {
                    xhrOrXdr = suppliedXhr;
                }
                else {
                    if (options.cors.expected) {
                        xhrOrXdr = getCorsAjaxTransport();
                    }
                    else {
                        xhrOrXdr = qq.createXhrInstance();
                    }
                }

                requestData[id].xhr = xhrOrXdr;
            }

            return xhrOrXdr;
        }

        // Removes element from queue, sends next request
        function dequeue(id) {
            var i = qq.indexOf(queue, id),
                max = options.maxConnections,
                nextId;

            delete requestData[id];
            queue.splice(i, 1);

            if (queue.length >= max && i < max) {
                nextId = queue[max - 1];
                sendRequest(nextId);
            }
        }

        function onComplete(id, xdrError) {
            var xhr = getXhrOrXdr(id),
                method = options.method,
                isError = xdrError === true;

            dequeue(id);

            if (isError) {
                log(method + " request for " + id + " has failed", "error");
            }
            else if (!isXdr(xhr) && !isResponseSuccessful(xhr.status)) {
                isError = true;
                log(method + " request for " + id + " has failed - response code " + xhr.status, "error");
            }

            options.onComplete(id, xhr, isError);
        }

        function getParams(id) {
            var onDemandParams = requestData[id].additionalParams,
                mandatedParams = options.mandatedParams,
                params;

            if (options.paramsStore.get) {
                params = options.paramsStore.get(id);
            }

            if (onDemandParams) {
                qq.each(onDemandParams, function (name, val) {
                    params = params || {};
                    params[name] = val;
                });
            }

            if (mandatedParams) {
                qq.each(mandatedParams, function (name, val) {
                    params = params || {};
                    params[name] = val;
                });
            }

            return params;
        }

        function sendRequest(id, optXhr) {
            var xhr = getXhrOrXdr(id, optXhr),
                method = options.method,
                params = getParams(id),
                payload = requestData[id].payload,
                url;

            options.onSend(id);

            url = createUrl(id, params, requestData[id].additionalQueryParams);

            // XDR and XHR status detection APIs differ a bit.
            if (isXdr(xhr)) {
                xhr.onload = getXdrLoadHandler(id);
                xhr.onerror = getXdrErrorHandler(id);
            }
            else {
                xhr.onreadystatechange = getXhrReadyStateChangeHandler(id);
            }

            registerForUploadProgress(id);

            // The last parameter is assumed to be ignored if we are actually using `XDomainRequest`.
            xhr.open(method, url, true);

            // Instruct the transport to send cookies along with the CORS request,
            // unless we are using `XDomainRequest`, which is not capable of this.
            if (options.cors.expected && options.cors.sendCredentials && !isXdr(xhr)) {
                xhr.withCredentials = true;
            }

            setHeaders(id);

            log("Sending " + method + " request for " + id);

            if (payload) {
                xhr.send(payload);
            }
            else if (shouldParamsBeInQueryString || !params) {
                xhr.send();
            }
            else if (params && options.contentType && options.contentType.toLowerCase().indexOf("application/x-www-form-urlencoded") >= 0) {
                xhr.send(qq.obj2url(params, ""));
            }
            else if (params && options.contentType && options.contentType.toLowerCase().indexOf("application/json") >= 0) {
                xhr.send(JSON.stringify(params));
            }
            else {
                xhr.send(params);
            }

            return xhr;
        }

        function createUrl(id, params, additionalQueryParams) {
            var endpoint = options.endpointStore.get(id),
                addToPath = requestData[id].addToPath;

            /*jshint -W116,-W041 */
            if (addToPath != undefined) {
                endpoint += "/" + addToPath;
            }

            if (shouldParamsBeInQueryString && params) {
                endpoint = qq.obj2url(params, endpoint);
            }

            if (additionalQueryParams) {
                endpoint = qq.obj2url(additionalQueryParams, endpoint);
            }

            return endpoint;
        }

        // Invoked by the UA to indicate a number of possible states that describe
        // a live `XMLHttpRequest` transport.
        function getXhrReadyStateChangeHandler(id) {
            return function () {
                if (getXhrOrXdr(id).readyState === 4) {
                    onComplete(id);
                }
            };
        }

        function registerForUploadProgress(id) {
            var onProgress = options.onProgress;

            if (onProgress) {
                getXhrOrXdr(id).upload.onprogress = function (e) {
                    if (e.lengthComputable) {
                        onProgress(id, e.loaded, e.total);
                    }
                };
            }
        }

        // This will be called by IE to indicate **success** for an associated
        // `XDomainRequest` transported request.
        function getXdrLoadHandler(id) {
            return function () {
                onComplete(id);
            };
        }

        // This will be called by IE to indicate **failure** for an associated
        // `XDomainRequest` transported request.
        function getXdrErrorHandler(id) {
            return function () {
                onComplete(id, true);
            };
        }

        function setHeaders(id) {
            var xhr = getXhrOrXdr(id),
                customHeaders = options.customHeaders,
                onDemandHeaders = requestData[id].additionalHeaders || {},
                method = options.method,
                allHeaders = {};

            // If XDomainRequest is being used, we can't set headers, so just ignore this block.
            if (!isXdr(xhr)) {
                options.acceptHeader && xhr.setRequestHeader("Accept", options.acceptHeader);

                // Only attempt to add X-Requested-With & Cache-Control if permitted
                if (options.allowXRequestedWithAndCacheControl) {
                    // Do not add X-Requested-With & Cache-Control if this is a cross-origin request
                    // OR the cross-origin request contains a non-simple method or header.
                    // This is done to ensure a preflight is not triggered exclusively based on the
                    // addition of these 2 non-simple headers.
                    if (!options.cors.expected || (!isSimpleMethod() || containsNonSimpleHeaders(customHeaders))) {
                        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                        xhr.setRequestHeader("Cache-Control", "no-cache");
                    }
                }

                if (options.contentType && (method === "POST" || method === "PUT")) {
                    xhr.setRequestHeader("Content-Type", options.contentType);
                }

                qq.extend(allHeaders, qq.isFunction(customHeaders) ? customHeaders(id) : customHeaders);
                qq.extend(allHeaders, onDemandHeaders);

                qq.each(allHeaders, function (name, val) {
                    xhr.setRequestHeader(name, val);
                });
            }
        }

        function isResponseSuccessful(responseCode) {
            return qq.indexOf(options.successfulResponseCodes[options.method], responseCode) >= 0;
        }

        function prepareToSend(id, optXhr, addToPath, additionalParams, additionalQueryParams, additionalHeaders, payload) {
            requestData[id] = {
                addToPath: addToPath,
                additionalParams: additionalParams,
                additionalQueryParams: additionalQueryParams,
                additionalHeaders: additionalHeaders,
                payload: payload
            };

            var len = queue.push(id);

            // if too many active connections, wait...
            if (len <= options.maxConnections) {
                return sendRequest(id, optXhr);
            }
        }

        shouldParamsBeInQueryString = options.method === "GET" || options.method === "DELETE";

        qq.extend(this, {
            // Start the process of sending the request.  The ID refers to the file associated with the request.
            initTransport: function (id) {
                var path, params, headers, payload, cacheBuster, additionalQueryParams;

                return {
                    // Optionally specify the end of the endpoint path for the request.
                    withPath: function (appendToPath) {
                        path = appendToPath;
                        return this;
                    },

                    // Optionally specify additional parameters to send along with the request.
                    // These will be added to the query string for GET/DELETE requests or the payload
                    // for POST/PUT requests.  The Content-Type of the request will be used to determine
                    // how these parameters should be formatted as well.
                    withParams: function (additionalParams) {
                        params = additionalParams;
                        return this;
                    },

                    withQueryParams: function (_additionalQueryParams_) {
                        additionalQueryParams = _additionalQueryParams_;
                        return this;
                    },

                    // Optionally specify additional headers to send along with the request.
                    withHeaders: function (additionalHeaders) {
                        headers = additionalHeaders;
                        return this;
                    },

                    // Optionally specify a payload/body for the request.
                    withPayload: function (thePayload) {
                        payload = thePayload;
                        return this;
                    },

                    // Appends a cache buster (timestamp) to the request URL as a query parameter (only if GET or DELETE)
                    withCacheBuster: function () {
                        cacheBuster = true;
                        return this;
                    },

                    // Send the constructed request.
                    send: function (optXhr) {
                        if (cacheBuster && qq.indexOf(["GET", "DELETE"], options.method) >= 0) {
                            params.qqtimestamp = new Date().getTime();
                        }

                        return prepareToSend(id, optXhr, path, params, additionalQueryParams, headers, payload);
                    }
                };
            },

            canceled: function (id) {
                dequeue(id);
            }
        });
    };

    /* globals qq */
    /**
     * Common upload handler functions.
     *
     * @constructor
     */
    qq.UploadHandler = function (spec) {
        "use strict";

        var proxy = spec.proxy,
            fileState = {},
            onCancel = proxy.onCancel,
            getName = proxy.getName;

        qq.extend(this, {
            add: function (id, fileItem) {
                fileState[id] = fileItem;
                fileState[id].temp = {};
            },

            cancel: function (id) {
                var self = this,
                    cancelFinalizationEffort = new qq.Promise(),
                    onCancelRetVal = onCancel(id, getName(id), cancelFinalizationEffort);

                onCancelRetVal.then(function () {
                    if (self.isValid(id)) {
                        fileState[id].canceled = true;
                        self.expunge(id);
                    }
                    cancelFinalizationEffort.success();
                });
            },

            expunge: function (id) {
                delete fileState[id];
            },

            getThirdPartyFileId: function (id) {
                return fileState[id].key;
            },

            isValid: function (id) {
                return fileState[id] !== undefined;
            },

            reset: function () {
                fileState = {};
            },

            _getFileState: function (id) {
                return fileState[id];
            },

            _setThirdPartyFileId: function (id, thirdPartyFileId) {
                fileState[id].key = thirdPartyFileId;
            },

            _wasCanceled: function (id) {
                return !!fileState[id].canceled;
            }
        });
    };

    /*globals qq*/
    /**
     * Base upload handler module.  Controls more specific handlers.
     *
     * @param o Options.  Passed along to the specific handler submodule as well.
     * @param namespace [optional] Namespace for the specific handler.
     */
    qq.UploadHandlerController = function (o, namespace) {
        "use strict";

        var controller = this,
            chunkingPossible = false,
            concurrentChunkingPossible = false,
            chunking, preventRetryResponse, log, handler,

            options = {
                paramsStore: {},
                maxConnections: 3, // maximum number of concurrent uploads
                chunking: {
                    enabled: false,
                    multiple: {
                        enabled: false
                    }
                },
                log: function (str, level) {
                },
                onProgress: function (id, fileName, loaded, total) {
                },
                onComplete: function (id, fileName, response, xhr) {
                },
                onCancel: function (id, fileName) {
                },
                onUploadPrep: function (id) {
                }, // Called if non-trivial operations will be performed before onUpload
                onUpload: function (id, fileName) {
                },
                onUploadChunk: function (id, fileName, chunkData) {
                },
                onUploadChunkSuccess: function (id, chunkData, response, xhr) {
                },
                onAutoRetry: function (id, fileName, response, xhr) {
                },
                onResume: function (id, fileName, chunkData) {
                },
                onUuidChanged: function (id, newUuid) {
                },
                getName: function (id) {
                },
                setSize: function (id, newSize) {
                },
                isQueued: function (id) {
                },
                getIdsInProxyGroup: function (id) {
                },
                getIdsInBatch: function (id) {
                }
            },

            chunked = {
                // Called when each chunk has uploaded successfully
                done: function (id, chunkIdx, response, xhr) {
                    var chunkData = handler._getChunkData(id, chunkIdx);

                    handler._getFileState(id).attemptingResume = false;

                    delete handler._getFileState(id).temp.chunkProgress[chunkIdx];
                    handler._getFileState(id).loaded += chunkData.size;

                    options.onUploadChunkSuccess(id, handler._getChunkDataForCallback(chunkData), response, xhr);
                },

                // Called when all chunks have been successfully uploaded and we want to ask the handler to perform any
                // logic associated with closing out the file, such as combining the chunks.
                finalize: function (id) {
                    var size = options.getSize(id),
                        name = options.getName(id);

                    log("All chunks have been uploaded for " + id + " - finalizing....");
                    handler.finalizeChunks(id).then(
                        function (response, xhr) {
                            log("Finalize successful for " + id);

                            var normaizedResponse = upload.normalizeResponse(response, true);

                            options.onProgress(id, name, size, size);
                            handler._maybeDeletePersistedChunkData(id);
                            upload.cleanup(id, normaizedResponse, xhr);
                        },
                        function (response, xhr) {
                            var normaizedResponse = upload.normalizeResponse(response, false);

                            log("Problem finalizing chunks for file ID " + id + " - " + normaizedResponse.error, "error");

                            if (normaizedResponse.reset) {
                                chunked.reset(id);
                            }

                            if (!options.onAutoRetry(id, name, normaizedResponse, xhr)) {
                                upload.cleanup(id, normaizedResponse, xhr);
                            }
                        }
                    );
                },

                hasMoreParts: function (id) {
                    return !!handler._getFileState(id).chunking.remaining.length;
                },

                nextPart: function (id) {
                    var nextIdx = handler._getFileState(id).chunking.remaining.shift();

                    if (nextIdx >= handler._getTotalChunks(id)) {
                        nextIdx = null;
                    }

                    return nextIdx;
                },

                reset: function (id) {
                    log("Server or callback has ordered chunking effort to be restarted on next attempt for item ID " + id, "error");

                    handler._maybeDeletePersistedChunkData(id);
                    handler.reevaluateChunking(id);
                    handler._getFileState(id).loaded = 0;
                },

                sendNext: function (id) {
                    var size = options.getSize(id),
                        name = options.getName(id),
                        chunkIdx = chunked.nextPart(id),
                        chunkData = handler._getChunkData(id, chunkIdx),
                        resuming = handler._getFileState(id).attemptingResume,
                        inProgressChunks = handler._getFileState(id).chunking.inProgress || [];

                    if (handler._getFileState(id).loaded == null) {
                        handler._getFileState(id).loaded = 0;
                    }

                    // Don't follow-through with the resume attempt if the integrator returns false from onResume
                    if (resuming && options.onResume(id, name, chunkData) === false) {
                        chunked.reset(id);
                        chunkIdx = chunked.nextPart(id);
                        chunkData = handler._getChunkData(id, chunkIdx);
                        resuming = false;
                    }

                    // If all chunks have already uploaded successfully, we must be re-attempting the finalize step.
                    if (chunkIdx == null && inProgressChunks.length === 0) {
                        chunked.finalize(id);
                    }

                    // Send the next chunk
                    else {
                        log(qq.format("Sending chunked upload request for item {}.{}, bytes {}-{} of {}.", id, chunkIdx,
                            chunkData.start + 1, chunkData.end, size));
                        options.onUploadChunk(id, name, handler._getChunkDataForCallback(chunkData));
                        inProgressChunks.push(chunkIdx);
                        handler._getFileState(id).chunking.inProgress = inProgressChunks;

                        if (concurrentChunkingPossible) {
                            connectionManager.open(id, chunkIdx);
                        }

                        if (concurrentChunkingPossible && connectionManager.available() &&
                            handler._getFileState(id).chunking.remaining.length) {
                            chunked.sendNext(id);
                        }

                        handler.uploadChunk(id, chunkIdx, resuming).then(
                            // upload chunk success
                            function success(response, xhr) {
                                log("Chunked upload request succeeded for " + id + ", chunk " + chunkIdx);

                                handler.clearCachedChunk(id, chunkIdx);

                                var inProgressChunks = handler._getFileState(id).chunking.inProgress || [],
                                    responseToReport = upload.normalizeResponse(response, true),
                                    inProgressChunkIdx = qq.indexOf(inProgressChunks, chunkIdx);

                                log(qq.format("Chunk {} for file {} uploaded successfully.", chunkIdx, id));

                                chunked.done(id, chunkIdx, responseToReport, xhr);

                                if (inProgressChunkIdx >= 0) {
                                    inProgressChunks.splice(inProgressChunkIdx, 1);
                                }

                                handler._maybePersistChunkedState(id);

                                if (!chunked.hasMoreParts(id) && inProgressChunks.length === 0) {
                                    chunked.finalize(id);
                                }
                                else if (chunked.hasMoreParts(id)) {
                                    chunked.sendNext(id);
                                }
                                else {
                                    log(qq.format("File ID {} has no more chunks to send and these chunk indexes are still marked as in-progress: {}",
                                        id, JSON.stringify(inProgressChunks)));
                                }
                            },

                            // upload chunk failure
                            function failure(response, xhr) {
                                log("Chunked upload request failed for " + id + ", chunk " + chunkIdx);

                                handler.clearCachedChunk(id, chunkIdx);

                                var responseToReport = upload.normalizeResponse(response, false),
                                    inProgressIdx;

                                if (responseToReport.reset) {
                                    chunked.reset(id);
                                }
                                else {
                                    inProgressIdx = qq.indexOf(handler._getFileState(id).chunking.inProgress, chunkIdx);
                                    if (inProgressIdx >= 0) {
                                        handler._getFileState(id).chunking.inProgress.splice(inProgressIdx, 1);
                                        handler._getFileState(id).chunking.remaining.unshift(chunkIdx);
                                    }
                                }

                                // We may have aborted all other in-progress chunks for this file due to a failure.
                                // If so, ignore the failures associated with those aborts.
                                if (!handler._getFileState(id).temp.ignoreFailure) {
                                    // If this chunk has failed, we want to ignore all other failures of currently in-progress
                                    // chunks since they will be explicitly aborted
                                    if (concurrentChunkingPossible) {
                                        handler._getFileState(id).temp.ignoreFailure = true;

                                        log(qq.format("Going to attempt to abort these chunks: {}. These are currently in-progress: {}.",
                                            JSON.stringify(Object.keys(handler._getXhrs(id))),
                                            JSON.stringify(handler._getFileState(id).chunking.inProgress)));
                                        qq.each(handler._getXhrs(id), function (ckid, ckXhr) {
                                            log(qq.format("Attempting to abort file {}.{}. XHR readyState {}. ", id, ckid,
                                                ckXhr.readyState));
                                            ckXhr.abort();
                                            // Flag the transport, in case we are waiting for some other async operation
                                            // to complete before attempting to upload the chunk
                                            ckXhr._cancelled = true;
                                        });

                                        // We must indicate that all aborted chunks are no longer in progress
                                        handler.moveInProgressToRemaining(id);

                                        // Free up any connections used by these chunks, but don't allow any
                                        // other files to take up the connections (until we have exhausted all auto-retries)
                                        connectionManager.free(id, true);
                                    }

                                    if (!options.onAutoRetry(id, name, responseToReport, xhr)) {
                                        // If one chunk fails, abort all of the others to avoid odd race conditions that occur
                                        // if a chunk succeeds immediately after one fails before we have determined if the upload
                                        // is a failure or not.
                                        upload.cleanup(id, responseToReport, xhr);
                                    }
                                }
                            }
                        )
                            .done(function () {
                                handler.clearXhr(id, chunkIdx);
                            });
                    }
                }
            },

            connectionManager = {
                _open: [],
                _openChunks: {},
                _waiting: [],

                available: function () {
                    var max = options.maxConnections,
                        openChunkEntriesCount = 0,
                        openChunksCount = 0;

                    qq.each(connectionManager._openChunks, function (fileId, openChunkIndexes) {
                        openChunkEntriesCount++;
                        openChunksCount += openChunkIndexes.length;
                    });

                    return max - (connectionManager._open.length - openChunkEntriesCount + openChunksCount);
                },

                /**
                 * Removes element from queue, starts upload of next
                 */
                free: function (id, dontAllowNext) {
                    var allowNext = !dontAllowNext,
                        waitingIndex = qq.indexOf(connectionManager._waiting, id),
                        connectionsIndex = qq.indexOf(connectionManager._open, id),
                        nextId;

                    delete connectionManager._openChunks[id];

                    if (upload.getProxyOrBlob(id) instanceof qq.BlobProxy) {
                        log("Generated blob upload has ended for " + id + ", disposing generated blob.");
                        delete handler._getFileState(id).file;
                    }

                    // If this file was not consuming a connection, it was just waiting, so remove it from the waiting array
                    if (waitingIndex >= 0) {
                        connectionManager._waiting.splice(waitingIndex, 1);
                    }
                    // If this file was consuming a connection, allow the next file to be uploaded
                    else if (allowNext && connectionsIndex >= 0) {
                        connectionManager._open.splice(connectionsIndex, 1);

                        nextId = connectionManager._waiting.shift();
                        if (nextId >= 0) {
                            connectionManager._open.push(nextId);
                            upload.start(nextId);
                        }
                    }
                },

                getWaitingOrConnected: function () {
                    var waitingOrConnected = [];

                    // Chunked files may have multiple connections open per chunk (if concurrent chunking is enabled)
                    // We need to grab the file ID of any file that has at least one chunk consuming a connection.
                    qq.each(connectionManager._openChunks, function (fileId, chunks) {
                        if (chunks && chunks.length) {
                            waitingOrConnected.push(parseInt(fileId));
                        }
                    });

                    // For non-chunked files, only one connection will be consumed per file.
                    // This is where we aggregate those file IDs.
                    qq.each(connectionManager._open, function (idx, fileId) {
                        if (!connectionManager._openChunks[fileId]) {
                            waitingOrConnected.push(parseInt(fileId));
                        }
                    });

                    // There may be files waiting for a connection.
                    waitingOrConnected = waitingOrConnected.concat(connectionManager._waiting);

                    return waitingOrConnected;
                },

                isUsingConnection: function (id) {
                    return qq.indexOf(connectionManager._open, id) >= 0;
                },

                open: function (id, chunkIdx) {
                    if (chunkIdx == null) {
                        connectionManager._waiting.push(id);
                    }

                    if (connectionManager.available()) {
                        if (chunkIdx == null) {
                            connectionManager._waiting.pop();
                            connectionManager._open.push(id);
                        }
                        else {
                            (function () {
                                var openChunksEntry = connectionManager._openChunks[id] || [];
                                openChunksEntry.push(chunkIdx);
                                connectionManager._openChunks[id] = openChunksEntry;
                            }());
                        }

                        return true;
                    }

                    return false;
                },

                reset: function () {
                    connectionManager._waiting = [];
                    connectionManager._open = [];
                }
            },

            simple = {
                send: function (id, name) {
                    handler._getFileState(id).loaded = 0;

                    log("Sending simple upload request for " + id);
                    handler.uploadFile(id).then(
                        function (response, optXhr) {
                            log("Simple upload request succeeded for " + id);

                            var responseToReport = upload.normalizeResponse(response, true),
                                size = options.getSize(id);

                            options.onProgress(id, name, size, size);
                            upload.maybeNewUuid(id, responseToReport);
                            upload.cleanup(id, responseToReport, optXhr);
                        },

                        function (response, optXhr) {
                            log("Simple upload request failed for " + id);

                            var responseToReport = upload.normalizeResponse(response, false);

                            if (!options.onAutoRetry(id, name, responseToReport, optXhr)) {
                                upload.cleanup(id, responseToReport, optXhr);
                            }
                        }
                    );
                }
            },

            upload = {
                cancel: function (id) {
                    log("Cancelling " + id);
                    options.paramsStore.remove(id);
                    connectionManager.free(id);
                },

                cleanup: function (id, response, optXhr) {
                    var name = options.getName(id);

                    options.onComplete(id, name, response, optXhr);

                    if (handler._getFileState(id)) {
                        handler._clearXhrs && handler._clearXhrs(id);
                    }

                    connectionManager.free(id);
                },

                // Returns a qq.BlobProxy, or an actual File/Blob if no proxy is involved, or undefined
                // if none of these are available for the ID
                getProxyOrBlob: function (id) {
                    return (handler.getProxy && handler.getProxy(id)) ||
                           (handler.getFile && handler.getFile(id));
                },

                initHandler: function () {
                    var handlerType = namespace ? qq[namespace] : qq.traditional,
                        handlerModuleSubtype = qq.supportedFeatures.ajaxUploading ? "Xhr" : "Form";

                    handler = new handlerType[handlerModuleSubtype + "UploadHandler"](
                        options,
                        {
                            getDataByUuid: options.getDataByUuid,
                            getName: options.getName,
                            getSize: options.getSize,
                            getUuid: options.getUuid,
                            log: log,
                            onCancel: options.onCancel,
                            onProgress: options.onProgress,
                            onUuidChanged: options.onUuidChanged
                        }
                    );

                    if (handler._removeExpiredChunkingRecords) {
                        handler._removeExpiredChunkingRecords();
                    }
                },

                isDeferredEligibleForUpload: function (id) {
                    return options.isQueued(id);
                },

                // For Blobs that are part of a group of generated images, along with a reference image,
                // this will ensure the blobs in the group are uploaded in the order they were triggered,
                // even if some async processing must be completed on one or more Blobs first.
                maybeDefer: function (id, blob) {
                    // If we don't have a file/blob yet & no file/blob exists for this item, request it,
                    // and then submit the upload to the specific handler once the blob is available.
                    // ASSUMPTION: This condition will only ever be true if XHR uploading is supported.
                    if (blob && !handler.getFile(id) && blob instanceof qq.BlobProxy) {

                        // Blob creation may take some time, so the caller may want to update the
                        // UI to indicate that an operation is in progress, even before the actual
                        // upload begins and an onUpload callback is invoked.
                        options.onUploadPrep(id);

                        log("Attempting to generate a blob on-demand for " + id);
                        blob.create().then(function (generatedBlob) {
                                log("Generated an on-demand blob for " + id);

                                // Update record associated with this file by providing the generated Blob
                                handler.updateBlob(id, generatedBlob);

                                // Propagate the size for this generated Blob
                                options.setSize(id, generatedBlob.size);

                                // Order handler to recalculate chunking possibility, if applicable
                                handler.reevaluateChunking(id);

                                upload.maybeSendDeferredFiles(id);
                            },

                            // Blob could not be generated.  Fail the upload & attempt to prevent retries.  Also bubble error message.
                            function (errorMessage) {
                                var errorResponse = {};

                                if (errorMessage) {
                                    errorResponse.error = errorMessage;
                                }

                                log(qq.format("Failed to generate blob for ID {}.  Error message: {}.", id, errorMessage), "error");

                                options.onComplete(id, options.getName(id), qq.extend(errorResponse, preventRetryResponse), null);
                                upload.maybeSendDeferredFiles(id);
                                connectionManager.free(id);
                            });
                    }
                    else {
                        return upload.maybeSendDeferredFiles(id);
                    }

                    return false;
                },

                // Upload any grouped blobs, in the proper order, that are ready to be uploaded
                maybeSendDeferredFiles: function (id) {
                    var idsInGroup = options.getIdsInProxyGroup(id),
                        uploadedThisId = false;

                    if (idsInGroup && idsInGroup.length) {
                        log("Maybe ready to upload proxy group file " + id);

                        qq.each(idsInGroup, function (idx, idInGroup) {
                            if (upload.isDeferredEligibleForUpload(idInGroup) && !!handler.getFile(idInGroup)) {
                                uploadedThisId = idInGroup === id;
                                upload.now(idInGroup);
                            }
                            else if (upload.isDeferredEligibleForUpload(idInGroup)) {
                                return false;
                            }
                        });
                    }
                    else {
                        uploadedThisId = true;
                        upload.now(id);
                    }

                    return uploadedThisId;
                },

                maybeNewUuid: function (id, response) {
                    if (response.newUuid !== undefined) {
                        options.onUuidChanged(id, response.newUuid);
                    }
                },

                // The response coming from handler implementations may be in various formats.
                // Instead of hoping a promise nested 5 levels deep will always return an object
                // as its first param, let's just normalize the response here.
                normalizeResponse: function (originalResponse, successful) {
                    var response = originalResponse;

                    // The passed "response" param may not be a response at all.
                    // It could be a string, detailing the error, for example.
                    if (!qq.isObject(originalResponse)) {
                        response = {};

                        if (qq.isString(originalResponse) && !successful) {
                            response.error = originalResponse;
                        }
                    }

                    response.success = successful;

                    return response;
                },

                now: function (id) {
                    var name = options.getName(id);

                    if (!controller.isValid(id)) {
                        throw new qq.Error(id + " is not a valid file ID to upload!");
                    }

                    options.onUpload(id, name);

                    if (chunkingPossible && handler._shouldChunkThisFile(id)) {
                        chunked.sendNext(id);
                    }
                    else {
                        simple.send(id, name);
                    }
                },

                start: function (id) {
                    var blobToUpload = upload.getProxyOrBlob(id);

                    if (blobToUpload) {
                        return upload.maybeDefer(id, blobToUpload);
                    }
                    else {
                        upload.now(id);
                        return true;
                    }
                }
            };

        qq.extend(this, {
            /**
             * Adds file or file input to the queue
             **/
            add: function (id, file) {
                handler.add.apply(this, arguments);
            },

            /**
             * Sends the file identified by id
             */
            upload: function (id) {
                if (connectionManager.open(id)) {
                    return upload.start(id);
                }
                return false;
            },

            retry: function (id) {
                // On retry, if concurrent chunking has been enabled, we may have aborted all other in-progress chunks
                // for a file when encountering a failed chunk upload.  We then signaled the controller to ignore
                // all failures associated with these aborts.  We are now retrying, so we don't want to ignore
                // any more failures at this point.
                if (concurrentChunkingPossible) {
                    handler._getFileState(id).temp.ignoreFailure = false;
                }

                // If we are attempting to retry a file that is already consuming a connection, this is likely an auto-retry.
                // Just go ahead and ask the handler to upload again.
                if (connectionManager.isUsingConnection(id)) {
                    return upload.start(id);
                }

                // If we are attempting to retry a file that is not currently consuming a connection,
                // this is likely a manual retry attempt.  We will need to ensure a connection is available
                // before the retry commences.
                else {
                    return controller.upload(id);
                }
            },

            /**
             * Cancels file upload by id
             */
            cancel: function (id) {
                var cancelRetVal = handler.cancel(id);

                if (qq.isGenericPromise(cancelRetVal)) {
                    cancelRetVal.then(function () {
                        upload.cancel(id);
                    });
                }
                else if (cancelRetVal !== false) {
                    upload.cancel(id);
                }
            },

            /**
             * Cancels all queued or in-progress uploads
             */
            cancelAll: function () {
                var waitingOrConnected = connectionManager.getWaitingOrConnected(),
                    i;

                // ensure files are cancelled in reverse order which they were added
                // to avoid a flash of time where a queued file begins to upload before it is canceled
                if (waitingOrConnected.length) {
                    for (i = waitingOrConnected.length - 1; i >= 0; i--) {
                        controller.cancel(waitingOrConnected[i]);
                    }
                }

                connectionManager.reset();
            },

            // Returns a File, Blob, or the Blob/File for the reference/parent file if the targeted blob is a proxy.
            // Undefined if no file record is available.
            getFile: function (id) {
                if (handler.getProxy && handler.getProxy(id)) {
                    return handler.getProxy(id).referenceBlob;
                }

                return handler.getFile && handler.getFile(id);
            },

            // Returns true if the Blob associated with the ID is related to a proxy s
            isProxied: function (id) {
                return !!(handler.getProxy && handler.getProxy(id));
            },

            getInput: function (id) {
                if (handler.getInput) {
                    return handler.getInput(id);
                }
            },

            reset: function () {
                log("Resetting upload handler");
                controller.cancelAll();
                connectionManager.reset();
                handler.reset();
            },

            expunge: function (id) {
                if (controller.isValid(id)) {
                    return handler.expunge(id);
                }
            },

            /**
             * Determine if the file exists.
             */
            isValid: function (id) {
                return handler.isValid(id);
            },

            getResumableFilesData: function () {
                if (handler.getResumableFilesData) {
                    return handler.getResumableFilesData();
                }
                return [];
            },

            /**
             * This may or may not be implemented, depending on the handler.  For handlers where a third-party ID is
             * available (such as the "key" for Amazon S3), this will return that value.  Otherwise, the return value
             * will be undefined.
             *
             * @param id Internal file ID
             * @returns {*} Some identifier used by a 3rd-party service involved in the upload process
             */
            getThirdPartyFileId: function (id) {
                if (controller.isValid(id)) {
                    return handler.getThirdPartyFileId(id);
                }
            },

            /**
             * Attempts to pause the associated upload if the specific handler supports this and the file is "valid".
             * @param id ID of the upload/file to pause
             * @returns {boolean} true if the upload was paused
             */
            pause: function (id) {
                if (controller.isResumable(id) && handler.pause && controller.isValid(id) && handler.pause(id)) {
                    connectionManager.free(id);
                    handler.moveInProgressToRemaining(id);
                    return true;
                }
                return false;
            },

            // True if the file is eligible for pause/resume.
            isResumable: function (id) {
                return !!handler.isResumable && handler.isResumable(id);
            }
        });

        qq.extend(options, o);
        log = options.log;
        chunkingPossible = options.chunking.enabled && qq.supportedFeatures.chunking;
        concurrentChunkingPossible = chunkingPossible && options.chunking.concurrent.enabled;

        preventRetryResponse = (function () {
            var response = {};

            response[options.preventRetryParam] = true;

            return response;
        }());

        upload.initHandler();
    };

    /* globals qq */
    /**
     * Common APIs exposed to creators of upload via form/iframe handlers.  This is reused and possibly overridden
     * in some cases by specific form upload handlers.
     *
     * @constructor
     */
    qq.FormUploadHandler = function (spec) {
        "use strict";

        var options = spec.options,
            handler = this,
            proxy = spec.proxy,
            formHandlerInstanceId = qq.getUniqueId(),
            onloadCallbacks = {},
            detachLoadEvents = {},
            postMessageCallbackTimers = {},
            isCors = options.isCors,
            inputName = options.inputName,
            getUuid = proxy.getUuid,
            log = proxy.log,
            corsMessageReceiver = new qq.WindowReceiveMessage({log: log});

        /**
         * Remove any trace of the file from the handler.
         *
         * @param id ID of the associated file
         */
        function expungeFile(id) {
            delete detachLoadEvents[id];

            // If we are dealing with CORS, we might still be waiting for a response from a loaded iframe.
            // In that case, terminate the timer waiting for a message from the loaded iframe
            // and stop listening for any more messages coming from this iframe.
            if (isCors) {
                clearTimeout(postMessageCallbackTimers[id]);
                delete postMessageCallbackTimers[id];
                corsMessageReceiver.stopReceivingMessages(id);
            }

            var iframe = document.getElementById(handler._getIframeName(id));
            if (iframe) {
                // To cancel request set src to something else.  We use src="javascript:false;"
                // because it doesn't trigger ie6 prompt on https
                /* jshint scripturl:true */
                iframe.setAttribute("src", "javascript:false;");

                qq(iframe).remove();
            }
        }

        /**
         * @param iframeName `document`-unique Name of the associated iframe
         * @returns {*} ID of the associated file
         */
        function getFileIdForIframeName(iframeName) {
            return iframeName.split("_")[0];
        }

        /**
         * Generates an iframe to be used as a target for upload-related form submits.  This also adds the iframe
         * to the current `document`.  Note that the iframe is hidden from view.
         *
         * @param name Name of the iframe.
         * @returns {HTMLIFrameElement} The created iframe
         */
        function initIframeForUpload(name) {
            var iframe = qq.toElement("<iframe src='javascript:false;' name='" + name + "' />");

            iframe.setAttribute("id", name);

            iframe.style.display = "none";
            document.body.appendChild(iframe);

            return iframe;
        }

        /**
         * If we are in CORS mode, we must listen for messages (containing the server response) from the associated
         * iframe, since we cannot directly parse the content of the iframe due to cross-origin restrictions.
         *
         * @param iframe Listen for messages on this iframe.
         * @param callback Invoke this callback with the message from the iframe.
         */
        function registerPostMessageCallback(iframe, callback) {
            var iframeName = iframe.id,
                fileId = getFileIdForIframeName(iframeName),
                uuid = getUuid(fileId);

            onloadCallbacks[uuid] = callback;

            // When the iframe has loaded (after the server responds to an upload request)
            // declare the attempt a failure if we don't receive a valid message shortly after the response comes in.
            detachLoadEvents[fileId] = qq(iframe).attach("load", function () {
                if (handler.getInput(fileId)) {
                    log("Received iframe load event for CORS upload request (iframe name " + iframeName + ")");

                    postMessageCallbackTimers[iframeName] = setTimeout(function () {
                        var errorMessage = "No valid message received from loaded iframe for iframe name " + iframeName;
                        log(errorMessage, "error");
                        callback({
                            error: errorMessage
                        });
                    }, 1000);
                }
            });

            // Listen for messages coming from this iframe.  When a message has been received, cancel the timer
            // that declares the upload a failure if a message is not received within a reasonable amount of time.
            corsMessageReceiver.receiveMessage(iframeName, function (message) {
                log("Received the following window message: '" + message + "'");
                var fileId = getFileIdForIframeName(iframeName),
                    response = handler._parseJsonResponse(message),
                    uuid = response.uuid,
                    onloadCallback;

                if (uuid && onloadCallbacks[uuid]) {
                    log("Handling response for iframe name " + iframeName);
                    clearTimeout(postMessageCallbackTimers[iframeName]);
                    delete postMessageCallbackTimers[iframeName];

                    handler._detachLoadEvent(iframeName);

                    onloadCallback = onloadCallbacks[uuid];

                    delete onloadCallbacks[uuid];
                    corsMessageReceiver.stopReceivingMessages(iframeName);
                    onloadCallback(response);
                }
                else if (!uuid) {
                    log("'" + message + "' does not contain a UUID - ignoring.");
                }
            });
        }

        qq.extend(this, new qq.UploadHandler(spec));

        qq.override(this, function (super_) {
            return {
                /**
                 * Adds File or Blob to the queue
                 **/
                add: function (id, fileInput) {
                    super_.add(id, {input: fileInput});

                    fileInput.setAttribute("name", inputName);

                    // remove file input from DOM
                    if (fileInput.parentNode) {
                        qq(fileInput).remove();
                    }
                },

                expunge: function (id) {
                    expungeFile(id);
                    super_.expunge(id);
                },

                isValid: function (id) {
                    return super_.isValid(id) &&
                           handler._getFileState(id).input !== undefined;
                }
            };
        });

        qq.extend(this, {
            getInput: function (id) {
                return handler._getFileState(id).input;
            },

            /**
             * This function either delegates to a more specific message handler if CORS is involved,
             * or simply registers a callback when the iframe has been loaded that invokes the passed callback
             * after determining if the content of the iframe is accessible.
             *
             * @param iframe Associated iframe
             * @param callback Callback to invoke after we have determined if the iframe content is accessible.
             */
            _attachLoadEvent: function (iframe, callback) {
                /*jslint eqeq: true*/
                var responseDescriptor;

                if (isCors) {
                    registerPostMessageCallback(iframe, callback);
                }
                else {
                    detachLoadEvents[iframe.id] = qq(iframe).attach("load", function () {
                        log("Received response for " + iframe.id);

                        // when we remove iframe from dom
                        // the request stops, but in IE load
                        // event fires
                        if (!iframe.parentNode) {
                            return;
                        }

                        try {
                            // fixing Opera 10.53
                            if (iframe.contentDocument &&
                                iframe.contentDocument.body &&
                                iframe.contentDocument.body.innerHTML == "false") {
                                // In Opera event is fired second time
                                // when body.innerHTML changed from false
                                // to server response approx. after 1 sec
                                // when we upload file with iframe
                                return;
                            }
                        }
                        catch (error) {
                            //IE may throw an "access is denied" error when attempting to access contentDocument on the iframe in some cases
                            log("Error when attempting to access iframe during handling of upload response (" + error.message + ")",
                                "error");
                            responseDescriptor = {success: false};
                        }

                        callback(responseDescriptor);
                    });
                }
            },

            /**
             * Creates an iframe with a specific document-unique name.
             *
             * @param id ID of the associated file
             * @returns {HTMLIFrameElement}
             */
            _createIframe: function (id) {
                var iframeName = handler._getIframeName(id);

                return initIframeForUpload(iframeName);
            },

            /**
             * Called when we are no longer interested in being notified when an iframe has loaded.
             *
             * @param id Associated file ID
             */
            _detachLoadEvent: function (id) {
                if (detachLoadEvents[id] !== undefined) {
                    detachLoadEvents[id]();
                    delete detachLoadEvents[id];
                }
            },

            /**
             * @param fileId ID of the associated file
             * @returns {string} The `document`-unique name of the iframe
             */
            _getIframeName: function (fileId) {
                return fileId + "_" + formHandlerInstanceId;
            },

            /**
             * Generates a form element and appends it to the `document`.  When the form is submitted, a specific iframe is targeted.
             * The name of the iframe is passed in as a property of the spec parameter, and must be unique in the `document`.  Note
             * that the form is hidden from view.
             *
             * @param spec An object containing various properties to be used when constructing the form.  Required properties are
             * currently: `method`, `endpoint`, `params`, `paramsInBody`, and `targetName`.
             * @returns {HTMLFormElement} The created form
             */
            _initFormForUpload: function (spec) {
                var method = spec.method,
                    endpoint = spec.endpoint,
                    params = spec.params,
                    paramsInBody = spec.paramsInBody,
                    targetName = spec.targetName,
                    form = qq.toElement("<form method='" + method + "' enctype='multipart/form-data'></form>"),
                    url = endpoint;

                if (paramsInBody) {
                    qq.obj2Inputs(params, form);
                }
                else {
                    url = qq.obj2url(params, endpoint);
                }

                form.setAttribute("action", url);
                form.setAttribute("target", targetName);
                form.style.display = "none";
                document.body.appendChild(form);

                return form;
            },

            /**
             * @param innerHtmlOrMessage JSON message
             * @returns {*} The parsed response, or an empty object if the response could not be parsed
             */
            _parseJsonResponse: function (innerHtmlOrMessage) {
                var response = {};

                try {
                    response = qq.parseJson(innerHtmlOrMessage);
                }
                catch (error) {
                    log("Error when attempting to parse iframe upload response (" + error.message + ")", "error");
                }

                return response;
            }
        });
    };

    /* globals qq */
    /**
     * Common API exposed to creators of XHR handlers.  This is reused and possibly overriding in some cases by specific
     * XHR upload handlers.
     *
     * @constructor
     */
    qq.XhrUploadHandler = function (spec) {
        "use strict";

        var handler = this,
            namespace = spec.options.namespace,
            proxy = spec.proxy,
            chunking = spec.options.chunking,
            resume = spec.options.resume,
            chunkFiles = chunking && spec.options.chunking.enabled && qq.supportedFeatures.chunking,
            resumeEnabled = resume && spec.options.resume.enabled && chunkFiles && qq.supportedFeatures.resume,
            getName = proxy.getName,
            getSize = proxy.getSize,
            getUuid = proxy.getUuid,
            getEndpoint = proxy.getEndpoint,
            getDataByUuid = proxy.getDataByUuid,
            onUuidChanged = proxy.onUuidChanged,
            onProgress = proxy.onProgress,
            log = proxy.log;

        function abort(id) {
            qq.each(handler._getXhrs(id), function (xhrId, xhr) {
                var ajaxRequester = handler._getAjaxRequester(id, xhrId);

                xhr.onreadystatechange = null;
                xhr.upload.onprogress = null;
                xhr.abort();
                ajaxRequester && ajaxRequester.canceled && ajaxRequester.canceled(id);
            });
        }

        qq.extend(this, new qq.UploadHandler(spec));

        qq.override(this, function (super_) {
            return {
                /**
                 * Adds File or Blob to the queue
                 **/
                add: function (id, blobOrProxy) {
                    if (qq.isFile(blobOrProxy) || qq.isBlob(blobOrProxy)) {
                        super_.add(id, {file: blobOrProxy});
                    }
                    else if (blobOrProxy instanceof qq.BlobProxy) {
                        super_.add(id, {proxy: blobOrProxy});
                    }
                    else {
                        throw new Error("Passed obj is not a File, Blob, or proxy");
                    }

                    handler._initTempState(id);
                    resumeEnabled && handler._maybePrepareForResume(id);
                },

                expunge: function (id) {
                    abort(id);
                    handler._maybeDeletePersistedChunkData(id);
                    handler._clearXhrs(id);
                    super_.expunge(id);
                }
            };
        });

        qq.extend(this, {
            // Clear the cached chunk `Blob` after we are done with it, just in case the `Blob` bytes are stored in memory.
            clearCachedChunk: function (id, chunkIdx) {
                delete handler._getFileState(id).temp.cachedChunks[chunkIdx];
            },

            clearXhr: function (id, chunkIdx) {
                var tempState = handler._getFileState(id).temp;

                if (tempState.xhrs) {
                    delete tempState.xhrs[chunkIdx];
                }
                if (tempState.ajaxRequesters) {
                    delete tempState.ajaxRequesters[chunkIdx];
                }
            },

            // Called when all chunks have been successfully uploaded.  Expected promissory return type.
            // This defines the default behavior if nothing further is required when all chunks have been uploaded.
            finalizeChunks: function (id, responseParser) {
                var lastChunkIdx = handler._getTotalChunks(id) - 1,
                    xhr = handler._getXhr(id, lastChunkIdx);

                if (responseParser) {
                    return new qq.Promise().success(responseParser(xhr), xhr);
                }

                return new qq.Promise().success({}, xhr);
            },

            getFile: function (id) {
                return handler.isValid(id) && handler._getFileState(id).file;
            },

            getProxy: function (id) {
                return handler.isValid(id) && handler._getFileState(id).proxy;
            },

            /**
             * @returns {Array} Array of objects containing properties useful to integrators
             * when it is important to determine which files are potentially resumable.
             */
            getResumableFilesData: function () {
                var resumableFilesData = [];

                handler._iterateResumeRecords(function (key, uploadData) {
                    handler.moveInProgressToRemaining(null, uploadData.chunking.inProgress, uploadData.chunking.remaining);

                    var data = {
                        name: uploadData.name,
                        remaining: uploadData.chunking.remaining,
                        size: uploadData.size,
                        uuid: uploadData.uuid
                    };

                    if (uploadData.key) {
                        data.key = uploadData.key;
                    }

                    resumableFilesData.push(data);
                });

                return resumableFilesData;
            },

            isResumable: function (id) {
                return !!chunking && handler.isValid(id) && !handler._getFileState(id).notResumable;
            },

            moveInProgressToRemaining: function (id, optInProgress, optRemaining) {
                var inProgress = optInProgress || handler._getFileState(id).chunking.inProgress,
                    remaining = optRemaining || handler._getFileState(id).chunking.remaining;

                if (inProgress) {
                    log(qq.format("Moving these chunks from in-progress {}, to remaining.", JSON.stringify(inProgress)));
                    inProgress.reverse();
                    qq.each(inProgress, function (idx, chunkIdx) {
                        remaining.unshift(chunkIdx);
                    });
                    inProgress.length = 0;
                }
            },

            pause: function (id) {
                if (handler.isValid(id)) {
                    log(qq.format("Aborting XHR upload for {} '{}' due to pause instruction.", id, getName(id)));
                    handler._getFileState(id).paused = true;
                    abort(id);
                    return true;
                }
            },

            reevaluateChunking: function (id) {
                if (chunking && handler.isValid(id)) {
                    var state = handler._getFileState(id),
                        totalChunks,
                        i;

                    delete state.chunking;

                    state.chunking = {};
                    totalChunks = handler._getTotalChunks(id);
                    if (totalChunks > 1 || chunking.mandatory) {
                        state.chunking.enabled = true;
                        state.chunking.parts = totalChunks;
                        state.chunking.remaining = [];

                        for (i = 0; i < totalChunks; i++) {
                            state.chunking.remaining.push(i);
                        }

                        handler._initTempState(id);
                    }
                    else {
                        state.chunking.enabled = false;
                    }
                }
            },

            updateBlob: function (id, newBlob) {
                if (handler.isValid(id)) {
                    handler._getFileState(id).file = newBlob;
                }
            },

            _clearXhrs: function (id) {
                var tempState = handler._getFileState(id).temp;

                qq.each(tempState.ajaxRequesters, function (chunkId) {
                    delete tempState.ajaxRequesters[chunkId];
                });

                qq.each(tempState.xhrs, function (chunkId) {
                    delete tempState.xhrs[chunkId];
                });
            },

            /**
             * Creates an XHR instance for this file and stores it in the fileState.
             *
             * @param id File ID
             * @param optChunkIdx The chunk index associated with this XHR, if applicable
             * @returns {XMLHttpRequest}
             */
            _createXhr: function (id, optChunkIdx) {
                return handler._registerXhr(id, optChunkIdx, qq.createXhrInstance());
            },

            _getAjaxRequester: function (id, optChunkIdx) {
                var chunkIdx = optChunkIdx == null ? -1 : optChunkIdx;
                return handler._getFileState(id).temp.ajaxRequesters[chunkIdx];
            },

            _getChunkData: function (id, chunkIndex) {
                var chunkSize = chunking.partSize,
                    fileSize = getSize(id),
                    fileOrBlob = handler.getFile(id),
                    startBytes = chunkSize * chunkIndex,
                    endBytes = startBytes + chunkSize >= fileSize ? fileSize : startBytes + chunkSize,
                    totalChunks = handler._getTotalChunks(id),
                    cachedChunks = this._getFileState(id).temp.cachedChunks,

                // To work around a Webkit GC bug, we must keep each chunk `Blob` in scope until we are done with it.
                // See https://github.com/Widen/fine-uploader/issues/937#issuecomment-41418760
                    blob = cachedChunks[chunkIndex] || qq.sliceBlob(fileOrBlob, startBytes, endBytes);

                cachedChunks[chunkIndex] = blob;

                return {
                    part: chunkIndex,
                    start: startBytes,
                    end: endBytes,
                    count: totalChunks,
                    blob: blob,
                    size: endBytes - startBytes
                };
            },

            _getChunkDataForCallback: function (chunkData) {
                return {
                    partIndex: chunkData.part,
                    startByte: chunkData.start + 1,
                    endByte: chunkData.end,
                    totalParts: chunkData.count
                };
            },

            /**
             * @param id File ID
             * @returns {string} Identifier for this item that may appear in the browser's local storage
             */
            _getLocalStorageId: function (id) {
                var formatVersion = "5.0",
                    name = getName(id),
                    size = getSize(id),
                    chunkSize = chunking.partSize,
                    endpoint = getEndpoint(id);

                return qq.format("qq{}resume{}-{}-{}-{}-{}", namespace, formatVersion, name, size, chunkSize, endpoint);
            },

            _getMimeType: function (id) {
                return handler.getFile(id).type;
            },

            _getPersistableData: function (id) {
                return handler._getFileState(id).chunking;
            },

            /**
             * @param id ID of the associated file
             * @returns {number} Number of parts this file can be divided into, or undefined if chunking is not supported in this UA
             */
            _getTotalChunks: function (id) {
                if (chunking) {
                    var fileSize = getSize(id),
                        chunkSize = chunking.partSize;

                    return Math.ceil(fileSize / chunkSize);
                }
            },

            _getXhr: function (id, optChunkIdx) {
                var chunkIdx = optChunkIdx == null ? -1 : optChunkIdx;
                return handler._getFileState(id).temp.xhrs[chunkIdx];
            },

            _getXhrs: function (id) {
                return handler._getFileState(id).temp.xhrs;
            },

            // Iterates through all XHR handler-created resume records (in local storage),
            // invoking the passed callback and passing in the key and value of each local storage record.
            _iterateResumeRecords: function (callback) {
                if (resumeEnabled) {
                    qq.each(localStorage, function (key, item) {
                        if (key.indexOf(qq.format("qq{}resume", namespace)) === 0) {
                            var uploadData = JSON.parse(item);
                            callback(key, uploadData);
                        }
                    });
                }
            },

            _initTempState: function (id) {
                handler._getFileState(id).temp = {
                    ajaxRequesters: {},
                    chunkProgress: {},
                    xhrs: {},
                    cachedChunks: {}
                };
            },

            _markNotResumable: function (id) {
                handler._getFileState(id).notResumable = true;
            },

            // Removes a chunked upload record from local storage, if possible.
            // Returns true if the item was removed, false otherwise.
            _maybeDeletePersistedChunkData: function (id) {
                var localStorageId;

                if (resumeEnabled && handler.isResumable(id)) {
                    localStorageId = handler._getLocalStorageId(id);

                    if (localStorageId && localStorage.getItem(localStorageId)) {
                        localStorage.removeItem(localStorageId);
                        return true;
                    }
                }

                return false;
            },

            // If this is a resumable upload, grab the relevant data from storage and items in memory that track this upload
            // so we can pick up from where we left off.
            _maybePrepareForResume: function (id) {
                var state = handler._getFileState(id),
                    localStorageId, persistedData;

                // Resume is enabled and possible and this is the first time we've tried to upload this file in this session,
                // so prepare for a resume attempt.
                if (resumeEnabled && state.key === undefined) {
                    localStorageId = handler._getLocalStorageId(id);
                    persistedData = localStorage.getItem(localStorageId);

                    // If we found this item in local storage, maybe we should resume it.
                    if (persistedData) {
                        persistedData = JSON.parse(persistedData);

                        // If we found a resume record but we have already handled this file in this session,
                        // don't try to resume it & ensure we don't persist future check data
                        if (getDataByUuid(persistedData.uuid)) {
                            handler._markNotResumable(id);
                        }
                        else {
                            log(qq.format("Identified file with ID {} and name of {} as resumable.", id, getName(id)));

                            onUuidChanged(id, persistedData.uuid);

                            state.key = persistedData.key;
                            state.chunking = persistedData.chunking;
                            state.loaded = persistedData.loaded;
                            state.attemptingResume = true;

                            handler.moveInProgressToRemaining(id);
                        }
                    }
                }
            },

            // Persist any data needed to resume this upload in a new session.
            _maybePersistChunkedState: function (id) {
                var state = handler._getFileState(id),
                    localStorageId, persistedData;

                // If local storage isn't supported by the browser, or if resume isn't enabled or possible, give up
                if (resumeEnabled && handler.isResumable(id)) {
                    localStorageId = handler._getLocalStorageId(id);

                    persistedData = {
                        name: getName(id),
                        size: getSize(id),
                        uuid: getUuid(id),
                        key: state.key,
                        chunking: state.chunking,
                        loaded: state.loaded,
                        lastUpdated: Date.now()
                    };

                    try {
                        localStorage.setItem(localStorageId, JSON.stringify(persistedData));
                    }
                    catch (error) {
                        log(qq.format("Unable to save resume data for '{}' due to error: '{}'.", id, error.toString()), "warn");
                    }
                }
            },

            _registerProgressHandler: function (id, chunkIdx, chunkSize) {
                var xhr = handler._getXhr(id, chunkIdx),
                    name = getName(id),
                    progressCalculator = {
                        simple: function (loaded, total) {
                            var fileSize = getSize(id);

                            if (loaded === total) {
                                onProgress(id, name, fileSize, fileSize);
                            }
                            else {
                                onProgress(id, name, (loaded >= fileSize ? fileSize - 1 : loaded), fileSize);
                            }
                        },

                        chunked: function (loaded, total) {
                            var chunkProgress = handler._getFileState(id).temp.chunkProgress,
                                totalSuccessfullyLoadedForFile = handler._getFileState(id).loaded,
                                loadedForRequest = loaded,
                                totalForRequest = total,
                                totalFileSize = getSize(id),
                                estActualChunkLoaded = loadedForRequest - (totalForRequest - chunkSize),
                                totalLoadedForFile = totalSuccessfullyLoadedForFile;

                            chunkProgress[chunkIdx] = estActualChunkLoaded;

                            qq.each(chunkProgress, function (chunkIdx, chunkLoaded) {
                                totalLoadedForFile += chunkLoaded;
                            });

                            onProgress(id, name, totalLoadedForFile, totalFileSize);
                        }
                    };

                xhr.upload.onprogress = function (e) {
                    if (e.lengthComputable) {
                        /* jshint eqnull: true */
                        var type = chunkSize == null ? "simple" : "chunked";
                        progressCalculator[type](e.loaded, e.total);
                    }
                };
            },

            /**
             * Registers an XHR transport instance created elsewhere.
             *
             * @param id ID of the associated file
             * @param optChunkIdx The chunk index associated with this XHR, if applicable
             * @param xhr XMLHttpRequest object instance
             * @param optAjaxRequester `qq.AjaxRequester` associated with this request, if applicable.
             * @returns {XMLHttpRequest}
             */
            _registerXhr: function (id, optChunkIdx, xhr, optAjaxRequester) {
                var xhrsId = optChunkIdx == null ? -1 : optChunkIdx,
                    tempState = handler._getFileState(id).temp;

                tempState.xhrs = tempState.xhrs || {};
                tempState.ajaxRequesters = tempState.ajaxRequesters || {};

                tempState.xhrs[xhrsId] = xhr;

                if (optAjaxRequester) {
                    tempState.ajaxRequesters[xhrsId] = optAjaxRequester;
                }

                return xhr;
            },

            // Deletes any local storage records that are "expired".
            _removeExpiredChunkingRecords: function () {
                var expirationDays = resume.recordsExpireIn;

                handler._iterateResumeRecords(function (key, uploadData) {
                    var expirationDate = new Date(uploadData.lastUpdated);

                    // transform updated date into expiration date
                    expirationDate.setDate(expirationDate.getDate() + expirationDays);

                    if (expirationDate.getTime() <= Date.now()) {
                        log("Removing expired resume record with key " + key);
                        localStorage.removeItem(key);
                    }
                });
            },

            /**
             * Determine if the associated file should be chunked.
             *
             * @param id ID of the associated file
             * @returns {*} true if chunking is enabled, possible, and the file can be split into more than 1 part
             */
            _shouldChunkThisFile: function (id) {
                var state = handler._getFileState(id);

                if (!state.chunking) {
                    handler.reevaluateChunking(id);
                }

                return state.chunking.enabled;
            }
        });
    };

    /*globals qq */
    /*jshint -W117 */
    qq.WindowReceiveMessage = function (o) {
        "use strict";

        var options = {
                log: function (message, level) {
                }
            },
            callbackWrapperDetachers = {};

        qq.extend(options, o);

        qq.extend(this, {
            receiveMessage: function (id, callback) {
                var onMessageCallbackWrapper = function (event) {
                    callback(event.data);
                };

                if (window.postMessage) {
                    callbackWrapperDetachers[id] = qq(window).attach("message", onMessageCallbackWrapper);
                }
                else {
                    log("iframe message passing not supported in this browser!", "error");
                }
            },

            stopReceivingMessages: function (id) {
                if (window.postMessage) {
                    var detacher = callbackWrapperDetachers[id];
                    if (detacher) {
                        detacher();
                    }
                }
            }
        });
    };

    /*globals qq*/
    /**
     * Upload handler used that assumes the current user agent does not have any support for the
     * File API, and, therefore, makes use of iframes and forms to submit the files directly to
     * a generic server.
     *
     * @param options Options passed from the base handler
     * @param proxy Callbacks & methods used to query for or push out data/changes
     */
    qq.traditional = qq.traditional || {};
    qq.traditional.FormUploadHandler = function (options, proxy) {
        "use strict";

        var handler = this,
            getName = proxy.getName,
            getUuid = proxy.getUuid,
            log = proxy.log;

        /**
         * Returns json object received by iframe from server.
         */
        function getIframeContentJson(id, iframe) {
            /*jshint evil: true*/

            var response, doc, innerHtml;

            //IE may throw an "access is denied" error when attempting to access contentDocument on the iframe in some cases
            try {
                // iframe.contentWindow.document - for IE<7
                doc = iframe.contentDocument || iframe.contentWindow.document;
                innerHtml = doc.body.innerHTML;

                log("converting iframe's innerHTML to JSON");
                log("innerHTML = " + innerHtml);
                //plain text response may be wrapped in <pre> tag
                if (innerHtml && innerHtml.match(/^<pre/i)) {
                    innerHtml = doc.body.firstChild.firstChild.nodeValue;
                }

                response = handler._parseJsonResponse(innerHtml);
            }
            catch (error) {
                log("Error when attempting to parse form upload response (" + error.message + ")", "error");
                response = {success: false};
            }

            return response;
        }

        /**
         * Creates form, that will be submitted to iframe
         */
        function createForm(id, iframe) {
            var params = options.paramsStore.get(id),
                method = options.method.toLowerCase() === "get" ? "GET" : "POST",
                endpoint = options.endpointStore.get(id),
                name = getName(id);

            params[options.uuidName] = getUuid(id);
            params[options.filenameParam] = name;

            return handler._initFormForUpload({
                method: method,
                endpoint: endpoint,
                params: params,
                paramsInBody: options.paramsInBody,
                targetName: iframe.name
            });
        }

        this.uploadFile = function (id) {
            var input = handler.getInput(id),
                iframe = handler._createIframe(id),
                promise = new qq.Promise(),
                form;

            form = createForm(id, iframe);
            form.appendChild(input);

            handler._attachLoadEvent(iframe, function (responseFromMessage) {
                log("iframe loaded");

                var response = responseFromMessage ? responseFromMessage : getIframeContentJson(id, iframe);

                handler._detachLoadEvent(id);

                //we can't remove an iframe if the iframe doesn't belong to the same domain
                if (!options.cors.expected) {
                    qq(iframe).remove();
                }

                if (response.success) {
                    promise.success(response);
                }
                else {
                    promise.failure(response);
                }
            });

            log("Sending upload request for " + id);
            form.submit();
            qq(form).remove();

            return promise;
        };

        qq.extend(this, new qq.FormUploadHandler({
            options: {
                isCors: options.cors.expected,
                inputName: options.inputName
            },

            proxy: {
                onCancel: options.onCancel,
                getName: getName,
                getUuid: getUuid,
                log: log
            }
        }));
    };

    /*globals qq*/
    /**
     * Upload handler used to upload to traditional endpoints.  It depends on File API support, and, therefore,
     * makes use of `XMLHttpRequest` level 2 to upload `File`s and `Blob`s to a generic server.
     *
     * @param spec Options passed from the base handler
     * @param proxy Callbacks & methods used to query for or push out data/changes
     */
    qq.traditional = qq.traditional || {};
    qq.traditional.XhrUploadHandler = function (spec, proxy) {
        "use strict";

        var handler = this,
            getName = proxy.getName,
            getSize = proxy.getSize,
            getUuid = proxy.getUuid,
            log = proxy.log,
            multipart = spec.forceMultipart || spec.paramsInBody,

            addChunkingSpecificParams = function (id, params, chunkData) {
                var size = getSize(id),
                    name = getName(id);

                params[spec.chunking.paramNames.partIndex] = chunkData.part;
                params[spec.chunking.paramNames.partByteOffset] = chunkData.start;
                params[spec.chunking.paramNames.chunkSize] = chunkData.size;
                params[spec.chunking.paramNames.totalParts] = chunkData.count;
                params[spec.totalFileSizeName] = size;

                /**
                 * When a Blob is sent in a multipart request, the filename value in the content-disposition header is either "blob"
                 * or an empty string.  So, we will need to include the actual file name as a param in this case.
                 */
                if (multipart) {
                    params[spec.filenameParam] = name;
                }
            },

            allChunksDoneRequester = new qq.traditional.AllChunksDoneAjaxRequester({
                cors: spec.cors,
                endpoint: spec.chunking.success.endpoint,
                log: log
            }),

            createReadyStateChangedHandler = function (id, xhr) {
                var promise = new qq.Promise();

                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4) {
                        var result = onUploadOrChunkComplete(id, xhr);

                        if (result.success) {
                            promise.success(result.response, xhr);
                        }
                        else {
                            promise.failure(result.response, xhr);
                        }
                    }
                };

                return promise;
            },

            getChunksCompleteParams = function (id) {
                var params = spec.paramsStore.get(id),
                    name = getName(id),
                    size = getSize(id);

                params[spec.uuidName] = getUuid(id);
                params[spec.filenameParam] = name;
                params[spec.totalFileSizeName] = size;
                params[spec.chunking.paramNames.totalParts] = handler._getTotalChunks(id);

                return params;
            },

            isErrorUploadResponse = function (xhr, response) {
                return qq.indexOf([200, 201, 202, 203, 204], xhr.status) < 0 ||
                       // !response.success ||
                       response.reset;
            },

            onUploadOrChunkComplete = function (id, xhr) {
                var response;

                log("xhr - server response received for " + id);
                log("responseText = " + xhr.responseText);

                response = parseResponse(true, xhr);

                return {
                    success: !isErrorUploadResponse(xhr, response),
                    response: response
                };
            },

        // If this is an upload response, we require a JSON payload, otherwise, it is optional.
            parseResponse = function (upload, xhr) {
                var response = {};

                try {
                    log(qq.format("Received response status {} with body: {}", xhr.status, xhr.responseText));
                    response = qq.parseJson(xhr.responseText);
                }
                catch (error) {
                    upload && log("Error when attempting to parse xhr response text (" + error.message + ")", "error");
                }

                return response;
            },

            sendChunksCompleteRequest = function (id) {
                var promise = new qq.Promise();

                allChunksDoneRequester.complete(
                    id,
                    handler._createXhr(id),
                    getChunksCompleteParams(id),
                    spec.customHeaders.get(id)
                )
                    .then(function (xhr) {
                        promise.success(parseResponse(false, xhr), xhr);
                    }, function (xhr) {
                        promise.failure(parseResponse(false, xhr), xhr);
                    });

                return promise;
            },

            setParamsAndGetEntityToSend = function (params, xhr, fileOrBlob, id) {
                var formData = new FormData(),
                    method = spec.method,
                    endpoint = spec.endpointStore.get(id),
                    name = getName(id),
                    size = getSize(id);

                params[spec.uuidName] = getUuid(id);
                params[spec.filenameParam] = name;

                if (multipart) {
                    params[spec.totalFileSizeName] = size;
                }

                //build query string
                if (!spec.paramsInBody) {
                    if (!multipart) {
                        params[spec.inputName] = name;
                    }
                    endpoint = qq.obj2url(params, endpoint);
                }

                xhr.open(method, endpoint, true);

                if (spec.cors.expected && spec.cors.sendCredentials) {
                    xhr.withCredentials = true;
                }

                if (multipart) {
                    if (spec.paramsInBody) {
                        qq.obj2FormData(params, formData);
                    }

                    formData.append(spec.inputName, fileOrBlob);
                    return formData;
                }

                return fileOrBlob;
            },

            setUploadHeaders = function (id, xhr) {
                var extraHeaders = spec.customHeaders.get(id),
                    fileOrBlob = handler.getFile(id);

                xhr.setRequestHeader("Accept", "application/json");
                xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                xhr.setRequestHeader("Cache-Control", "no-cache");

                if (!multipart) {
                    xhr.setRequestHeader("Content-Type", "application/octet-stream");
                    //NOTE: return mime type in xhr works on chrome 16.0.9 firefox 11.0a2
                    xhr.setRequestHeader("X-Mime-Type", fileOrBlob.type);
                }

                qq.each(extraHeaders, function (name, val) {
                    xhr.setRequestHeader(name, val);
                });
            };

        qq.extend(this, {
            uploadChunk: function (id, chunkIdx, resuming) {
                var chunkData = handler._getChunkData(id, chunkIdx),
                    xhr = handler._createXhr(id, chunkIdx),
                    size = getSize(id),
                    promise, toSend, params;

                promise = createReadyStateChangedHandler(id, xhr);
                handler._registerProgressHandler(id, chunkIdx, chunkData.size);
                params = spec.paramsStore.get(id);
                addChunkingSpecificParams(id, params, chunkData);

                if (resuming) {
                    params[spec.resume.paramNames.resuming] = true;
                }

                toSend = setParamsAndGetEntityToSend(params, xhr, chunkData.blob, id);
                setUploadHeaders(id, xhr);
                xhr.send(toSend);

                return promise;
            },

            uploadFile: function (id) {
                var fileOrBlob = handler.getFile(id),
                    promise, xhr, params, toSend;

                xhr = handler._createXhr(id);
                handler._registerProgressHandler(id);
                promise = createReadyStateChangedHandler(id, xhr);
                params = spec.paramsStore.get(id);
                toSend = setParamsAndGetEntityToSend(params, xhr, fileOrBlob, id);
                setUploadHeaders(id, xhr);
                xhr.send(toSend);

                return promise;
            }
        });

        qq.extend(this, new qq.XhrUploadHandler({
            options: qq.extend({namespace: "traditional"}, spec),
            proxy: qq.extend({getEndpoint: spec.endpointStore.get}, proxy)
        }));

        qq.override(this, function (super_) {
            return {
                finalizeChunks: function (id) {
                    if (spec.chunking.success.endpoint) {
                        return sendChunksCompleteRequest(id);
                    }
                    else {
                        return super_.finalizeChunks(id, qq.bind(parseResponse, this, true));
                    }
                }
            };
        });
    };

    /*globals qq*/
    /**
     * Ajax requester used to send a POST to a traditional endpoint once all chunks for a specific file have uploaded
     * successfully.
     *
     * @param o Options from the caller - will override the defaults.
     * @constructor
     */
    qq.traditional.AllChunksDoneAjaxRequester = function (o) {
        "use strict";

        var requester,
            method = "POST",
            options = {
                cors: {
                    allowXdr: false,
                    expected: false,
                    sendCredentials: false
                },
                endpoint: null,
                log: function (str, level) {
                }
            },
            promises = {},
            endpointHandler = {
                get: function (id) {
                    return options.endpoint;
                }
            };

        qq.extend(options, o);

        requester = qq.extend(this, new qq.AjaxRequester({
            acceptHeader: "application/json",
            validMethods: [method],
            method: method,
            endpointStore: endpointHandler,
            allowXRequestedWithAndCacheControl: false,
            cors: options.cors,
            log: options.log,
            onComplete: function (id, xhr, isError) {
                var promise = promises[id];

                delete promises[id];

                if (isError) {
                    promise.failure(xhr);
                }
                else {
                    promise.success(xhr);
                }
            }
        }));

        qq.extend(this, {
            complete: function (id, xhr, params, headers) {
                var promise = new qq.Promise();

                options.log("Submitting All Chunks Done request for " + id);

                promises[id] = promise;

                requester.initTransport(id)
                    .withParams(params)
                    .withHeaders(headers)
                    .send(xhr);

                return promise;
            }
        });
    };

    /*globals qq*/
    qq.PasteSupport = function (o) {
        "use strict";

        var options, detachPasteHandler;

        options = {
            targetElement: null,
            callbacks: {
                log: function (message, level) {
                },
                pasteReceived: function (blob) {
                }
            }
        };

        function isImage(item) {
            return item.type &&
                   item.type.indexOf("image/") === 0;
        }

        function registerPasteHandler() {
            detachPasteHandler = qq(options.targetElement).attach("paste", function (event) {
                var clipboardData = event.clipboardData;

                if (clipboardData) {
                    qq.each(clipboardData.items, function (idx, item) {
                        if (isImage(item)) {
                            var blob = item.getAsFile();
                            options.callbacks.pasteReceived(blob);
                        }
                    });
                }
            });
        }

        function unregisterPasteHandler() {
            if (detachPasteHandler) {
                detachPasteHandler();
            }
        }

        qq.extend(options, o);
        registerPasteHandler();

        qq.extend(this, {
            reset: function () {
                unregisterPasteHandler();
            }
        });
    };

    /*globals qq, document, CustomEvent*/
    qq.DragAndDrop = function (o) {
        "use strict";

        var options,
            HIDE_ZONES_EVENT_NAME = "qq-hidezones",
            HIDE_BEFORE_ENTER_ATTR = "qq-hide-dropzone",
            uploadDropZones = [],
            droppedFiles = [],
            disposeSupport = new qq.DisposeSupport();

        options = {
            dropZoneElements: [],
            allowMultipleItems: true,
            classes: {
                dropActive: null
            },
            callbacks: new qq.DragAndDrop.callbacks(),
            debug: false
        };

        qq.extend(options, o, true);

        function uploadDroppedFiles(files, uploadDropZone) {
            // We need to convert the `FileList` to an actual `Array` to avoid iteration issues
            var filesAsArray = Array.prototype.slice.call(files);

            if (options.debug) {
                options.callbacks.dropLog("Grabbed " + files.length + " dropped files.");
            }
            uploadDropZone.dropDisabled(false);
            options.callbacks.processingDroppedFilesComplete(filesAsArray, uploadDropZone.getElement());
        }

        function traverseFileTree(entry) {
            var parseEntryPromise = new qq.Promise();

            if (entry.isFile) {
                entry.file(function (file) {
                        var name = entry.name,
                            fullPath = entry.fullPath,
                            indexOfNameInFullPath = fullPath.indexOf(name);

                        // remove file name from full path string
                        fullPath = fullPath.substr(0, indexOfNameInFullPath);

                        // remove leading slash in full path string
                        if (fullPath.charAt(0) === "/") {
                            fullPath = fullPath.substr(1);
                        }

                        file.qqPath = fullPath;
                        droppedFiles.push(file);
                        parseEntryPromise.success();
                    },
                    function (fileError) {
                        options.callbacks.dropLog("Problem parsing '" + entry.fullPath + "'.  FileError code " + fileError.code + ".",
                            "error");
                        parseEntryPromise.failure();
                    });
            }
            else if (entry.isDirectory) {
                getFilesInDirectory(entry).then(
                    function allEntriesRead(entries) {
                        var entriesLeft = entries.length;

                        qq.each(entries, function (idx, entry) {
                            traverseFileTree(entry).done(function () {
                                entriesLeft -= 1;

                                if (entriesLeft === 0) {
                                    parseEntryPromise.success();
                                }
                            });
                        });

                        if (!entries.length) {
                            parseEntryPromise.success();
                        }
                    },

                    function readFailure(fileError) {
                        options.callbacks.dropLog("Problem parsing '" + entry.fullPath + "'.  FileError code " + fileError.code + ".",
                            "error");
                        parseEntryPromise.failure();
                    }
                );
            }

            return parseEntryPromise;
        }

        // Promissory.  Guaranteed to read all files in the root of the passed directory.
        function getFilesInDirectory(entry, reader, accumEntries, existingPromise) {
            var promise = existingPromise || new qq.Promise(),
                dirReader = reader || entry.createReader();

            dirReader.readEntries(
                function readSuccess(entries) {
                    var newEntries = accumEntries ? accumEntries.concat(entries) : entries;

                    if (entries.length) {
                        setTimeout(function () { // prevent stack overflow, however unlikely
                            getFilesInDirectory(entry, dirReader, newEntries, promise);
                        }, 0);
                    }
                    else {
                        promise.success(newEntries);
                    }
                },

                promise.failure
            );

            return promise;
        }

        function handleDataTransfer(dataTransfer, uploadDropZone) {
            var pendingFolderPromises = [],
                handleDataTransferPromise = new qq.Promise();

            options.callbacks.processingDroppedFiles();
            uploadDropZone.dropDisabled(true);

            if (dataTransfer.files.length > 1 && !options.allowMultipleItems) {
                options.callbacks.processingDroppedFilesComplete([]);
                options.callbacks.dropError("tooManyFilesError", "");
                uploadDropZone.dropDisabled(false);
                handleDataTransferPromise.failure();
            }
            else {
                droppedFiles = [];

                if (qq.isFolderDropSupported(dataTransfer)) {
                    qq.each(dataTransfer.items, function (idx, item) {
                        var entry = item.webkitGetAsEntry();

                        if (entry) {
                            //due to a bug in Chrome's File System API impl - #149735
                            if (entry.isFile) {
                                droppedFiles.push(item.getAsFile());
                            }

                            else {
                                pendingFolderPromises.push(traverseFileTree(entry).done(function () {
                                    pendingFolderPromises.pop();
                                    if (pendingFolderPromises.length === 0) {
                                        handleDataTransferPromise.success();
                                    }
                                }));
                            }
                        }
                    });
                }
                else {
                    droppedFiles = dataTransfer.files;
                }

                if (pendingFolderPromises.length === 0) {
                    handleDataTransferPromise.success();
                }
            }

            return handleDataTransferPromise;
        }

        function setupDropzone(dropArea) {
            var dropZone = new qq.UploadDropZone({
                HIDE_ZONES_EVENT_NAME: HIDE_ZONES_EVENT_NAME,
                element: dropArea,
                onEnter: function (e) {
                    qq(dropArea).addClass(options.classes.dropActive);
                    e.stopPropagation();
                    options.callbacks.onDragEnter(e);
                },
                onLeaveNotDescendants: function (e) {
                    qq(dropArea).removeClass(options.classes.dropActive);
                    options.callbacks.onDragLeave(e);
                },
                onDrop: function (e) {
                    handleDataTransfer(e.dataTransfer, dropZone).then(
                        function () {
                            uploadDroppedFiles(droppedFiles, dropZone);
                        },
                        function () {
                            options.callbacks.dropLog("Drop event DataTransfer parsing failed.  No files will be uploaded.", "error");
                        }
                    );
                    options.callbacks.onDrop(e);
                }
            });

            disposeSupport.addDisposer(function () {
                dropZone.dispose();
            });

            qq(dropArea).hasAttribute(HIDE_BEFORE_ENTER_ATTR) && qq(dropArea).hide();

            uploadDropZones.push(dropZone);

            return dropZone;
        }

        function isFileDrag(dragEvent) {
            var fileDrag;

            qq.each(dragEvent.dataTransfer.types, function (key, val) {
                if (val === "Files") {
                    fileDrag = true;
                    return false;
                }
            });

            return fileDrag;
        }

        // Attempt to determine when the file has left the document.  It is not always possible to detect this
        // in all cases, but it is generally possible in all browsers, with a few exceptions.
        //
        // Exceptions:
        // * IE10+ & Safari: We can't detect a file leaving the document if the Explorer window housing the file
        //                   overlays the browser window.
        // * IE10+: If the file is dragged out of the window too quickly, IE does not set the expected values of the
        //          event's X & Y properties.
        function leavingDocumentOut(e) {
            if (qq.firefox()) {
                return !e.relatedTarget;
            }

            if (qq.safari()) {
                return e.x < 0 || e.y < 0;
            }

            return e.x === 0 && e.y === 0;
        }

        function setupDragDrop() {
            var dropZones = options.dropZoneElements,

                maybeHideDropZones = function () {
                    setTimeout(function () {
                        qq.each(dropZones, function (idx, dropZone) {
                            qq(dropZone).hasAttribute(HIDE_BEFORE_ENTER_ATTR) && qq(dropZone).hide();
                            qq(dropZone).removeClass(options.classes.dropActive);
                        });
                    }, 10);
                };

            qq.each(dropZones, function (idx, dropZone) {
                var uploadDropZone = setupDropzone(dropZone);

                // IE <= 9 does not support the File API used for drag+drop uploads
                if (dropZones.length && qq.supportedFeatures.fileDrop) {
                    disposeSupport.attach(document, "dragenter", function (e) {
                        if (!uploadDropZone.dropDisabled() && isFileDrag(e)) {
                            qq.each(dropZones, function (idx, dropZone) {
                                // We can't apply styles to non-HTMLElements, since they lack the `style` property.
                                // Also, if the drop zone isn't initially hidden, let's not mess with `style.display`.
                                if (dropZone instanceof HTMLElement &&
                                    qq(dropZone).hasAttribute(HIDE_BEFORE_ENTER_ATTR)) {

                                    qq(dropZone).css({display: "block"});
                                }
                            });
                        }
                    });
                }
            });

            disposeSupport.attach(document, "dragleave", function (e) {
                if (leavingDocumentOut(e)) {
                    maybeHideDropZones();
                }
            });

            // Just in case we were not able to detect when a dragged file has left the document,
            // hide all relevant drop zones the next time the mouse enters the document.
            // Note that mouse events such as this one are not fired during drag operations.
            disposeSupport.attach(qq(document).children()[0], "mouseenter", function (e) {
                maybeHideDropZones();
            });

            disposeSupport.attach(document, "drop", function (e) {
                e.preventDefault();
                maybeHideDropZones();
            });

            disposeSupport.attach(document, HIDE_ZONES_EVENT_NAME, maybeHideDropZones);
        }

        setupDragDrop();

        qq.extend(this, {
            setupExtraDropzone: function (element) {
                options.dropZoneElements.push(element);
                setupDropzone(element);
            },

            removeDropzone: function (element) {
                var i,
                    dzs = options.dropZoneElements;

                for (i in dzs) {
                    if (dzs[i] === element) {
                        return dzs.splice(i, 1);
                    }
                }
            },

            dispose: function () {
                disposeSupport.dispose();
                qq.each(uploadDropZones, function (idx, dropZone) {
                    dropZone.dispose();
                });
            }
        });
    };

    qq.DragAndDrop.callbacks = function () {
        "use strict";

        return {
            processingDroppedFiles: function () {
            },
            processingDroppedFilesComplete: function (files, targetEl) {
            },
            dropError: function (code, errorSpecifics) {
                qq.log("Drag & drop error code '" + code + " with these specifics: '" + errorSpecifics + "'", "error");
            },
            dropLog: function (message, level) {
                qq.log(message, level);
            },
            onDragEnter: function (event) {
            },
            onDragLeave: function (event) {
            },
            onDrop: function (event) {
            },
        };
    };

    qq.UploadDropZone = function (o) {
        "use strict";

        var disposeSupport = new qq.DisposeSupport(),
            options, element, preventDrop, dropOutsideDisabled;

        options = {
            element: null,
            onEnter: function (e) {
            },
            onLeave: function (e) {
            },
            // is not fired when leaving element by hovering descendants
            onLeaveNotDescendants: function (e) {
            },
            onDrop: function (e) {
            }
        };

        qq.extend(options, o);
        element = options.element;

        function dragoverShouldBeCanceled() {
            return qq.safari() || (qq.firefox() && qq.windows());
        }

        function disableDropOutside(e) {
            // run only once for all instances
            if (!dropOutsideDisabled) {

                // for these cases we need to catch onDrop to reset dropArea
                if (dragoverShouldBeCanceled) {
                    disposeSupport.attach(document, "dragover", function (e) {
                        e.preventDefault();
                    });
                } else {
                    disposeSupport.attach(document, "dragover", function (e) {
                        if (e.dataTransfer) {
                            e.dataTransfer.dropEffect = "none";
                            e.preventDefault();
                        }
                    });
                }

                dropOutsideDisabled = true;
            }
        }

        function isValidFileDrag(e) {
            // e.dataTransfer currently causing IE errors
            // IE9 does NOT support file API, so drag-and-drop is not possible
            if (!qq.supportedFeatures.fileDrop) {
                return false;
            }

            var effectTest, dt = e.dataTransfer,
            // do not check dt.types.contains in webkit, because it crashes safari 4
                isSafari = qq.safari();

            // dt.effectAllowed is none in Safari 5
            // dt.types.contains check is for firefox

            // dt.effectAllowed crashes IE 11 & 10 when files have been dragged from
            // the filesystem
            effectTest = qq.ie() && qq.supportedFeatures.fileDrop ? true : dt.effectAllowed !== "none";
            return dt && effectTest && (dt.files || (!isSafari && dt.types.contains && dt.types.contains("Files")));
        }

        function isOrSetDropDisabled(isDisabled) {
            if (isDisabled !== undefined) {
                preventDrop = isDisabled;
            }
            return preventDrop;
        }

        function triggerHidezonesEvent() {
            var hideZonesEvent;

            function triggerUsingOldApi() {
                hideZonesEvent = document.createEvent("Event");
                hideZonesEvent.initEvent(options.HIDE_ZONES_EVENT_NAME, true, true);
            }

            if (window.CustomEvent) {
                try {
                    hideZonesEvent = new CustomEvent(options.HIDE_ZONES_EVENT_NAME);
                }
                catch (err) {
                    triggerUsingOldApi();
                }
            }
            else {
                triggerUsingOldApi();
            }

            document.dispatchEvent(hideZonesEvent);
        }

        function attachEvents() {
            disposeSupport.attach(element, "dragover", function (e) {
                if (!isValidFileDrag(e)) {
                    return;
                }

                // dt.effectAllowed crashes IE 11 & 10 when files have been dragged from
                // the filesystem
                var effect = qq.ie() && qq.supportedFeatures.fileDrop ? null : e.dataTransfer.effectAllowed;
                if (effect === "move" || effect === "linkMove") {
                    e.dataTransfer.dropEffect = "move"; // for FF (only move allowed)
                } else {
                    e.dataTransfer.dropEffect = "copy"; // for Chrome
                }

                e.stopPropagation();
                e.preventDefault();
            });

            disposeSupport.attach(element, "dragenter", function (e) {
                if (!isOrSetDropDisabled()) {
                    if (!isValidFileDrag(e)) {
                        return;
                    }
                    options.onEnter(e);
                }
            });

            disposeSupport.attach(element, "dragleave", function (e) {
                if (!isValidFileDrag(e)) {
                    return;
                }

                options.onLeave(e);

                var relatedTarget = document.elementFromPoint(e.clientX, e.clientY);
                // do not fire when moving a mouse over a descendant
                if (qq(this).contains(relatedTarget)) {
                    return;
                }

                options.onLeaveNotDescendants(e);
            });

            disposeSupport.attach(element, "drop", function (e) {
                if (!isOrSetDropDisabled()) {
                    if (!isValidFileDrag(e)) {
                        return;
                    }

                    e.preventDefault();
                    e.stopPropagation();
                    options.onDrop(e);

                    triggerHidezonesEvent();
                }
            });
        }

        disableDropOutside();
        attachEvents();

        qq.extend(this, {
            dropDisabled: function (isDisabled) {
                return isOrSetDropDisabled(isDisabled);
            },

            dispose: function () {
                disposeSupport.dispose();
            },

            getElement: function () {
                return element;
            }
        });
    };

    /*globals qq, XMLHttpRequest*/
    qq.DeleteFileAjaxRequester = function (o) {
        "use strict";

        var requester,
            options = {
                method: "DELETE",
                uuidParamName: "qquuid",
                endpointStore: {},
                maxConnections: 3,
                customHeaders: function (id) {
                    return {};
                },
                paramsStore: {},
                cors: {
                    expected: false,
                    sendCredentials: false
                },
                log: function (str, level) {
                },
                onDelete: function (id) {
                },
                onDeleteComplete: function (id, xhrOrXdr, isError) {
                }
            };

        qq.extend(options, o);

        function getMandatedParams() {
            if (options.method.toUpperCase() === "POST") {
                return {
                    _method: "DELETE"
                };
            }

            return {};
        }

        requester = qq.extend(this, new qq.AjaxRequester({
            acceptHeader: "application/json",
            validMethods: ["POST", "DELETE"],
            method: options.method,
            endpointStore: options.endpointStore,
            paramsStore: options.paramsStore,
            mandatedParams: getMandatedParams(),
            maxConnections: options.maxConnections,
            customHeaders: function (id) {
                return options.customHeaders.get(id);
            },
            log: options.log,
            onSend: options.onDelete,
            onComplete: options.onDeleteComplete,
            cors: options.cors
        }));

        qq.extend(this, {
            sendDelete: function (id, uuid, additionalMandatedParams) {
                var additionalOptions = additionalMandatedParams || {};

                options.log("Submitting delete file request for " + id);

                if (options.method === "DELETE") {
                    requester.initTransport(id)
                        .withPath(uuid)
                        .withParams(additionalOptions)
                        .send();
                }
                else {
                    additionalOptions[options.uuidParamName] = uuid;
                    requester.initTransport(id)
                        .withParams(additionalOptions)
                        .send();
                }
            }
        });
    };

    /*globals qq */
    qq.Identify = function (fileOrBlob, log) {
        "use strict";

        function isIdentifiable(magicBytes, questionableBytes) {
            var identifiable = false,
                magicBytesEntries = [].concat(magicBytes);

            qq.each(magicBytesEntries, function (idx, magicBytesArrayEntry) {
                if (questionableBytes.indexOf(magicBytesArrayEntry) === 0) {
                    identifiable = true;
                    return false;
                }
            });

            return identifiable;
        }

        qq.extend(this, {
            /**
             * Determines if a Blob can be displayed natively in the current browser.  This is done by reading magic
             * bytes in the beginning of the file, so this is an asynchronous operation.  Before we attempt to read the
             * file, we will examine the blob's type attribute to save CPU cycles.
             *
             * @returns {qq.Promise} Promise that is fulfilled when identification is complete.
             * If successful, the MIME string is passed to the success handler.
             */
            isPreviewable: function () {
                var self = this,
                    identifier = new qq.Promise(),
                    previewable = false,
                    name = fileOrBlob.name === undefined ? "blob" : fileOrBlob.name;

                log(qq.format("Attempting to determine if {} can be rendered in this browser", name));

                log("First pass: check type attribute of blob object.");

                if (this.isPreviewableSync()) {
                    log("Second pass: check for magic bytes in file header.");

                    qq.readBlobToHex(fileOrBlob, 0, 4).then(function (hex) {
                            qq.each(self.PREVIEWABLE_MIME_TYPES, function (mime, bytes) {
                                if (isIdentifiable(bytes, hex)) {
                                    // Safari is the only supported browser that can deal with TIFFs natively,
                                    // so, if this is a TIFF and the UA isn't Safari, declare this file "non-previewable".
                                    if (mime !== "image/tiff" || qq.supportedFeatures.tiffPreviews) {
                                        previewable = true;
                                        identifier.success(mime);
                                    }

                                    return false;
                                }
                            });

                            log(qq.format("'{}' is {} able to be rendered in this browser", name, previewable ? "" : "NOT"));

                            if (!previewable) {
                                identifier.failure();
                            }
                        },
                        function () {
                            log("Error reading file w/ name '" + name + "'.  Not able to be rendered in this browser.");
                            identifier.failure();
                        });
                }
                else {
                    identifier.failure();
                }

                return identifier;
            },

            /**
             * Determines if a Blob can be displayed natively in the current browser.  This is done by checking the
             * blob's type attribute.  This is a synchronous operation, useful for situations where an asynchronous operation
             * would be challenging to support.  Note that the blob's type property is not as accurate as reading the
             * file's magic bytes.
             *
             * @returns {Boolean} true if the blob can be rendered in the current browser
             */
            isPreviewableSync: function () {
                var fileMime = fileOrBlob.type,
                // Assumption: This will only ever be executed in browsers that support `Object.keys`.
                    isRecognizedImage = qq.indexOf(Object.keys(this.PREVIEWABLE_MIME_TYPES), fileMime) >= 0,
                    previewable = false,
                    name = fileOrBlob.name === undefined ? "blob" : fileOrBlob.name;

                if (isRecognizedImage) {
                    if (fileMime === "image/tiff") {
                        previewable = qq.supportedFeatures.tiffPreviews;
                    }
                    else {
                        previewable = true;
                    }
                }

                !previewable && log(name + " is not previewable in this browser per the blob's type attr");

                return previewable;
            }
        });
    };

    qq.Identify.prototype.PREVIEWABLE_MIME_TYPES = {
        "image/jpeg": "ffd8ff",
        "image/gif": "474946",
        "image/png": "89504e",
        "image/bmp": "424d",
        "image/tiff": ["49492a00", "4d4d002a"]
    };

    /*globals qq*/
    /**
     * Attempts to validate an image, wherever possible.
     *
     * @param blob File or Blob representing a user-selecting image.
     * @param log Uses this to post log messages to the console.
     * @constructor
     */
    qq.ImageValidation = function (blob, log) {
        "use strict";

        /**
         * @param limits Object with possible image-related limits to enforce.
         * @returns {boolean} true if at least one of the limits has a non-zero value
         */
        function hasNonZeroLimits(limits) {
            var atLeastOne = false;

            qq.each(limits, function (limit, value) {
                if (value > 0) {
                    atLeastOne = true;
                    return false;
                }
            });

            return atLeastOne;
        }

        /**
         * @returns {qq.Promise} The promise is a failure if we can't obtain the width & height.
         * Otherwise, `success` is called on the returned promise with an object containing
         * `width` and `height` properties.
         */
        function getWidthHeight() {
            var sizeDetermination = new qq.Promise();

            new qq.Identify(blob, log).isPreviewable().then(function () {
                var image = new Image(),
                    url = window.URL && window.URL.createObjectURL ? window.URL :
                          window.webkitURL && window.webkitURL.createObjectURL ? window.webkitURL :
                          null;

                if (url) {
                    image.onerror = function () {
                        log("Cannot determine dimensions for image.  May be too large.", "error");
                        sizeDetermination.failure();
                    };

                    image.onload = function () {
                        sizeDetermination.success({
                            width: this.width,
                            height: this.height
                        });
                    };

                    image.src = url.createObjectURL(blob);
                }
                else {
                    log("No createObjectURL function available to generate image URL!", "error");
                    sizeDetermination.failure();
                }
            }, sizeDetermination.failure);

            return sizeDetermination;
        }

        /**
         *
         * @param limits Object with possible image-related limits to enforce.
         * @param dimensions Object containing `width` & `height` properties for the image to test.
         * @returns {String || undefined} The name of the failing limit.  Undefined if no failing limits.
         */
        function getFailingLimit(limits, dimensions) {
            var failingLimit;

            qq.each(limits, function (limitName, limitValue) {
                if (limitValue > 0) {
                    var limitMatcher = /(max|min)(Width|Height)/.exec(limitName),
                        dimensionPropName = limitMatcher[2].charAt(0).toLowerCase() + limitMatcher[2].slice(1),
                        actualValue = dimensions[dimensionPropName];

                    /*jshint -W015*/
                    switch (limitMatcher[1]) {
                    case "min":
                        if (actualValue < limitValue) {
                            failingLimit = limitName;
                            return false;
                        }
                        break;
                    case "max":
                        if (actualValue > limitValue) {
                            failingLimit = limitName;
                            return false;
                        }
                        break;
                    }
                }
            });

            return failingLimit;
        }

        /**
         * Validate the associated blob.
         *
         * @param limits
         * @returns {qq.Promise} `success` is called on the promise is the image is valid or
         * if the blob is not an image, or if the image is not verifiable.
         * Otherwise, `failure` with the name of the failing limit.
         */
        this.validate = function (limits) {
            var validationEffort = new qq.Promise();

            log("Attempting to validate image.");

            if (hasNonZeroLimits(limits)) {
                getWidthHeight().then(function (dimensions) {
                    var failingLimit = getFailingLimit(limits, dimensions);

                    if (failingLimit) {
                        validationEffort.failure(failingLimit);
                    }
                    else {
                        validationEffort.success();
                    }
                }, validationEffort.success);
            }
            else {
                validationEffort.success();
            }

            return validationEffort;
        };
    };

    /* globals qq */
    /**
     * Module used to control populating the initial list of files.
     *
     * @constructor
     */
    qq.Session = function (spec) {
        "use strict";

        var options = {
            endpoint: null,
            params: {},
            customHeaders: {},
            cors: {},
            addFileRecord: function (sessionData) {
            },
            log: function (message, level) {
            }
        };

        qq.extend(options, spec, true);

        function isJsonResponseValid(response) {
            if (qq.isArray(response)) {
                return true;
            }

            options.log("Session response is not an array.", "error");
        }

        function handleFileItems(fileItems, success, xhrOrXdr, promise) {
            var someItemsIgnored = false;

            success = success && isJsonResponseValid(fileItems);

            if (success) {
                qq.each(fileItems, function (idx, fileItem) {
                    /* jshint eqnull:true */
                    if (fileItem.uuid == null) {
                        someItemsIgnored = true;
                        options.log(qq.format("Session response item {} did not include a valid UUID - ignoring.", idx), "error");
                    }
                    else if (fileItem.name == null) {
                        someItemsIgnored = true;
                        options.log(qq.format("Session response item {} did not include a valid name - ignoring.", idx), "error");
                    }
                    else {
                        try {
                            options.addFileRecord(fileItem);
                            return true;
                        }
                        catch (err) {
                            someItemsIgnored = true;
                            options.log(err.message, "error");
                        }
                    }

                    return false;
                });
            }

            promise[success && !someItemsIgnored ? "success" : "failure"](fileItems, xhrOrXdr);
        }

        // Initiate a call to the server that will be used to populate the initial file list.
        // Returns a `qq.Promise`.
        this.refresh = function () {
            /*jshint indent:false */
            var refreshEffort = new qq.Promise(),
                refreshCompleteCallback = function (response, success, xhrOrXdr) {
                    handleFileItems(response, success, xhrOrXdr, refreshEffort);
                },
                requesterOptions = qq.extend({}, options),
                requester = new qq.SessionAjaxRequester(
                    qq.extend(requesterOptions, {onComplete: refreshCompleteCallback})
                );

            requester.queryServer();

            return refreshEffort;
        };
    };

    /*globals qq, XMLHttpRequest*/
    /**
     * Thin module used to send GET requests to the server, expecting information about session
     * data used to initialize an uploader instance.
     *
     * @param spec Various options used to influence the associated request.
     * @constructor
     */
    qq.SessionAjaxRequester = function (spec) {
        "use strict";

        var requester,
            options = {
                endpoint: null,
                customHeaders: {},
                params: {},
                cors: {
                    expected: false,
                    sendCredentials: false
                },
                onComplete: function (response, success, xhrOrXdr) {
                },
                log: function (str, level) {
                }
            };

        qq.extend(options, spec);

        function onComplete(id, xhrOrXdr, isError) {
            var response = null;

            /* jshint eqnull:true */
            if (xhrOrXdr.responseText != null) {
                try {
                    response = qq.parseJson(xhrOrXdr.responseText);
                }
                catch (err) {
                    options.log("Problem parsing session response: " + err.message, "error");
                    isError = true;
                }
            }

            options.onComplete(response, !isError, xhrOrXdr);
        }

        requester = qq.extend(this, new qq.AjaxRequester({
            acceptHeader: "application/json",
            validMethods: ["GET"],
            method: "GET",
            endpointStore: {
                get: function () {
                    return options.endpoint;
                }
            },
            customHeaders: options.customHeaders,
            log: options.log,
            onComplete: onComplete,
            cors: options.cors
        }));

        qq.extend(this, {
            queryServer: function () {
                var params = qq.extend({}, options.params);

                options.log("Session query request.");

                requester.initTransport("sessionRefresh")
                    .withParams(params)
                    .withCacheBuster()
                    .send();
            }
        });
    };

    /* globals qq */
    /**
     * Module that handles support for existing forms.
     *
     * @param options Options passed from the integrator-supplied options related to form support.
     * @param startUpload Callback to invoke when files "stored" should be uploaded.
     * @param log Proxy for the logger
     * @constructor
     */
    qq.FormSupport = function (options, startUpload, log) {
        "use strict";
        var self = this,
            interceptSubmit = options.interceptSubmit,
            formEl = options.element,
            autoUpload = options.autoUpload;

        // Available on the public API associated with this module.
        qq.extend(this, {
            // To be used by the caller to determine if the endpoint will be determined by some processing
            // that occurs in this module, such as if the form has an action attribute.
            // Ignore if `attachToForm === false`.
            newEndpoint: null,

            // To be used by the caller to determine if auto uploading should be allowed.
            // Ignore if `attachToForm === false`.
            newAutoUpload: autoUpload,

            // true if a form was detected and is being tracked by this module
            attachedToForm: false,

            // Returns an object with names and values for all valid form elements associated with the attached form.
            getFormInputsAsObject: function () {
                /* jshint eqnull:true */
                if (formEl == null) {
                    return null;
                }

                return self._form2Obj(formEl);
            }
        });

        // If the form contains an action attribute, this should be the new upload endpoint.
        function determineNewEndpoint(formEl) {
            if (formEl.getAttribute("action")) {
                self.newEndpoint = formEl.getAttribute("action");
            }
        }

        // Return true only if the form is valid, or if we cannot make this determination.
        // If the form is invalid, ensure invalid field(s) are highlighted in the UI.
        function validateForm(formEl, nativeSubmit) {
            if (formEl.checkValidity && !formEl.checkValidity()) {
                log("Form did not pass validation checks - will not upload.", "error");
                nativeSubmit();
            }
            else {
                return true;
            }
        }

        // Intercept form submit attempts, unless the integrator has told us not to do this.
        function maybeUploadOnSubmit(formEl) {
            var nativeSubmit = formEl.submit;

            // Intercept and squelch submit events.
            qq(formEl).attach("submit", function (event) {
                event = event || window.event;

                if (event.preventDefault) {
                    event.preventDefault();
                }
                else {
                    event.returnValue = false;
                }

                validateForm(formEl, nativeSubmit) && startUpload();
            });

            // The form's `submit()` function may be called instead (i.e. via jQuery.submit()).
            // Intercept that too.
            formEl.submit = function () {
                validateForm(formEl, nativeSubmit) && startUpload();
            };
        }

        // If the element value passed from the uploader is a string, assume it is an element ID - select it.
        // The rest of the code in this module depends on this being an HTMLElement.
        function determineFormEl(formEl) {
            if (formEl) {
                if (qq.isString(formEl)) {
                    formEl = document.getElementById(formEl);
                }

                if (formEl) {
                    log("Attaching to form element.");
                    determineNewEndpoint(formEl);
                    interceptSubmit && maybeUploadOnSubmit(formEl);
                }
            }

            return formEl;
        }

        formEl = determineFormEl(formEl);
        this.attachedToForm = !!formEl;
    };

    qq.extend(qq.FormSupport.prototype, {
        // Converts all relevant form fields to key/value pairs.  This is meant to mimic the data a browser will
        // construct from a given form when the form is submitted.
        _form2Obj: function (form) {
            "use strict";
            var obj = {},
                notIrrelevantType = function (type) {
                    var irrelevantTypes = [
                        "button",
                        "image",
                        "reset",
                        "submit"
                    ];

                    return qq.indexOf(irrelevantTypes, type.toLowerCase()) < 0;
                },
                radioOrCheckbox = function (type) {
                    return qq.indexOf(["checkbox", "radio"], type.toLowerCase()) >= 0;
                },
                ignoreValue = function (el) {
                    if (radioOrCheckbox(el.type) && !el.checked) {
                        return true;
                    }

                    return el.disabled && el.type.toLowerCase() !== "hidden";
                },
                selectValue = function (select) {
                    var value = null;

                    qq.each(qq(select).children(), function (idx, child) {
                        if (child.tagName.toLowerCase() === "option" && child.selected) {
                            value = child.value;
                            return false;
                        }
                    });

                    return value;
                };

            qq.each(form.elements, function (idx, el) {
                if ((qq.isInput(el, true) || el.tagName.toLowerCase() === "textarea") &&
                    notIrrelevantType(el.type) && !ignoreValue(el)) {

                    obj[el.name] = el.value;
                }
                else if (el.tagName.toLowerCase() === "select" && !ignoreValue(el)) {
                    var value = selectValue(el);

                    if (value !== null) {
                        obj[el.name] = value;
                    }
                }
            });

            return obj;
        }
    });

    /* globals qq */
    /**
     * Keeps a running tally of total upload progress for a batch of files.
     *
     * @param callback Invoked when total progress changes, passing calculated total loaded & total size values.
     * @param getSize Function that returns the size of a file given its ID
     * @constructor
     */
    qq.TotalProgress = function (callback, getSize) {
        "use strict";

        var perFileProgress = {},
            totalLoaded = 0,
            totalSize = 0,

            lastLoadedSent = -1,
            lastTotalSent = -1,
            callbackProxy = function (loaded, total) {
                if (loaded !== lastLoadedSent || total !== lastTotalSent) {
                    callback(loaded, total);
                }

                lastLoadedSent = loaded;
                lastTotalSent = total;
            },

            /**
             * @param failed Array of file IDs that have failed
             * @param retryable Array of file IDs that are retryable
             * @returns true if none of the failed files are eligible for retry
             */
            noRetryableFiles = function (failed, retryable) {
                var none = true;

                qq.each(failed, function (idx, failedId) {
                    if (qq.indexOf(retryable, failedId) >= 0) {
                        none = false;
                        return false;
                    }
                });

                return none;
            },

            onCancel = function (id) {
                updateTotalProgress(id, -1, -1);
                delete perFileProgress[id];
            },

            onAllComplete = function (successful, failed, retryable) {
                if (failed.length === 0 || noRetryableFiles(failed, retryable)) {
                    callbackProxy(totalSize, totalSize);
                    this.reset();
                }
            },

            onNew = function (id) {
                var size = getSize(id);

                // We might not know the size yet, such as for blob proxies
                if (size > 0) {
                    updateTotalProgress(id, 0, size);
                    perFileProgress[id] = {loaded: 0, total: size};
                }
            },

            /**
             * Invokes the callback with the current total progress of all files in the batch.  Called whenever it may
             * be appropriate to re-calculate and disseminate this data.
             *
             * @param id ID of a file that has changed in some important way
             * @param newLoaded New loaded value for this file.  -1 if this value should no longer be part of calculations
             * @param newTotal New total size of the file.  -1 if this value should no longer be part of calculations
             */
            updateTotalProgress = function (id, newLoaded, newTotal) {
                var oldLoaded = perFileProgress[id] ? perFileProgress[id].loaded : 0,
                    oldTotal = perFileProgress[id] ? perFileProgress[id].total : 0;

                if (newLoaded === -1 && newTotal === -1) {
                    totalLoaded -= oldLoaded;
                    totalSize -= oldTotal;
                }
                else {
                    if (newLoaded) {
                        totalLoaded += newLoaded - oldLoaded;
                    }
                    if (newTotal) {
                        totalSize += newTotal - oldTotal;
                    }
                }

                callbackProxy(totalLoaded, totalSize);
            };

        qq.extend(this, {
            // Called when a batch of files has completed uploading.
            onAllComplete: onAllComplete,

            // Called when the status of a file has changed.
            onStatusChange: function (id, oldStatus, newStatus) {
                if (newStatus === qq.status.CANCELED || newStatus === qq.status.REJECTED) {
                    onCancel(id);
                }
                else if (newStatus === qq.status.SUBMITTING) {
                    onNew(id);
                }
            },

            // Called whenever the upload progress of an individual file has changed.
            onIndividualProgress: function (id, loaded, total) {
                updateTotalProgress(id, loaded, total);
                perFileProgress[id] = {loaded: loaded, total: total};
            },

            // Called whenever the total size of a file has changed, such as when the size of a generated blob is known.
            onNewSize: function (id) {
                onNew(id);
            },

            reset: function () {
                perFileProgress = {};
                totalLoaded = 0;
                totalSize = 0;
            }
        });
    };

    if (typeof define === 'function' && define.amd) {
        define(function () {
            return qq;
        });
    }
    else if (typeof module !== 'undefined' && module.exports) {
        module.exports = qq;
    }
    else {
        global.qq = qq;
    }
}(window));

/*! 2016-06-16 */
