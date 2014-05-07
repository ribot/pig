module.exports = function Response( key ) {
  this.error = function error( error ) {
    if ( typeof window != "undefined" && window.android ) {
      window.android.reply( key, error, null );
    }
  };

  this.send = function send( response ) {
    if ( typeof window != "undefined" && window.android ) {
      window.android.reply( key, null, response );
    }
  };
};
