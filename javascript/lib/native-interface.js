var noop = function noop () {};

var nativeInterface = {

  /**
   * Invokes `event` in native code
   *
   * @param {String} callback id
   * @param {String} json data
   */
  event: function event( name, data ) {
    getNativeInterface().event( name, data );
  },

  /**
   * Invokes `fail` in native code
   *
   * @param {String} callback id
   * @param {String} error code
   * @param {String} error name
   * @param {String} error message
   */
  fail: function fail( callbackId, code, name, message ) {
    getNativeInterface().fail( callbackId, code, name, message );
  },

  /**
   * Invokes `success` in native code
   *
   * @param {String} event name
   * @param {String} json data
   */
  success: function success( callbackId, data ) {
    getNativeInterface().success( callbackId, data );
  }

};

var getNativeInterface = function() {
  if ( typeof window != 'undefined' ) {
    if ( window.android ) {
      return window.android;

    } else if ( window.ios ) {
      return window.ios;
    }
  }

  return {
    event: noop,
    fail: noop,
    success: noop
  }
};


// Exports
module.exports = nativeInterface;
