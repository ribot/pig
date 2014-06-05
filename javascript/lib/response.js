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

    if ( this.key != -1 ) {
      // Android
      if ( typeof window != "undefined" && window.android ) {
        window.android.fail( this.key, ( code || null ), error.name, error.message );
      }
      // iOS
      // TODO
    }

    preventRecurrence.call( this );

  },

  /**
   * Native party success callback
   */
  success: function success( data ) {

    if ( this.key != -1 ) {
      // Android
      if ( typeof window != "undefined" && window.android ) {
        window.android.success( this.key, JSON.stringify( data ) );
      }
      // iOS
      // TODO
    }

    preventRecurrence.call( this );

  }

};

/**
 * Replaces the success and fail function in this response object with a
 * function which throws and Error.
 **/
function preventRecurrence () {
  this.fail = this.success = function () {
    throw new Error( 'Cannot respond more than once using the same response object.' );
  }
}

// Exports
module.exports = Response;
