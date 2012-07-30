var intradayStyleList = new Array();
intradayStyleList[0] = new Array('Line');
intradayStyleList[1] = new Array('line');

var styleList = new Array();
styleList[0] = new Array('Line', 'HLC', 'OHLC', 'Candlestick');
styleList[1] = new Array('line', 'hlc', 'ohlc', 'candlestick');

// This function goes through the options for the given
// drop down box and removes them in preparation for
// a new set of values
function emptyList(box) {
    while (box.options.length) {
        box.options[0] = null;
    }
}

// This function assigns new drop down options to the given
// drop down box from the list of lists specified.
// arr[0] holds the display text
// arr[1] are the values
function fillList(box, arr) {
    for (i = 0; i < arr[0].length; i++) {
        option = new Option(arr[0][i], arr[1][i]);
        box.options[box.length] = option;
    }
    box.selectedIndex = 0;
}

// This function performs a drop down list option change by first
// emptying the existing option list and then assigning a new set
// Isolate the appropriate list by using the value
// of the currently selected option
function changeList(box) {
    var list;
    if (box.options[box.selectedIndex].value == 'intraday') {
        if (box.form.style.length == 1) {
            return;
        }
        list = intradayStyleList;
    } else {
        if (box.form.style.length == 4) {
            return;
        }
        list = styleList;
    }
    emptyList(box.form.style);
    fillList(box.form.style, list);
}
