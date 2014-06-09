var _global = ( typeof window != 'undefined' ? window : undefined ) || global || {},
    noop = function noop () {};

var nativeInterface = _global.android || _global.ios || {

  /**
   * Invokes `event` in native code
   *
   * @param {String} callback id
   * @param {String} json data
   */
  event: noop,

  /**
   * Invokes `fail` in native code
   *
   * @param {String} callback id
   * @param {String} error code
   * @param {String} error name
   * @param {String} error message
   */
  fail: noop,

  /**
   * Invokes `success` in native code
   *
   * @param {String} event name
   * @param {String} json data
   */
  success: noop

};

// Exports
module.exports = nativeInterface;
