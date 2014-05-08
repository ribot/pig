/**
 * Response constructor
 */
var Response = function Response ( key ) {
  return this;
};

Response.prototype = {

  error: function error ( message ) {
    if ( typeof window != "undefined" && window.android ) {
      window.android.reply( key, message, null );
    }
  },

  send: function send ( response ) {
    if ( typeof window != "undefined" && window.android ) {
      window.android.reply( key, null, response );
    }
  }

};

// Exports
module.exports = Response;
