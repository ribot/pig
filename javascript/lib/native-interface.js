var global = global || {};

var nativeInterface = global.android || global.ios || {
  event: console.log,
  fail: console.log,
  success: console.log
};

// Exports
module.exports = nativeInterface;
