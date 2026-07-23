var recorder = require('/test/recorder');

__.disposer(function () {
    throw new Error('first disposer fails');
});

__.disposer(function () {
    recorder.incrementAndGet();
});
