module.exports = function Response( callback ) {
  this.error = function error( error ) {
    callback( error );
  };

  this.send = function send( response ) {
    callback( null, response );
  };
};
