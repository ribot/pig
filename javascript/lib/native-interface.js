var global = global || {};
var window = window || {};

var nativeInterface = window.android || global.ios || {
  event: console.log,
  fail: console.log,
  success: console.log
};

// Exports
module.exports = nativeInterface;
