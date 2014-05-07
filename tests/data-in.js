var sinon = require( 'sinon' )
    assert = require( 'assert' ),
    piggie = require( '../index' ),
    Response = process.env.PIGGIE_COVERAGE ? require( '../lib-cov/response' ) : require( '../lib/response' );

describe( 'Data In', function() {
  beforeEach( function() {
    piggie.reset();
  } );


  it( 'should respond when no data is passed in', function() {
    // Setup the handler
    piggie.handle( 'event', function( data, res ) {
      res.send( 'Done' );
    } );

    // Send the request and spy on the response
    var res = new Response( '1' );
    var spy = sinon.spy( res, 'send');
    piggie.send( res, 'event' );
    assert(spy.calledWith( 'Done' ));
  } );


  it( 'should respond when a single JSON string is passed in', function() {
    // Setup the handler
    piggie.handle( 'event', function( data, res ) {
      res.send( data );
    } );

    // Send the request and spy on the response
    var res = new Response( '1' );
    var spy = sinon.spy( res, 'send');
    piggie.send( res, 'event', '\"data goes in\"' );
    assert(spy.calledWith( 'data goes in' ));
  } );


  it( 'should respond when a single JSON number is passed in', function() {
    // Setup the handler
    piggie.handle( 'event', function( data, res ) {
      res.send( data );
    } );

    // Send the request and spy on the response
    var res = new Response( '1' );
    var spy = sinon.spy( res, 'send');
    piggie.send( res, 'event', '100' );
    assert(spy.calledWith( 100 ));
  } );


  it( 'should respond when a JSON object is passed in', function() {
    // Setup the handler
    piggie.handle( 'event', function( data, res ) {
      res.send( data.name );
    } );

    // Send the request and spy on the response
    var res = new Response( '1' );
    var spy = sinon.spy( res, 'send');
    piggie.send( res, 'event', '{\"name\":\"Big Jeff\"}' );
    assert(spy.calledWith( 'Big Jeff' ));
  } );
} );
