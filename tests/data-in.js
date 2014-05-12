var sinon = require( 'sinon' ),
    assert = require( 'assert' ),
    piggie = require( '../index' ),
    Response = process.env.PIGGIE_COVERAGE ? require( '../lib-cov/response' ) : require( '../lib/response' );

describe( 'Data in', function() {

  beforeEach( function () {
    piggie.reset();
  } );


  it( 'should call the handler when no data is passed in', function () {
    var dummyResponse = new Response( 'dummy-key' );

    // Setup the handler
    piggie.register( 'event', function ( data, response ) {
      assert( true );
    } );

    // Send the request
    piggie.execute( dummyResponse, 'event' );

  } );


  it( 'should call the handler when a JSON string is passed in', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        dummyData = 'data goes in';

    // Setup the handler
    piggie.register( 'event', function ( data, response ) {
      assert.strictEqual( data, dummyData );
    } );

    // Send the request
    piggie.execute( dummyResponse, 'event', JSON.stringify( dummyData ) );

  } );


  it( 'should call the handler when a JSON number is passed in', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        dummyData = 100;

    // Setup the handler
    piggie.register( 'event', function ( data, response ) {
      assert.strictEqual( data, dummyData );
    } );

    // Send the request
    piggie.execute( dummyResponse, 'event', JSON.stringify( dummyData ) );

  } );

  it( 'should call the handler when a JSON boolean is passed in', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        dummyData = true;

    // Setup the handler
    piggie.register( 'event', function ( data, response ) {
      assert.strictEqual( data, dummyData );
    } );

    // Send the request
    piggie.execute( dummyResponse, 'event', JSON.stringify( dummyData ) );

  } );


  it( 'should call the handler when a JSON object is passed in', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        dummyData = {
          name: 'Big Jeff'
        };

    // Setup the handler
    piggie.register( 'event', function ( data, response ) {
      assert.deepEqual( data, dummyData );
    } );

    // Send the request
    piggie.execute( dummyResponse, 'event', JSON.stringify( dummyData ) );

  } );


  it( 'should call the handler when a JSON array is passed in', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        dummyData = [
          'Big Jeff',
          'Bobbert'
        ];

    // Setup the handler
    piggie.register( 'event', function ( data, response ) {
      assert.deepEqual( data, dummyData );
    } );

    // Send the request
    piggie.execute( dummyResponse, 'event', JSON.stringify( dummyData ) );

  } );


  it( 'should call the handlers response fail callback when an invalid JSON string is passed in', function () {
      var dummyResponse = new Response( 'dummy-key' ),
        responseFailSpy = sinon.spy( dummyResponse, 'fail' ),
        dummyData = '"name:"Big Jeff"}';

    // Setup the handler
    piggie.register( 'event', function ( data, response ) { } );

    // Send the request and spy on the fail callback
    piggie.execute( dummyResponse, 'event', dummyData );
    assert( responseFailSpy.called );

  } );

} );
