var recorder = require('/test/recorder');

__.disposer(function () {
    recorder.incrementAndGet();
});
