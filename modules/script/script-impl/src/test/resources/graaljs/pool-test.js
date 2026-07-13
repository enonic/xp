var counter = 0;

exports.block = function (sync) {
    sync.await();
    counter++;
    return counter;
};

exports.inc = function () {
    counter++;
    return counter;
};
