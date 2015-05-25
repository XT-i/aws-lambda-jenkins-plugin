console.log('Loading function');

exports.handler = function(event, context) {
    console.log('value1 =', event.key1);
    if(event.key1 == 'moon'){
        context.fail('event_fail');
    }else{
        event.key1 = 'hello ' + event.key1;
        context.succeed(event);  
    }
};