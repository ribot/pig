/**
 * Response constructor
 */
var Response = function Response ( key ) {
  this.key = key;
  return this;
};

Response.prototype = {

  /**
   * Native party failure callback
   */
  fail: function fail ( error, code ) {
    if ( typeof window != "undefined" && window.android ) {
      window.android.fail( this.key, ( code || null ), error.name, error.message );
    }
  },

  /**
   * Native party success callback
   */
  success: function success ( data ) {
    if ( typeof window != "undefined" && window.android ) {
      window.android.success( this.key, data );
    }
  }

};

// Exports
module.exports = Response;
