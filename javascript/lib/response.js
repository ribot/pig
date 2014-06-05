/**
 * Response constructor
 */
var Response = function Response( key ) {
  this.key = key;
  return this;
};

Response.prototype = {

  /**
   * Native party failure callback
   */
  fail: function fail( error, code ) {
    // Shortcut if there is no callback on the native side
    if ( this.key === -1 ) return;

    // Android
    if ( typeof window != "undefined" && window.android ) {
      window.android.fail( this.key, ( code || null ), error.name, error.message );
    }
    // iOS
    // TODO
  },

  /**
   * Native party success callback
   */
  success: function success( data ) {
    // Shortcut if there is no callback on the native side
    if ( this.key === -1 ) return;

    // Android
    if ( typeof window != "undefined" && window.android ) {
      window.android.success( this.key, JSON.stringify( data ) );
    }
    // iOS
    // TODO
  }

};

// Exports
module.exports = Response;
