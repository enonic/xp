var counter = 0;

exports.inc = function () {
    counter++;
    return counter;
};

exports.mkListener = function () {
    return function () {
        counter++;
        return counter;
    };
};
