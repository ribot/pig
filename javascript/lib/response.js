// Dependencies
var nativeInterface = require( './native-interface' );

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
    error = error || new Error();
    code = code || '';

    if ( this.key !== -1 ) {
      nativeInterface.fail( this.key, code, error.name, error.message );
    }

    _preventRecurrence.call( this );

  },

  /**
   * Native party success callback
   */
  success: function success( data ) {
    var json = ( data ? JSON.stringify( data ) : '' );

    if ( this.key !== -1 ) {
      nativeInterface.success( this.key, json );
    }

    _preventRecurrence.call( this );

  }

};

/**
 * Replaces the success and fail function in this response object with a
 * function which throws and Error.
 */
function _preventRecurrence() {

  this.fail = this.success = function recurrenceHandler() {
    throw new Error( 'Cannot respond more than once using the same response object.' );
  };

}

// Exports
module.exports = Response;
