var global = global || {},
    window = window || {},
    noop = function noop () {};

var nativeInterface = window.android || global.ios || {
  event: noop,
  fail: noop,
  success: noop
};

// Exports
module.exports = nativeInterface;
