var sinon = require( 'sinon' ),
    assert = require( 'assert' ),
    piggie = require( '../index' ),
    Response = process.env.PIGGIE_COVERAGE ? require( '../lib-cov/response' ) : require( '../lib/response' );

describe( 'Handler', function() {

  beforeEach( function() {
    piggie._reset();

    window = {
      android: {
        fail: function() {},
        success: function() {},
        event: function() {}
      }
    };
  } );


  it( 'should error when path has not been registered', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        responseFailSpy = sinon.spy( dummyResponse, 'fail' );

    // Test the handler calls the fail callback
    piggie._execute( dummyResponse, 'event' );
    assert( responseFailSpy.called );

  } );


  it( 'should error when attempting to register a path twice', function () {

    // Setup the first handler
    piggie.register( 'event', function ( data, response ) { } );

    assert.throws( function () {
      // Attempt to setup the second handler
      piggie.register( 'event', function ( data, response ) { } );
    } );

  } );

} );
